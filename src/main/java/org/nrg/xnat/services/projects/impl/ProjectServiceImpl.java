package org.nrg.xnat.services.projects.impl;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.nrg.action.ActionException;
import org.nrg.config.exceptions.ConfigServiceException;
import org.nrg.framework.services.ContextService;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.model.XnatProjectdataI;
import org.nrg.xdat.om.ArcProject;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.base.BaseXnatProjectdata;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xdat.services.DataTypeAwareEventService;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFTItem;
import org.nrg.xft.db.MaterializedView;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.XftItemEventI;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.ActionNameAbsent;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.IDAbsent;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.JustificationAbsent;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xft.exception.XftItemException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xft.utils.XftStringUtils;
import org.nrg.xnat.model.util.SecureResourceUtil;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.projects.ProjectService;
import org.nrg.xnat.turbine.utils.ArcSpecManager;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.nrg.xnat.utils.WorkflowUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import static org.nrg.xdat.om.base.auto.AutoXnatProjectdata.SCHEMA_ELEMENT_NAME;

import java.util.*;

@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService {
	
	@Autowired
	public ProjectServiceImpl(final NamedParameterJdbcTemplate template, final SiteConfigPreferences preferences,final DataTypeAwareEventService eventService) {
		_template = template;
		_preferences = preferences;
		_eventService = eventService;
	}
	
    @Override
    public List<XnatProjectdataI> findAll(final UserI user) throws NotFoundException {
    	List<XnatProjectdata> projects= XnatProjectdata.getAllXnatProjectdatas(user, false);

		List<XnatProjectdataI> xnatProjectdataIs = new ArrayList<>();
		for(XnatProjectdata project: projects) {
			xnatProjectdataIs.add(project);
		}
    	if(Objects.isNull(projects) || projects.isEmpty()) {
    		throw new  NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME) ;
    	}
    	return xnatProjectdataIs;
    }

    @Override
    public  Optional<XnatProjectdataI> findById(final UserI user, final String projectId) throws DataFormatException, NotFoundException {
    	if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID" + projectId + " wasn't found ");
    	}

		XnatProjectdataI proj = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);


    	if(Objects.isNull(proj)) {
    		throw new  NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME, projectId) ;
    	}
    	return Optional.of(proj);
    }

    @Override
    public XnatProjectdataI create(final UserI user, final XnatProjectdata proj, boolean  allowDataDeletion, String accessibility, String xsiType, XnatEventUtil event ) throws ActionException, UserNotFoundException, UserInitException, DataFormatException, XftItemException, InsufficientPrivilegesException, ResourceAlreadyExistsException {
		log.debug("User {} is creating the project {}", user.getUsername(), proj.getId());
		XFTItem item;

		// step 1: get XFTItem from project request
		item = getProjectXftItem(user, proj, xsiType);

		// step 2: Set user into XFTItem
		item.setUser(user);

		if (item.instanceOf("xnat:projectData")) {
			XnatProjectdata project = new XnatProjectdata(item);
			if (StringUtils.isBlank(project.getId())) {
				throw new DataFormatException("Requires XNAT ProjectData ID");
			}

			if (!XftStringUtils.isValidId(project.getId())) {
				throw new DataFormatException("Invalid character in project ID.");
			}

			if (item.getCurrentDBVersion() == null) {
				if (_preferences.getUiAllowNonAdminProjectCreation() || Roles.isSiteAdmin(user)) {
					BaseXnatProjectdata.createProject(project, user, allowDataDeletion, false, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, event), accessibility);
				} else {
					throw new InsufficientPrivilegesException( "User account doesn't have permission to edit this project.");
				}
			} else {
				throw new ResourceAlreadyExistsException("Project already exists.", proj.getId());
			}
		}
		return XnatProjectdata.getXnatProjectdatasById(proj.getId(), user, false);
	}
    
    
	@SuppressWarnings("unused")
	@Override
    public XnatProjectdataI update(final UserI user, final XnatProjectdata project, String filepath, boolean allowDataDeletion, String accessibility, Boolean testHyphen,  String xsiType, XnatEventUtil event ) throws Exception {
		log.debug("User {} is updating the project  Id {} ", user.getUsername(), project.getId());
	
		final String projectId = project.getId();

		if (user.isGuest()) {
			throw new InsufficientPrivilegesException("User " + user.getUsername() + " doesn't have permission to edit projects on this system");
		}
		// Project equal to null means a new project, so either non-admins must be able
		// to create projects or the user must be an admin.
		if (project == null && !_preferences.getUiAllowNonAdminProjectCreation() && !Roles.isSiteAdmin(user)) {
			throw new InsufficientPrivilegesException( "User " + user.getUsername() + " doesn't have permission to create projects on this system");
		}
		// All file path settings require an existing project, so if there's a file path  and no project, that's bad, m'kay?
		final boolean hasFilePath = StringUtils.isNotBlank(filepath);
		if (hasFilePath && project == null) {
			throw new DataFormatException("You can't set the '" + StringUtils.substringBefore(filepath, "/") + "' attribute without specifying the project on which you want to set it.");
		}
		// If we do have a project, we can go ahead and check permissions to edit it now
		// before we go any farther.
		if (project != null && !Permissions.canEditProject(user, projectId)) {
			throw new InsufficientPrivilegesException( "User " + user.getUsername() + " doesn't have permission to edit the project " + project.getId());
		}
		if (project == null || Permissions.canEdit(user, project)) {
			XFTItem item = getProjectXftItem(user, project, xsiType);
			item.setUser(user);

			if (item.instanceOf("xnat:projectData")) {
				XnatProjectdata workingProject = new XnatProjectdata(item);

				if (hasFilePath) {
					if (StringUtils.isBlank(workingProject.getId())) {
						item = project.getItem();
						workingProject = project;
					}
					if (!Permissions.canEdit(user, item))
						throw new InsufficientPrivilegesException("User account doesn't have permission to edit this project.");

					workingProject = verifyFilePathAndCreateorUpdateXnatProject(filepath, workingProject, user,event);
				} else
					return save(workingProject, projectId, user, item, allowDataDeletion, project,accessibility, testHyphen, event);
			}
		} else {
			throw new InsufficientPrivilegesException("User account doesn't have permission to edit this project.");
		}
		return XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
	}
	
	
	
	@Override
	public Optional<String> findByProjectId(UserI user, String projectId) throws NotFoundException, DataFormatException{
		if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID "+ projectId +" wasn't found");
		}
		
		XnatProjectdata project = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
		
		if(Objects.isNull(project)) {
			throw new NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME, projectId);
		}
		
		String result = getProjectAccessibility(project);
		if(StringUtils.isBlank(result)) {
			throw new NotFoundException( "An error occurred trying to retrieve the accessibility setting for the project '{}'", project.getId());
		}
    	return Optional.of(result);
	}

	@Override
	public Optional<String> findByProjectIdAndAccessLevel(UserI user, String projectId, String accessLevel) throws NotFoundException, DataFormatException {
		return findByProjectId(user, projectId);
	}

	@Override
	public String update(UserI user, String access, String projectId, XnatEventUtil event) throws NotFoundException, InsufficientPrivilegesException, JustificationAbsent, ActionNameAbsent, IDAbsent, ConfigServiceException {
		XnatProjectdata project = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
		if (StringUtils.isBlank(access) || project == null) 
			throw new NotFoundException( "An error occurred trying to retrieve the accessibility setting for the project '{}'", project.getId());

		try {
			if (!Permissions.canDelete(user, project)) 
				throw new InsufficientPrivilegesException(user.getUsername());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// If we don't allow non private projects, we shouldn't allow accessibility to change.
		verifyProjectAccessibility(user);
		
		updateProjectAccessibity(access, project, user,event);
		
		return getProjectAccessibility(XnatProjectdata.getXnatProjectdatasById(projectId, user, false));
	}

	@Override
	public Optional<ArcProject> findArcProjectByProjectId(UserI user, String projectId) throws NotFoundException, DataFormatException {
		if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID " + projectId+ "wasn't found ");
		}
		XnatProjectdata proj = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
		if(Objects.isNull(proj)) {
    		throw new  NotFoundException(ArcProject.SCHEMA_ELEMENT_NAME, projectId) ;
		}
		ArcProject arcProj=   ArcSpecManager.GetFreshInstance().getProjectArc(proj.getId());
		if(Objects.isNull(arcProj)) {
    		throw new  NotFoundException(ArcProject.SCHEMA_ELEMENT_NAME, projectId) ;
		}
		return Optional.of(arcProj);
	}
	
	
	private void updateProjectAccessibity(String access, XnatProjectdata project, UserI user, XnatEventUtil event2) throws JustificationAbsent, ActionNameAbsent, IDAbsent  {
		final String currentAccess = getProjectAccessibility(project);
		if (!StringUtils.equals(currentAccess, access)) {
			final PersistentWorkflowI workflow = WorkflowUtils.buildProjectWorkflow(user, project, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.PROJECT_ACCESS, EventUtils.MODIFY_PROJECT_ACCESS,event2));
			final EventMetaI event = workflow.buildEvent();
			try {
				if (Permissions.setDefaultAccessibility(project.getId(), access, true, user, event)) {
					WorkflowUtils.complete(workflow, event);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	private String getSiteConfigurationProperty(final String property, final String defaultValue) throws
			ConfigServiceException {
		try {
//			final SiteConfigPreferences preferences = _contextService.getInstance().getBean(SiteConfigPreferences.class);
			final String value = _preferences.getValue(property);
			return StringUtils.defaultIfBlank(value, defaultValue);
		} catch (NoSuchBeanDefinitionException e) {
			log.warn("Couldn't find the site config preferences bean, returning default value {}", defaultValue, e);
			return defaultValue;
		}
	}



	private void verifyProjectAccessibility(UserI user) throws InsufficientPrivilegesException, ConfigServiceException {
//		final boolean nonPrivateAllowed = XDAT.getBoolSiteConfigurationProperty("securityAllowNonPrivateProjects", true);

		final boolean nonPrivateAllowed = BooleanUtils.toBoolean(getSiteConfigurationProperty("securityAllowNonPrivateProjects", Boolean.toString(true)));


		if (!nonPrivateAllowed) {
			log.debug("Unable to change project accessibility because securityAllowNonPrivateProjects is set to" + String.valueOf(nonPrivateAllowed));
			log.debug("Non-private projects are not allowed. Update siteConfig preference if you wish to allow non-private projects.");
			throw new InsufficientPrivilegesException(user.getUsername());
		}
	}
    
	private String getProjectAccessibility(XnatProjectdata project)  {
		try {
			return project.getPublicAccessibility();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
    private XnatProjectdata save(XnatProjectdata workingProject, String projectId, UserI user, XFTItem item, boolean allowDataDeletion, XnatProjectdata project, String accessibility, Boolean testHyphen, XnatEventUtil event) throws DataFormatException, InsufficientPrivilegesException, ResourceAlreadyExistsException, XftItemException  {
    	if (StringUtils.isBlank(workingProject.getId())) {
    		workingProject.setId(projectId);
    	} else if (!StringUtils.equalsIgnoreCase(projectId, workingProject.getId())) {
        	 throw new DataFormatException("The project ID for the REST call must match the value in submitted request body.");
    	}
        
        if (!XftStringUtils.isValidId(workingProject.getId()) && !testHyphen) {
       	 throw new DataFormatException("Invalid character in project ID.");
        }
        
        try {
        if (item.getCurrentDBVersion() != null) {
            if (!Permissions.canEdit(user, item)) {
           	 throw new InsufficientPrivilegesException("User account doesn't have permission to edit this project.");
            }
        } else {
            if (Boolean.TRUE.equals(_template.queryForObject(QUERY_NUM_CHANGES, new MapSqlParameterSource("projectId", projectId), Boolean.class))) {
           	 throw new InsufficientPrivilegesException("Project '" + project.getId() + "' was used in a previously deleted project and cannot be reused.");
            }
        }
        // Validate project fields.  If there are conflicts, build a error message and display it to the user.
        final Collection<String> conflicts = workingProject.validateProjectFields();
        if (!conflicts.isEmpty()) {
       	 throw new ResourceAlreadyExistsException("Requested new project conflicts with existing projects: {}" + StringUtils.join(conflicts, ", "), "");
        }

        if (project == null) {
            BaseXnatProjectdata.createProject(workingProject, user, allowDataDeletion, true, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, event), accessibility);
        } else {
            SaveItemHelper.authorizedSave(item, user, false, false, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, event));
            if (StringUtils.isNotBlank(accessibility) && !StringUtils.equals(workingProject.getPublicAccessibility(), accessibility)) {
                // If we don't allow non private projects, we shouldn't allow accessibility to change. 
                final boolean nonPrivateAllowed = _preferences.getSecurityAllowNonPrivateProjects();
                if(!nonPrivateAllowed) {
                    log.debug("Unable to change project accessibility because securityAllowNonPrivateProjects is set to {}" , String.valueOf(nonPrivateAllowed));
                    log.debug("Non-private projects are not allowed. Update siteConfig preference if you wish to allow non-private projects.");
                    throw new InsufficientPrivilegesException("Non-private projects are not allowed. Update siteConfig preference if you wish to allow non-private projects.");
                }
                
                final PersistentWorkflowI workflow = WorkflowUtils.buildProjectWorkflow(user, project, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.PROJECT_ACCESS, EventUtils.MODIFY_PROJECT_ACCESS, event));
               
				Permissions.setDefaultAccessibility(workingProject.getId(), accessibility, false, user, workflow.buildEvent());
            }
//            XDAT.triggerXftItemEvent(XnatProjectdata.SCHEMA_ELEMENT_NAME, projectId, XftItemEventI.UPDATE);
			_eventService.triggerXftItemEvent(XnatProjectdata.SCHEMA_ELEMENT_NAME, projectId, XftItemEventI.UPDATE);

        }
		} catch (Exception e) {
			e.printStackTrace();
		}
        return XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
	}

	private XnatProjectdata verifyFilePathAndCreateorUpdateXnatProject(String filepath, XnatProjectdata workingProject, UserI user, XnatEventUtil event) throws Exception {
		SecureResourceUtil secureResoureUtil = new SecureResourceUtil();
		if((filepath.startsWith("quarantine_code/")) || (filepath.startsWith("prearchive_code/")) || (filepath.startsWith("current_arc/"))){
			final ArcProject arcProject = workingProject.getArcSpecification();
			if (filepath.startsWith("quarantine_code/")) {
	             final String quarantineCode = StringUtils.removeStart(filepath, "quarantine_code/"); 
	             if (StringUtils.isNotBlank(quarantineCode)) { 
	            	 arcProject.setQuarantineCode(translateArcProjectCode(quarantineCode));
	             }
	             
	         } else if (filepath.startsWith("prearchive_code/")) {
	             final String prearchiveCode = StringUtils.removeStart(filepath, "prearchive_code/");
	             if (StringUtils.isNotBlank(prearchiveCode)) {
//	                 if (XDAT.getBoolSiteConfigurationProperty("project.allow-auto-archive", true) || StringUtils.equals(prearchiveCode, "0")) {
						 if (BooleanUtils.toBoolean(getSiteConfigurationProperty("securityAllowNonPrivateProjects", Boolean.toString(true))) || StringUtils.equals(prearchiveCode, "0")) {
							 arcProject.setPrearchiveCode(translateArcProjectCode(prearchiveCode));
	                 }else {
	                	 throw new InsufficientPrivilegesException("");
	                 }
	             }
	         } else if (filepath.startsWith("current_arc/")) {
	             final String currentArc = StringUtils.removeStart(filepath, "current_arc/");
	             if (StringUtils.isNotBlank(currentArc)) { 
	            	 arcProject.setCurrentArc(currentArc);
	             }
	         }  
			 create(workingProject, arcProject, false, false, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, "Configured quarantine code", event),user, event);
             ArcSpecManager.Reset();
		}else if (filepath.startsWith("scan_type_mapping/")) {
             final String scanTypeMapping = StringUtils.removeStart(filepath, "scan_type_mapping/");
             workingProject.setUseScanTypeMapping(BooleanUtils.toBoolean(scanTypeMapping));
             secureResoureUtil.update(workingProject, false, false, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, "Configured current arc", event), event, user);
             ArcSpecManager.Reset();
         } else {
        	 throw new DataFormatException("request data is missing");
         }
		return workingProject;
	}


	public boolean create(final ArchivableItem parent, final ItemI sub, final boolean overwriteSecurity, final boolean allowDataDeletion, final EventDetails event, UserI user, XnatEventUtil xnatEvent) throws Exception {
		final PersistentWorkflowI workflow = WorkflowUtils.getOrCreateWorkflowData(XnatEventUtil.getEventId(xnatEvent.getEventId()), user, parent.getItem(), event);
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
    
    private int translateArcProjectCode(final String code) throws DataFormatException  {
        if (NumberUtils.isCreatable(code)) {
            return NumberUtils.createInteger(code);
        }
        switch (code) {
            case "true":
                return 1;
            case "false":
                return 0;
            default:
                throw new DataFormatException( "The submitted code " + code + " is invalid: must be an integer.");
        }
    }
    
    protected XFTItem getProjectXftItem(final UserI user, XnatProjectdata project, String xsiType) throws  XFTInitException, ElementNotFoundException {
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
    public void deleteById(final UserI user, final String projectId,  boolean removeFiles, XnatEventUtil event) throws DataFormatException, InitializationException, NotFoundException {
    	log.info("User {} is deleting the project  Id {} ", user.getUsername(), projectId);
    	if(Objects.isNull(projectId)) {
    		throw new DataFormatException("The requested projectId wasn't found ");
    	}
    	delete(user, findById(user, projectId).get(), removeFiles,event);
    }

    
    public void delete(final UserI user, final XnatProjectdataI proj, boolean removeFiles, XnatEventUtil xnatEvent) throws DataFormatException, InitializationException {
		XnatProjectdata project = null;
		final String projectId = proj.getId();
		String filepath = null;
		project = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
		log.debug("User {} is deleting the project {}", user.getUsername(), project.getId());
		if (project == null || StringUtils.isNotBlank(filepath)) {
			throw new DataFormatException("Please check project request object");
		}
		try {
			if (user.isGuest() || !Permissions.canDelete(user, project)) {
				throw new InsufficientPrivilegesException( "User account doesn't have permission to delete this project.");
			}

		} catch (Exception e) {
			log.error("An error occurred checking permissions for user " + user.getUsername() + " to delete the project " + projectId, e);
			throw new InitializationException("An error occurred checking permissions for user " + user.getUsername() + " to delete the project " + projectId);
		}

		try {
			final PersistentWorkflowI workflow = WorkflowUtils.getOrCreateWorkflowData(XnatEventUtil.getEventId(xnatEvent.getEventId()), user, SCHEMA_ELEMENT_NAME, projectId, projectId, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, EventUtils.getDeleteAction(XnatProjectdata.SCHEMA_ELEMENT_NAME), xnatEvent));
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

    private static final String QUERY_NUM_CHANGES = "SELECT COUNT(id) FROM xnat_projectdata_history WHERE id = :projectId";
    
    private final NamedParameterJdbcTemplate _template;
    private final SiteConfigPreferences _preferences;
	private final DataTypeAwareEventService _eventService;

}
