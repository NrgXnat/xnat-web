package org.nrg.xnat.model.util;

import static org.nrg.xft.event.XftItemEventI.DELETE;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ClientException;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.model.XnatProjectdataI;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatExperimentdataShare;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagescandataShare;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatPvisitdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.om.base.BaseXnatExperimentdata;
import org.nrg.xdat.om.base.BaseXnatImagescandata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.turbine.utils.TurbineUtils;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFTItem;
import org.nrg.xft.db.MaterializedView;
import org.nrg.xft.db.ViewManager;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.XftItemEvent;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xft.utils.XftStringUtils;
import org.nrg.xft.utils.ValidationUtils.ValidationResults;
import org.nrg.xnat.archive.Rename;
import org.nrg.xnat.exceptions.InvalidArchiveStructure;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.nrg.xnat.turbine.utils.XNATUtils;
import org.nrg.xnat.utils.WorkflowUtils;
import org.restlet.data.Form;

import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecureResourceUtil {

	public boolean rename(final XnatProjectdataI proj, final ArchivableItem existing, final String label, final UserI user) {
        try {
            new Rename((XnatProjectdata) proj, existing, label, user, getReason(), getEventType()).call();
        } catch (Rename.ProcessingInProgress e) {
            final String message = "Specified session is being processed (" + e.getPipelineName() + ").";
            log.error(message, e);
            return false;
        } catch (Rename.DuplicateLabelException | Rename.LabelConflictException e) {
            final String message = "Specified label " + label + " is already in use.";
            log.error(message, e);
            return false;
        } catch (Rename.FolderConflictException e) {
            final String message = "File system destination contains pre-existing files";
            log.error(message, e);
            return false;
        } catch (InvalidArchiveStructure | URISyntaxException e) {
            final String message = "Non-standard archive structure in existing experiment directory.";
            log.error(message, e);
            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
	 public boolean update(final ArchivableItem item, boolean overwriteSecurity, boolean allowDataDeletion, final EventDetails event,  XnatEventUtil xnatEvent,UserI user) throws Exception {
		 final PersistentWorkflowI workflow = WorkflowUtils.getOrCreateWorkflowData(XnatEventUtil.getEventId(xnatEvent.getEventId()),user, item.getItem(), event);
	        final EventMetaI meta = workflow.buildEvent();
	        return update(item, overwriteSecurity, allowDataDeletion, workflow, meta, user);
	    }
	
	public boolean update(final ArchivableItem item, boolean overwriteSecurity, boolean allowDataDeletion, final PersistentWorkflowI workflow, final EventMetaI meta, UserI user) throws Exception {
        return createOrUpdateImpl(false, item, overwriteSecurity, allowDataDeletion, workflow, meta, user);
    }
	
	public boolean create(final ArchivableItem item, boolean overwriteSecurity, boolean allowDataDeletion, EventDetails event,XnatEventUtil xnatEvent, UserI user) throws Exception {
		final PersistentWorkflowI workflow = WorkflowUtils.getOrCreateWorkflowData(XnatEventUtil.getEventId(xnatEvent.getEventId()), user, item.getItem(),event);
		final EventMetaI meta = workflow.buildEvent();
		return create(item, overwriteSecurity, allowDataDeletion, workflow, meta, user);
	}
		 
	public boolean create(final ArchivableItem item, boolean overwriteSecurity, boolean allowDataDeletion, final PersistentWorkflowI workflow, final EventMetaI meta, UserI user) throws Exception {
		return createOrUpdateImpl(true, item, overwriteSecurity, allowDataDeletion, workflow, meta, user);
	}

	public boolean create(final ArchivableItem parent, final ItemI sub, final boolean overwriteSecurity, final boolean allowDataDeletion, final EventDetails event, XnatEventUtil xnatEvent, UserI user) throws Exception {
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
	 
	public void postSaveManageStatus(ItemI i, UserI user, XnatEventUtil event) throws InsufficientPrivilegesException, Exception {
			if (isQueryVariableTrue("activate")) {
				if (Permissions.canActivate(user, i.getItem())) {
					PersistentWorkflowI wrk = PersistentWorkflowUtils.getOrCreateWorkflowData(XnatEventUtil.getEventId(event.getEventId()), user,
							i.getItem(), XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, "Activated", event));
					try {
						i.activate(user);
						WorkflowUtils.complete(wrk, wrk.buildEvent());
					} catch (Exception e) {
						log.error("", e);
						WorkflowUtils.fail(wrk, wrk.buildEvent());
					}
				} else 
					throw new InsufficientPrivilegesException("Specified user  account has insufficient activation privileges for experiments in this project.");
			}

			if (isQueryVariableTrue(ViewManager.QUARANTINE)) {
				if (Permissions.canActivate(user, i.getItem())) {
					PersistentWorkflowI wrk = PersistentWorkflowUtils.getOrCreateWorkflowData(XnatEventUtil.getEventId(event.getEventId()), user,
							i.getItem(), XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, "Quarantined", event));
					try {
						i.quarantine(user);
						WorkflowUtils.complete(wrk, wrk.buildEvent());
					} catch (Exception e) {
						log.error("", e);
						WorkflowUtils.fail(wrk, wrk.buildEvent());
					}
				} else 
					throw new InsufficientPrivilegesException("Specified user  account has insufficient activation privileges for experiments in this project.");
			}

			if (isQueryVariableTrue("_lock")) {
				if (Permissions.canActivate(user, i.getItem())) {
					PersistentWorkflowI wrk = PersistentWorkflowUtils.getOrCreateWorkflowData(XnatEventUtil.getEventId(event.getEventId()), user,
							i.getItem(), XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, "Locked", event));
					try {
						i.lock(user);
						WorkflowUtils.complete(wrk, wrk.buildEvent());
					} catch (Exception e) {
						log.error("", e);
						WorkflowUtils.fail(wrk, wrk.buildEvent());
					}
				} else {
					throw new InsufficientPrivilegesException("Specified user account has insufficient activation privileges for experiments in this project.");
				}
			} else if (isQueryVariableTrue("_unlock")) {
				if (Permissions.canActivate(user, i.getItem())) {
					PersistentWorkflowI wrk = PersistentWorkflowUtils.getOrCreateWorkflowData(XnatEventUtil.getEventId(event.getEventId()), user,
							i.getItem(), XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, "Unlocked", event));
					try {
						i.activate(user);
						WorkflowUtils.complete(wrk, wrk.buildEvent());
					} catch (Exception e) {
						log.error("", e);
						WorkflowUtils.fail(wrk, wrk.buildEvent());
					}
				} else 
					throw new InsufficientPrivilegesException("Specified user  account has insufficient activation privileges for experiments in this project.");
				
			} else if (isQueryVariableTrue("_obsolete")) {
				if (Permissions.canActivate(user, i.getItem())) {
					PersistentWorkflowI wrk = PersistentWorkflowUtils.getOrCreateWorkflowData(XnatEventUtil.getEventId(event.getEventId()), user,
							i.getItem(), XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, "Obsoleted", event));
					try {
						i.getItem().setStatus(user, ViewManager.OBSOLETE);
						WorkflowUtils.complete(wrk, wrk.buildEvent());
					} catch (Exception e) {
						log.error("", e);
						WorkflowUtils.fail(wrk, wrk.buildEvent());
					}
				} else 
					throw new InsufficientPrivilegesException("Specified user  account has insufficient activation privileges for experiments in this project.");
				
			}
	}
	 public void delete(ArchivableItem parent, ItemI item, boolean removeFiles, EventDetails event, XnatEventUtil xnatEvent, UserI user) throws Exception {
	        final PersistentWorkflowI workflow = WorkflowUtils.getOrCreateWorkflowData(XnatEventUtil.getEventId(xnatEvent.getEventId()), user, parent.getXSIType(), parent.getId(), parent.getProject(), event);
	        final EventMetaI          ci       = workflow.buildEvent();

	        try {
	            XNATUtils.delete(parent, item, ci, removeFiles);
	            WorkflowUtils.complete(workflow, ci);
	        } catch (Exception e) {
	            WorkflowUtils.fail(workflow, ci);
	            throw e;
	        }

	        Users.clearCache(user);
	        MaterializedView.deleteByUser(user);
	    }
		
	    public XnatProjectdata getProjectFromFilePath(final XnatProjectdata project, final ArchivableItem item, String filepath, UserI user) throws NotFoundException {
	        if (filepath != null && !filepath.equals("")) {
	            if (filepath.startsWith("projects/")) {
	                final String          newProjectId = filepath.substring(9);
	                final XnatProjectdata newProject   = XnatProjectdata.getXnatProjectdatasById(newProjectId, user, false);
	                if (newProject == null) {
	                    throw new NotFoundException(newProjectId);
	                }
	                return newProject;
	            } else {
	                throw new IllegalArgumentException("Illegal file path '" + filepath + "' does not start with 'projects/'.");
	            }
	        } else if (!item.getProject().equals(project.getId())) {
	            return project;
	        }
	        return null;
	    }
	    
	    public void validateSubject(final XnatSubjectdata subject) throws Exception {
	        if (StringUtils.isNotBlank(subject.getLabel()) && !XftStringUtils.isValidId(subject.getId())) 
	        	throw new DataFormatException("Invalid character in subject label.");

	        final ValidationResults results = subject.validate();
	        if (results != null && !results.isValid()) 
	        	throw new ClientException(results.toFullString());
	    }
		
	    public void deleteItem(final XnatProjectdata proj, final BaseElement item, boolean removeFiles, UserI user, XnatEventUtil event) throws InitializationException, InsufficientPrivilegesException, NotFoundException, DataFormatException {
	    	if (!ArchivableItem.class.isAssignableFrom(item.getClass())) {
	            throw new IllegalArgumentException("The BaseElement item must also implement the ArchivableItem interface, but the class " + item.getClass().getName() + " doesn't.");
	        }

	        try {
	            final XnatProjectdata     newProject = getProjectFromFilePath(proj, (ArchivableItem) item, user);
	            final PersistentWorkflowI wrk        = WorkflowUtils.buildOpenWorkflow(user, item.getItem(), XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.getDeleteAction(item.getXSIType()), event));
	            final EventMetaI   c          = wrk.buildEvent();

	            try {
	                final XnatProjectdata              project     = (newProject != null) ? newProject : proj;
	                final Class<? extends BaseElement> itemType    = item.getClass();

	                final String message;
	                if (XnatPvisitdata.class.isAssignableFrom(itemType)) {
	                    message = ((XnatPvisitdata) item).delete(project, user, removeFiles, c);
	                } else if (XnatImagesessiondata.class.isAssignableFrom(itemType)) {
	                    message = ((XnatImagesessiondata) item).delete(project, user, removeFiles, c);
	                } else if (XnatSubjectdata.class.isAssignableFrom(itemType)) {
	                    message = ((XnatSubjectdata) item).delete(project, user, removeFiles, c);
	                } else if (XnatExperimentdata.class.isAssignableFrom(itemType)) {
	                    message = ((XnatExperimentdata) item).delete(project, user, removeFiles, c);
	                } else {
	                    message = null;
	                }
	                if (message != null) {
	                    WorkflowUtils.fail(wrk, c);
	                    throw new InsufficientPrivilegesException("The user { } has insufficient privileges to access the requested subject", user.getUsername());
	                } else {
	                    XDAT.triggerXftItemEvent(item, DELETE, ImmutableMap.of("target", project.getId()));
	                    WorkflowUtils.complete(wrk, c);
	                }
	            } catch (Exception e) {
	                try {
	                    WorkflowUtils.fail(wrk, c);
	                } catch (Exception e1) {
	                    log.error("", e1);
	                }
	                log.error("", e);
	                throw new InitializationException(e.getMessage());
	            }
	        } catch (PersistentWorkflowUtils.EventRequirementAbsent e) {
	            log.error("Forbidden: " + e.getMessage(), e);
	            throw new InsufficientPrivilegesException("The user { } has insufficient privileges to access the requested subject", user.getUsername());
	        } catch (NotFoundException e) {
	        	throw new NotFoundException("Unable to identify project: " + e.getMessage());
	        } catch (IllegalArgumentException e) {
	        	throw new DataFormatException("The requested object wasn't found");
	        }
	    }
	
	public void shareExperimentToProject(final UserI user, final XnatProjectdata newProject, final XnatExperimentdata experiment, final String newLabel, XnatEventUtil event) throws Exception {
        shareExperimentToProject(user, newProject, experiment, new XnatExperimentdataShare(user), newLabel, event);
    }

    protected void shareExperimentToProject(final UserI user, final XnatProjectdata newProject, final XnatExperimentdata experiment, final XnatExperimentdataShare shared, final String newLabel, XnatEventUtil event) throws Exception {
        shareExperimentToProject(user, newProject, experiment, shared, newLabel, true, event);
    }

    protected void shareExperimentToProject(final UserI user, final XnatProjectdata newProject, final XnatExperimentdata experiment, final XnatExperimentdataShare shared, final String newLabel, boolean shareAllScans, XnatEventUtil event) throws Exception {
    	final String newProjectId = newProject.getId();

        shared.setProject(newProjectId);
        shared.setProperty("sharing_share_xnat_experimentda_id", experiment.getId());
        if(StringUtils.isNotBlank(newLabel)) {
            shared.setLabel(newLabel);
        }
        if (shareAllScans) {
            if (experiment instanceof XnatImagesessiondata) {
                for (XnatImagescandataI scan : ((XnatImagesessiondata) experiment).getScans_scan()) {
                    shareScanToProject(user, newProject, (XnatImagescandata) scan, event);
                }
            }
        }
        BaseXnatExperimentdata.SaveSharedProject(shared, experiment, user, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.CONFIGURED_PROJECT_SHARING, event));
        XDAT.triggerXftItemEvent(experiment, XftItemEvent.SHARE, ImmutableMap.<String, Object>of("target", newProjectId));
    }
    
    protected void shareScanToProject(final UserI user, final XnatProjectdata newProject, final XnatImagescandata scan, XnatEventUtil event)
            throws Exception {
        XnatImagescandataShare shared = new XnatImagescandataShare(user);
        final String newProjectId = newProject.getId();

        shared.setProject(newProjectId);
        shared.setProperty("sharing_share_xnat_imagescandat_xnat_imagescandata_id", scan.getXnatImagescandataId());
        shared.setLabel(scan.getId());
        BaseXnatImagescandata.SaveSharedProject(shared, scan, user, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.CONFIGURED_PROJECT_SHARING, event));
        XDAT.triggerXftItemEvent(scan, XftItemEvent.SHARE, ImmutableMap.<String, Object>of("target", newProjectId));
    }
	
    
   
    protected XnatProjectdata getProjectFromFilePath(final XnatProjectdataI project, final ArchivableItem item, final UserI user) throws NotFoundException {
        final String newProjectId = project.getId();
        final XnatProjectdata newProject   = XnatProjectdata.getXnatProjectdatasById(newProjectId, user, false);
        if (newProject == null) {
            throw new NotFoundException(newProjectId);
        }
        return newProject;
    }
    
    private boolean createOrUpdateImpl(final boolean isCreate, final ArchivableItem item,
			final boolean overwriteSecurity, final boolean allowDataDeletion, final PersistentWorkflowI workflow,
			final EventMetaI meta, UserI user) throws Exception {
		try {
			if (SaveItemHelper.authorizedSave(item, user, overwriteSecurity, allowDataDeletion, meta)) {
				if (isCreate) {
					final XFTItem xftItem = item.getItem();
					if (xftItem.instanceOf(XnatExperimentdata.SCHEMA_ELEMENT_NAME) || xftItem.instanceOf(XnatSubjectdata.SCHEMA_ELEMENT_NAME) || xftItem.instanceOf(XnatProjectdata.SCHEMA_ELEMENT_NAME)) {
						XDAT.triggerXftItemEvent(xftItem, XftItemEvent.CREATE);
					}
				}
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
    
  //=================Used In experiment========================== 
	protected List<String> actions = null;

	public boolean containsAction(final String name) {
		return getActions().contains(name);
	}

	public List<String> getActions() {
		final String ACTION = "action";
		if (actions == null) {
			final String[] actionA = getQueryVariables(ACTION);
			if (actionA != null && actionA.length > 0) {
				actions = Arrays.asList(actionA);
			}

			if (actions == null)
				actions = new ArrayList<>();
		}
		return actions;
	}

	public String[] getQueryVariables(String key) {
		return getVariablesFromForm(getQueryVariableForm(), key);
	}
	public boolean isQueryVariableTrue(String key) {
		return isQueryVariableTrueHelper(getQueryVariable(key));
	}

	private Form f = null;

	private Form getQueryVariableForm() {
		if (f == null) {
			// f = getQueryVariableForm(getRequest());
		}
		return f;
	}
	 private static String[] getVariablesFromForm(Form f, String key) {
	        if (f != null) {
	            String[] values = f.getValuesArray(key).clone();
	            for (int i = 0; i < values.length; i++) {
	                values[i] = TurbineUtils.escapeParam(values[i]);
	            }
	            return f.getValuesArray(key);
	        }
	        return null;
	    }
	
		
//=================Used In experiment========================== 
//	 public EventDetails newEventInstance(EventUtils.CATEGORY cat, String action) {
//	        return EventUtils.newEventInstance(cat, getEventType(), (getAction() != null) ? getAction() : action, "", "");
//	    }
	 
//	  public String getAction() {
//	        return "Deleted"; //HC
//	        		//getQueryVariable(EventUtils.EVENT_ACTION);
//	    }
	

	protected static boolean isQueryVariableTrueHelper(final Object queryVariableObj) {
		if (queryVariableObj == null) {
			return false;
		}
		if (queryVariableObj instanceof String) {
			return !(StringUtils.equalsAnyIgnoreCase((String) queryVariableObj, "false", "0"));
		} else {
			return false;
		}
	}
	
	public String getQueryVariable(String key) {
		return null;
	}


	
//	private Integer getEventId() {
//		return null;
//	}

	public EventUtils.TYPE getEventType() {
    	return EventUtils.TYPE.WEB_FORM;
//        final String id = getQueryVariable(EventUtils.EVENT_TYPE);
//        if (id != null) {
//            return EventUtils.getType(id, EventUtils.TYPE.WEB_SERVICE);
//        } else {
//            return EventUtils.TYPE.WEB_SERVICE;
//        }
    }

	private String getReason() {
		return null;
	}
}
