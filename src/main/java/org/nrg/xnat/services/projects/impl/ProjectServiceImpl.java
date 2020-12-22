package org.nrg.xnat.services.projects.impl;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.ArcProject;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.base.BaseXnatProjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFTItem;
import org.nrg.xft.db.MaterializedView;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.XftItemEventI;
import org.nrg.xft.event.EventUtils.TYPE;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.InvalidPermissionException;
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xft.utils.XftStringUtils;
import org.nrg.xnat.model.util.XnatProjectUtil;
import org.nrg.xnat.services.archive.impl.legacy.AbstractXftServiceImpl;
import org.nrg.xnat.services.projects.ProjectService;
import org.nrg.xnat.turbine.utils.ArcSpecManager;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.nrg.xnat.utils.WorkflowUtils;
import org.restlet.data.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import static org.nrg.xdat.om.base.auto.AutoXnatProjectdata.SCHEMA_ELEMENT_NAME;
import static org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST;
import static org.restlet.data.Status.CLIENT_ERROR_CONFLICT;
import static org.restlet.data.Status.CLIENT_ERROR_EXPECTATION_FAILED;
import static org.restlet.data.Status.CLIENT_ERROR_FORBIDDEN;
import static org.restlet.data.Status.SERVER_ERROR_INTERNAL;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ProjectServiceImpl extends AbstractXftServiceImpl implements ProjectService {
    @Autowired
    public ProjectServiceImpl(final NamedParameterJdbcTemplate template) {
        super(template);
    }

    @Override
    public List<XnatProjectdata> getAll(final UserI user) {
        return XnatProjectdata.getAllXnatProjectdatas(user, false);
    }

    @Override
    public XnatProjectdata findById(final UserI user, final String projectId) {
        if (Objects.nonNull(projectId)) {
            return XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
        } else {
            throw new NullPointerException("ProjectId is Null");
        }
    }

    @Override
    public XnatProjectdata create(final UserI user, final XnatProjectdata project) {
        log.debug("User {} is creating the project {}", user.getUsername(), project.getId());
        XFTItem item;
        try {
            item = project.getItem();
            	
            if (item == null) {
                String xsiType = this.getQueryVariable("xsiType");
                if (xsiType != null) {
                    item = XFTItem.NewItem(xsiType, user);
                }
            }
            if (item == null) 
            	throw new DataFormatException("Need POST Contents");
           
            boolean allowDataDeletion = false;
            if (this.getQueryVariable("allowDataDeletion") != null && this.getQueryVariable("allowDataDeletion").equalsIgnoreCase("true")) {
                allowDataDeletion = true;
            }
            
            if (item.instanceOf("xnat:projectData")) {
                XnatProjectdata proj = new XnatProjectdata(item);

                if (StringUtils.isBlank(proj.getId()))
                	throw new DataFormatException("Requires XNAT ProjectData ID");
                
                if (!XftStringUtils.isValidId(proj.getId())) 
                	throw new DataFormatException("Invalid character in project ID.");

                if (item.getCurrentDBVersion() == null) {
                    if (XDAT.getSiteConfigPreferences().getUiAllowNonAdminProjectCreation() || Roles.isSiteAdmin(user)) {
                        final XnatProjectdata saved = BaseXnatProjectdata.createProject(proj, user, allowDataDeletion, false, newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN), getQueryVariable("accessibility"));
                        //returnSuccessfulCreateFromList(saved.getId());
                    } else  
                    	throw new InsufficientPrivilegesException("User account doesn't have permission to edit this project.");
                } else  
                	throw new ResourceAlreadyExistsException("Project already exists.", proj.getId());
            }
            
        }catch (Exception e) {
			// TODO: handle exception
		}
		return XnatProjectdata.getXnatProjectdatasById(project.getId(), user, false);
    }
    
    
    @SuppressWarnings("unused")
	@Override
    public XnatProjectdata update(final UserI user, final XnatProjectdata proj) throws Exception {
    	XnatProjectUtil xnatProjectUtil = new XnatProjectUtil();
    	XnatProjectdata project = null;
    	  final String projectId = proj.getId();
   	 String filepath = null;
   	project = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
    	 if (user.isGuest()) {
    		 throw new InsufficientPrivilegesException("User has insufficent privileges");
         }

         // Project equal to null means a new project, so either non-admins must be able to create projects or the user must be an admin.
         if (project == null && !XDAT.getSiteConfigPreferences().getUiAllowNonAdminProjectCreation() && !Roles.isSiteAdmin(user)) {
        	 throw new InsufficientPrivilegesException("User " + user.getUsername() + " doesn't have permission to create projects on this system");
         }
         // All file path settings require an existing project, so if there's a file path and no project, that's bad, m'kay?
         final boolean hasFilePath = StringUtils.isNotBlank(filepath);
         if (hasFilePath && project == null) {
        	 throw new DataFormatException("You can't set the '" + StringUtils.substringBefore(filepath, "/") + "' attribute without specifying the project on which you want to set it.");
         }
         // If we do have a project, we can go ahead and check permissions to edit it now before we go any farther.
         if (project != null && !Permissions.canEditProject(user, projectId)) {
        	 throw new InsufficientPrivilegesException("User " + user.getUsername() + " doesn't have permission to edit the project " + project.getId());
         }

         try {
             if (project == null || Permissions.canEdit(user, project)) {
                 XFTItem item = getProjectXftItem(user, project);

                 if (item == null) {
                	 throw new DataFormatException("Need PUT Contents");
                 }

                 final boolean allowDataDeletion = BooleanUtils.toBoolean(getQueryVariable("allowDataDeletion"));
                 if (item.instanceOf("xnat:projectData")) {
                     XnatProjectdata workingProject = new XnatProjectdata(item);

                     if (hasFilePath) {
                         if (StringUtils.isBlank(workingProject.getId())) {
                             item = project.getItem();
                             workingProject = project;
                         }

                         if (!Permissions.canEdit(user, item)) {
                        	 throw new InsufficientPrivilegesException("User account doesn't have permission to edit this project.");
                         }
                         if (filepath.startsWith("quarantine_code/")) {
                             final String quarantineCode = StringUtils.removeStart(filepath, "quarantine_code/");
                             if (StringUtils.isNotBlank(quarantineCode)) {
                                 final ArcProject arcProject = workingProject.getArcSpecification();
                                 arcProject.setQuarantineCode(translateArcProjectCode(quarantineCode));
                                 create(workingProject, arcProject, false, false, newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, "Configured quarantine code"),user);
                                 ArcSpecManager.Reset();
                             }
                         } else if (filepath.startsWith("prearchive_code/")) {
                             final String prearchiveCode = StringUtils.removeStart(filepath, "prearchive_code/");
                             if (StringUtils.isNotBlank(prearchiveCode)) {
                                 if (XDAT.getBoolSiteConfigurationProperty("project.allow-auto-archive", true) || StringUtils.equals(prearchiveCode, "0")) {
                                     final ArcProject arcProject = workingProject.getArcSpecification();
                                     arcProject.setPrearchiveCode(translateArcProjectCode(prearchiveCode));
                                     create(workingProject, arcProject, false, false, newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, "Configured prearchive code"),user);
                                     ArcSpecManager.Reset();
                                 } else {
                                	 throw new InsufficientPrivilegesException("");
                                 }
                             }
                         } else if (filepath.startsWith("current_arc/")) {
                             final String currentArc = StringUtils.removeStart(filepath, "current_arc/");
                             if (StringUtils.isNotBlank(currentArc)) {
                                 final ArcProject arcProject = workingProject.getArcSpecification();
                                 arcProject.setCurrentArc(currentArc);
                                 create(workingProject, arcProject, false, false, newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, "Configured current arc"), user);
                                 ArcSpecManager.Reset();
                             }
                         } else if (filepath.startsWith("scan_type_mapping/")) {
                             final String scanTypeMapping = StringUtils.removeStart(filepath, "scan_type_mapping/");
                             workingProject.setUseScanTypeMapping(BooleanUtils.toBoolean(scanTypeMapping));
                             xnatProjectUtil.update(workingProject, false, false, newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, "Configured current arc"),user);
                             ArcSpecManager.Reset();
                         } else {
                        	 throw new DataFormatException("request data is missing");
                         }
                     } else {
                         if (StringUtils.isBlank(workingProject.getId())) {
                             workingProject.setId(projectId);
                         } else if (!StringUtils.equalsIgnoreCase(projectId, workingProject.getId())) {
                        	 throw new DataFormatException("The project ID for the REST call must match the value in submitted request body.");
                         }

                         if (!XftStringUtils.isValidId(workingProject.getId()) && !isQueryVariableTrue("testHyphen")) {
                        	 throw new DataFormatException("Invalid character in project ID.");
                         }

                         if (item.getCurrentDBVersion() != null) {
                             if (!Permissions.canEdit(user, item)) {
                            	 throw new InsufficientPrivilegesException("User account doesn't have permission to edit this project.");
                             }
                         } else {
                             final Long count = XDAT.getContextService().getBean(NamedParameterJdbcTemplate.class).queryForObject("SELECT COUNT(id) FROM xnat_projectdata_history WHERE id = :projectId", new MapSqlParameterSource("projectId", projectId), Long.class);
                             if (count > 0) {
                            	 throw new InsufficientPrivilegesException("Project '" + project.getId() + "' was used in a previously deleted project and cannot be reused.");
                             }
                         }

                         // Validate project fields.  If there are conflicts, build a error message and display it to the user.
                         final Collection<String> conflicts = workingProject.validateProjectFields();
                         if (!conflicts.isEmpty()) {
                        	 throw new ResourceAlreadyExistsException("Requested new project conflicts with existing projects: " + StringUtils.join(conflicts, ", "), "");
                         }

                         final String accessibility = getQueryVariable("accessibility");
                         if (project == null) {
                             BaseXnatProjectdata.createProject(workingProject, user, allowDataDeletion, true, newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN), accessibility);
                         } else {
                             SaveItemHelper.authorizedSave(item, user, false, false, newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN));
                             if (StringUtils.isNotBlank(accessibility) && !StringUtils.equals(workingProject.getPublicAccessibility(), accessibility)) {
                                 // If we don't allow non private projects, we shouldn't allow accessibility to change. 
                                 final boolean nonPrivateAllowed = XDAT.getBoolSiteConfigurationProperty("securityAllowNonPrivateProjects", true);
                                 if(!nonPrivateAllowed) {
                                     log.debug("Unable to change project accessibility because securityAllowNonPrivateProjects is set to " + String.valueOf(nonPrivateAllowed));
                                     log.debug("Non-private projects are not allowed. Update siteConfig preference if you wish to allow non-private projects.");
                                     throw new InsufficientPrivilegesException("Non-private projects are not allowed. Update siteConfig preference if you wish to allow non-private projects.");
                                 }
                                 
                                 final PersistentWorkflowI workflow = WorkflowUtils.buildProjectWorkflow(user, project, newEventInstance(EventUtils.CATEGORY.PROJECT_ACCESS, EventUtils.MODIFY_PROJECT_ACCESS));
                                 Permissions.setDefaultAccessibility(workingProject.getId(), accessibility, false, user, workflow.buildEvent());
                             }
                             XDAT.triggerXftItemEvent(XnatProjectdata.SCHEMA_ELEMENT_NAME, projectId, XftItemEventI.UPDATE);
                         }
                     }
                 }
             } else {
            	 throw new InsufficientPrivilegesException("User account doesn't have permission to edit this project.");
             }
         } catch (ActionException e) {
        	 throw new Exception(e.getMessage());
         } catch (InvalidPermissionException | IllegalArgumentException e) {
        	 throw new InsufficientPrivilegesException(e.getMessage());
         } catch (Exception e) {
             log.error("Unknown exception type", e);
             throw new InitializationException("Something went wrong");
         }
		return  XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
    }
    
    private boolean isQueryVariableTrue(String string) {
		return false;
	}

	public boolean create(final ArchivableItem parent, final ItemI sub, final boolean overwriteSecurity, final boolean allowDataDeletion, final EventDetails event, UserI user) throws Exception {
        final PersistentWorkflowI workflow = WorkflowUtils.getOrCreateWorkflowData(getEventId(), user, parent.getItem(), event);
        final EventMetaI          meta     = workflow.buildEvent();

        try {
            if (SaveItemHelper.authorizedSave(sub, user, false, false, meta)) {
                WorkflowUtils.complete(workflow, meta);
                Users.clearCache(user);
                MaterializedView.deleteByUser(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            WorkflowUtils.fail(workflow, meta);
            throw e;
        }
    }
    
    public Integer getEventId() {
        final String id = getQueryVariable(EventUtils.EVENT_ID);
        if (id != null) {
            return Integer.valueOf(id);
        } else {
            return null;
        }
    }
    
    
    public EventDetails newEventInstance(EventUtils.CATEGORY cat, String action) {
        return EventUtils.newEventInstance(cat, getEventType(), (getAction() != null) ? getAction() : action, getReason(), getComment());
    }
    
    
    private int translateArcProjectCode(final String code) throws ClientException {
        if (NumberUtils.isCreatable(code)) {
            return NumberUtils.createInteger(code);
        }
        switch (code) {
            case "true":
                return 1;
            case "false":
                return 0;
            default:
                throw new ClientException( "The submitted code " + code + " is invalid: must be an integer.");
        }
    }
    
    protected XFTItem getProjectXftItem(final UserI user, XnatProjectdata project) throws ClientException, ServerException, XFTInitException, ElementNotFoundException {
        XFTItem item = project.getItem();

        if (item == null) {
            String xsiType = getQueryVariable("xsiType");
            if (xsiType != null) {
                item = XFTItem.NewItem(xsiType, user);
            }
        }

        if (item == null) {
            if (project != null) {
                item = project.getItem();
            }
        }
        return item;
    }

    @Override
    public void deleteById(final UserI user, final String projectId) {
        delete(user, findById(user, projectId));
    }

    @Override
    public void delete(final UserI user, final XnatProjectdata proj) {
    	XnatProjectdata project = null;
  	  final String projectId = proj.getId();
 	 String filepath = null;
 	project = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
        log.debug("User {} is deleting the project {}", user.getUsername(), project.getId());
        if (project == null || StringUtils.isNotBlank(filepath)) {
           // getResponse().setStatus(CLIENT_ERROR_BAD_REQUEST);
            //return;
        }
       // final UserI user = getUser();
        try {
            if (user.isGuest() || !Permissions.canDelete(user, project)) {
                //getResponse().setStatus(CLIENT_ERROR_FORBIDDEN);
               // return;
            }
        } catch (Exception e) {
            log.error("An error occurred checking permissions for user " + user.getUsername() + " to delete the project " + projectId, e);
           // getResponse().setStatus(SERVER_ERROR_INTERNAL);
           // return;
        }

        try {
            final PersistentWorkflowI workflow = WorkflowUtils.getOrCreateWorkflowData(getEventId(), user, SCHEMA_ELEMENT_NAME, projectId, projectId, newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, EventUtils.getDeleteAction(XnatProjectdata.SCHEMA_ELEMENT_NAME)));
            final EventMetaI          event    = workflow.buildEvent();

            try {
                project.delete(isQueryVariableTrue("removeFiles"), user, event);
                PersistentWorkflowUtils.complete(workflow, event);
                return;
            } catch (Exception e) {
                log.error("An error occurred when user " + user.getUsername() + " tried to delete the project " + projectId, e);
                PersistentWorkflowUtils.fail(workflow, event);
            }
        } catch (Exception e) {
            log.error("An error occurred trying manage delete operation for user " + user.getUsername() + " on project " + projectId, e);
        }
        // If we got here, the delete operation failed, so the server error status should always be set.
       // getResponse().setStatus(SERVER_ERROR_INTERNAL);
    }
    
    

    private EventDetails newEventInstance(EventUtils.CATEGORY cat) {
    	return EventUtils.newEventInstance(cat, getEventType(), getAction(), getReason(), getComment());
	}

	private String getComment() {
		return null;
	}

	private String getReason() {
		return null;
	}

	private String getAction() {
		return null;
	}

	private TYPE getEventType() {
		 final String id = null;
				 //getQueryVariable(EventUtils.EVENT_TYPE);
	        if (id != null) {
	            return EventUtils.getType(id, EventUtils.TYPE.WEB_SERVICE);
	        } else {
	            return EventUtils.TYPE.WEB_SERVICE;
	        }
	}

	private String getQueryVariable(String string) {
		return null;
	}

}
