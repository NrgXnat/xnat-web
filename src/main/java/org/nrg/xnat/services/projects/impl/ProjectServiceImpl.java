package org.nrg.xnat.services.projects.impl;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.ArcProject;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.base.BaseXnatProjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
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
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xft.exception.XftItemException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xft.utils.XftStringUtils;
import org.nrg.xnat.model.util.SecureResoureUtil;
import org.nrg.xnat.services.projects.ProjectService;
import org.nrg.xnat.turbine.utils.ArcSpecManager;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.nrg.xnat.utils.WorkflowUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import static org.nrg.xdat.om.base.auto.AutoXnatProjectdata.SCHEMA_ELEMENT_NAME;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService {
	
    @Override
    public Optional<List<XnatProjectdata>> findAll(final UserI user) throws NotFoundException {
    	List<XnatProjectdata> projects= XnatProjectdata.getAllXnatProjectdatas(user, false);
    	if(Objects.isNull(projects) || projects.isEmpty())
    		throw new  NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME) ;
    	return Optional.of(projects);
    }

    @Override
    public  Optional<XnatProjectdata> findById(final UserI user, final String projectId) throws DataFormatException, NotFoundException {
    	if(Objects.isNull(projectId))
    		throw new DataFormatException("The requested projectId wasn't found ");
    	XnatProjectdata proj = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
    	if(Objects.isNull(proj))
    		throw new  NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME, projectId) ;
    	return Optional.of(proj);
    }

    @Override
    public XnatProjectdata create(final UserI user, final XnatProjectdata proj, String allowDataDelete, String accessibility, String xsiType ) throws ActionException, UserNotFoundException, UserInitException, DataFormatException, XftItemException, InsufficientPrivilegesException, ResourceAlreadyExistsException {
		log.debug("User {} is creating the project {}", user.getUsername(), proj.getId());

		XFTItem item;

		// step 1: get XFTItem from project request
		item = getProjectXftItem(user, proj, xsiType);

		// step 2: Set user into XFTItem
		item.setUser(user);

		boolean allowDataDeletion = false;
		if (Objects.nonNull(allowDataDelete) && allowDataDelete.equalsIgnoreCase("true"))
			allowDataDeletion = true;

		if (item.instanceOf("xnat:projectData")) {
			XnatProjectdata project = new XnatProjectdata(item);
			if (StringUtils.isBlank(project.getId()))
				throw new DataFormatException("Requires XNAT ProjectData ID");

			if (!XftStringUtils.isValidId(project.getId()))
				throw new DataFormatException("Invalid character in project ID.");

			if (item.getCurrentDBVersion() == null) {
				if (XDAT.getSiteConfigPreferences().getUiAllowNonAdminProjectCreation() || Roles.isSiteAdmin(user)) {
					BaseXnatProjectdata.createProject(project, user, allowDataDeletion, false, newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN), accessibility);
				} else
					throw new InsufficientPrivilegesException( "User account doesn't have permission to edit this project.");
			} else
				throw new ResourceAlreadyExistsException("Project already exists.", proj.getId());
		}
		return XnatProjectdata.getXnatProjectdatasById(proj.getId(), user, false);
	}
    
    
	@SuppressWarnings("unused")
	@Override
    public XnatProjectdata update(final UserI user, final XnatProjectdata project, String filepath, String allowDataDelete, String accessibility, Boolean testHyphen,  String xsiType ) throws Exception {
		log.debug("User {} is updating the project  Id {} ", user.getUsername(), project.getId());
		final String projectId = project.getId();

		if (user.isGuest())
			throw new InsufficientPrivilegesException("User " + user.getUsername() + " doesn't have permission to edit projects on this system");

		// Project equal to null means a new project, so either non-admins must be able
		// to create projects or the user must be an admin.
		if (project == null && !XDAT.getSiteConfigPreferences().getUiAllowNonAdminProjectCreation() && !Roles.isSiteAdmin(user))
			throw new InsufficientPrivilegesException( "User " + user.getUsername() + " doesn't have permission to create projects on this system");

		// All file path settings require an existing project, so if there's a file path  and no project, that's bad, m'kay?
		final boolean hasFilePath = StringUtils.isNotBlank(filepath);
		if (hasFilePath && project == null)
			throw new DataFormatException("You can't set the '" + StringUtils.substringBefore(filepath, "/") + "' attribute without specifying the project on which you want to set it.");

		// If we do have a project, we can go ahead and check permissions to edit it now
		// before we go any farther.
		if (project != null && !Permissions.canEditProject(user, projectId))
			throw new InsufficientPrivilegesException( "User " + user.getUsername() + " doesn't have permission to edit the project " + project.getId());

		if (project == null || Permissions.canEdit(user, project)) {
			XFTItem item = getProjectXftItem(user, project, xsiType);
			item.setUser(user);

			final boolean allowDataDeletion = BooleanUtils.toBoolean(allowDataDelete);
			if (item.instanceOf("xnat:projectData")) {
				XnatProjectdata workingProject = new XnatProjectdata(item);

				if (hasFilePath) {
					if (StringUtils.isBlank(workingProject.getId())) {
						item = project.getItem();
						workingProject = project;
					}
					if (!Permissions.canEdit(user, item))
						throw new InsufficientPrivilegesException("User account doesn't have permission to edit this project.");

					workingProject = verifyFilePathAndCreateorUpdateXnatProject(filepath, workingProject, user);
				} else
					return saveXnatProject(workingProject, projectId, user, item, allowDataDeletion, project,accessibility, testHyphen);
			}
		} else
			throw new InsufficientPrivilegesException("User account doesn't have permission to edit this project.");
		return XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
	}
    
    private XnatProjectdata saveXnatProject(XnatProjectdata workingProject, String projectId, UserI user, XFTItem item, boolean allowDataDeletion, XnatProjectdata project, String accessibility, Boolean testHyphen) throws Exception {
    	if (StringUtils.isBlank(workingProject.getId()))
    		workingProject.setId(projectId);
         else if (!StringUtils.equalsIgnoreCase(projectId, workingProject.getId())) 
        	 throw new DataFormatException("The project ID for the REST call must match the value in submitted request body.");
        
        if (!XftStringUtils.isValidId(workingProject.getId()) && !testHyphen) 
       	 throw new DataFormatException("Invalid character in project ID.");
        

        if (item.getCurrentDBVersion() != null) {
            if (!Permissions.canEdit(user, item)) {
           	 throw new InsufficientPrivilegesException("User account doesn't have permission to edit this project.");
            }
        } else {
            final Long count = XDAT.getContextService().getBean(NamedParameterJdbcTemplate.class).queryForObject("SELECT COUNT(id) FROM xnat_projectdata_history WHERE id = :projectId", new MapSqlParameterSource("projectId", projectId), Long.class);
            if (count > 0) 
           	 throw new InsufficientPrivilegesException("Project '" + project.getId() + "' was used in a previously deleted project and cannot be reused.");
        }

        // Validate project fields.  If there are conflicts, build a error message and display it to the user.
        final Collection<String> conflicts = workingProject.validateProjectFields();
        if (!conflicts.isEmpty())
       	 throw new ResourceAlreadyExistsException("Requested new project conflicts with existing projects: {}" + StringUtils.join(conflicts, ", "), "");
        

        if (project == null) {
            BaseXnatProjectdata.createProject(workingProject, user, allowDataDeletion, true, newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN), accessibility);
        } else {
            SaveItemHelper.authorizedSave(item, user, false, false, newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN));
            if (StringUtils.isNotBlank(accessibility) && !StringUtils.equals(workingProject.getPublicAccessibility(), accessibility)) {
                // If we don't allow non private projects, we shouldn't allow accessibility to change. 
                final boolean nonPrivateAllowed = XDAT.getBoolSiteConfigurationProperty("securityAllowNonPrivateProjects", true);
                if(!nonPrivateAllowed) {
                    log.debug("Unable to change project accessibility because securityAllowNonPrivateProjects is set to {}" , String.valueOf(nonPrivateAllowed));
                    log.debug("Non-private projects are not allowed. Update siteConfig preference if you wish to allow non-private projects.");
                    throw new InsufficientPrivilegesException("Non-private projects are not allowed. Update siteConfig preference if you wish to allow non-private projects.");
                }
                
                final PersistentWorkflowI workflow = WorkflowUtils.buildProjectWorkflow(user, project, newEventInstance(EventUtils.CATEGORY.PROJECT_ACCESS, EventUtils.MODIFY_PROJECT_ACCESS));
                Permissions.setDefaultAccessibility(workingProject.getId(), accessibility, false, user, workflow.buildEvent());
            }
            XDAT.triggerXftItemEvent(XnatProjectdata.SCHEMA_ELEMENT_NAME, projectId, XftItemEventI.UPDATE);
        }
        return XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
	}

	private XnatProjectdata verifyFilePathAndCreateorUpdateXnatProject(String filepath, XnatProjectdata workingProject, UserI user) throws Exception {
		SecureResoureUtil secureResoureUtil = new SecureResoureUtil();
		if((filepath.startsWith("quarantine_code/")) || (filepath.startsWith("prearchive_code/")) || (filepath.startsWith("current_arc/"))){
			final ArcProject arcProject = workingProject.getArcSpecification();
			if (filepath.startsWith("quarantine_code/")) {
	             final String quarantineCode = StringUtils.removeStart(filepath, "quarantine_code/"); 
	             if (StringUtils.isNotBlank(quarantineCode)) 
	            	 arcProject.setQuarantineCode(translateArcProjectCode(quarantineCode));
	             
	         } else if (filepath.startsWith("prearchive_code/")) {
	             final String prearchiveCode = StringUtils.removeStart(filepath, "prearchive_code/");
	             if (StringUtils.isNotBlank(prearchiveCode)) {
	                 if (XDAT.getBoolSiteConfigurationProperty("project.allow-auto-archive", true) || StringUtils.equals(prearchiveCode, "0")) 
	                	 arcProject.setPrearchiveCode(translateArcProjectCode(prearchiveCode));
	                  else 
	                	 throw new InsufficientPrivilegesException("");
	             }
	         } else if (filepath.startsWith("current_arc/")) {
	             final String currentArc = StringUtils.removeStart(filepath, "current_arc/");
	             if (StringUtils.isNotBlank(currentArc)) 
	            	 arcProject.setCurrentArc(currentArc);
	         }  
			 create(workingProject, arcProject, false, false, newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, "Configured quarantine code"),user);
             ArcSpecManager.Reset();
		}else if (filepath.startsWith("scan_type_mapping/")) {
             final String scanTypeMapping = StringUtils.removeStart(filepath, "scan_type_mapping/");
             workingProject.setUseScanTypeMapping(BooleanUtils.toBoolean(scanTypeMapping));
             secureResoureUtil.update(workingProject, false, false, newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, "Configured current arc"), user);
             ArcSpecManager.Reset();
         } else {
        	 throw new DataFormatException("request data is missing");
         }
		return workingProject;
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
    
    protected XFTItem getProjectXftItem(final UserI user, XnatProjectdata project, String xsiType) throws ClientException, ServerException, XFTInitException, ElementNotFoundException {
		XFTItem item = project.getItem();

		if (item == null && xsiType != null) {
			item = XFTItem.NewItem(xsiType, user);
		}

		if (item == null && project != null) {
			item = project.getItem();
		}
		return item;
	}

    @Override
    public void deleteById(final UserI user, final String projectId,  boolean removeFiles) throws DataFormatException, InitializationException, NotFoundException {
    	log.info("User {} is deleting the project  Id {} ", user.getUsername(), projectId);
    	if(Objects.isNull(projectId))
    		throw new DataFormatException("The requested projectId wasn't found ");
    	delete(user, findById(user, projectId).get(), removeFiles);
    }

    
    public void delete(final UserI user, final XnatProjectdata proj, boolean removeFiles) throws DataFormatException, InitializationException {
		XnatProjectdata project = null;
		final String projectId = proj.getId();
		String filepath = null;
		project = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
		log.debug("User {} is deleting the project {}", user.getUsername(), project.getId());
		if (project == null || StringUtils.isNotBlank(filepath))
			throw new DataFormatException("Please check project request object");
		try {
			if (user.isGuest() || !Permissions.canDelete(user, project))
				throw new InsufficientPrivilegesException( "User account doesn't have permission to delete this project.");

		} catch (Exception e) {
			log.error("An error occurred checking permissions for user " + user.getUsername() + " to delete the project " + projectId, e);
			throw new InitializationException("An error occurred checking permissions for user " + user.getUsername() + " to delete the project " + projectId);
		}

		try {
			final PersistentWorkflowI workflow = WorkflowUtils.getOrCreateWorkflowData(getEventId(), user, SCHEMA_ELEMENT_NAME, projectId, projectId, newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, EventUtils.getDeleteAction(XnatProjectdata.SCHEMA_ELEMENT_NAME)));
			final EventMetaI event = workflow.buildEvent();

			try {
				project.delete(removeFiles, user, event);
				PersistentWorkflowUtils.complete(workflow, event);
				return;
			} catch (Exception e) {
				log.error("An error occurred when user " + user.getUsername() + " tried to delete the project " + projectId, e);
				PersistentWorkflowUtils.fail(workflow, event);
			}
		} catch (Exception e) {
			log.error("An error occurred trying manage delete operation for user " + user.getUsername() + " on project " + projectId, e);
		}
		// If we got here, the delete operation failed, so the server error status
		// should always be set.
		throw new InitializationException("delete operation failed");
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
