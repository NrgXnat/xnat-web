package org.nrg.xnat.model.util;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ClientException;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatExperimentdataShare;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagescandataShare;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
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
import org.nrg.xnat.archive.Rename;
import org.nrg.xnat.exceptions.InvalidArchiveStructure;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.nrg.xnat.turbine.utils.XNATUtils;
import org.nrg.xnat.utils.WorkflowUtils;
import org.restlet.data.Form;
import org.restlet.data.Status;

import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecureResoureUtil {

	public boolean rename(final XnatProjectdata proj, final ArchivableItem existing, final String label, final UserI user) {
        try {
            new Rename(proj, existing, label, user, getReason(), getEventType()).call();
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
	 public boolean update(final ArchivableItem item, boolean overwriteSecurity, boolean allowDataDeletion, final EventDetails event,  UserI user) throws Exception {
	        final PersistentWorkflowI workflow = WorkflowUtils.getOrCreateWorkflowData(getEventId(),user, item.getItem(), event);
	        final EventMetaI meta = workflow.buildEvent();
	        return update(item, overwriteSecurity, allowDataDeletion, workflow, meta, user);
	    }
	
	public boolean update(final ArchivableItem item, boolean overwriteSecurity, boolean allowDataDeletion, final PersistentWorkflowI workflow, final EventMetaI meta, UserI user) throws Exception {
        return createOrUpdateImpl(false, item, overwriteSecurity, allowDataDeletion, workflow, meta, user);
    }
	
	public boolean create(final ArchivableItem item, boolean overwriteSecurity, boolean allowDataDeletion, EventDetails event, UserI user) throws Exception {
		final PersistentWorkflowI workflow = WorkflowUtils.getOrCreateWorkflowData(getEventId(), user, item.getItem(),event);
		final EventMetaI meta = workflow.buildEvent();
		return create(item, overwriteSecurity, allowDataDeletion, workflow, meta, user);
	}
		 
	public boolean create(final ArchivableItem item, boolean overwriteSecurity, boolean allowDataDeletion, final PersistentWorkflowI workflow, final EventMetaI meta, UserI user) throws Exception {
		return createOrUpdateImpl(true, item, overwriteSecurity, allowDataDeletion, workflow, meta, user);
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
	 
	public void postSaveManageStatus(ItemI i, UserI user) throws InsufficientPrivilegesException, Exception {
			if (isQueryVariableTrue("activate")) {
				if (Permissions.canActivate(user, i.getItem())) {
					PersistentWorkflowI wrk = PersistentWorkflowUtils.getOrCreateWorkflowData(getEventId(), user,
							i.getItem(), newEventInstance(EventUtils.CATEGORY.DATA, "Activated"));
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
					PersistentWorkflowI wrk = PersistentWorkflowUtils.getOrCreateWorkflowData(getEventId(), user,
							i.getItem(), newEventInstance(EventUtils.CATEGORY.DATA, "Quarantined"));
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
					PersistentWorkflowI wrk = PersistentWorkflowUtils.getOrCreateWorkflowData(getEventId(), user,
							i.getItem(), newEventInstance(EventUtils.CATEGORY.DATA, "Locked"));
					try {
						i.lock(user);
						WorkflowUtils.complete(wrk, wrk.buildEvent());
					} catch (Exception e) {
						log.error("", e);
						WorkflowUtils.fail(wrk, wrk.buildEvent());
					}
				} else {
					throw new ClientException(Status.CLIENT_ERROR_FORBIDDEN,"Specified user account has insufficient activation privileges for experiments in this project.", new Exception());
				}
			} else if (isQueryVariableTrue("_unlock")) {
				if (Permissions.canActivate(user, i.getItem())) {
					PersistentWorkflowI wrk = PersistentWorkflowUtils.getOrCreateWorkflowData(getEventId(), user,
							i.getItem(), newEventInstance(EventUtils.CATEGORY.DATA, "Unlocked"));
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
					PersistentWorkflowI wrk = PersistentWorkflowUtils.getOrCreateWorkflowData(getEventId(), user,
							i.getItem(), newEventInstance(EventUtils.CATEGORY.DATA, "Obsoleted"));
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
	
	
	
	public void shareExperimentToProject(final UserI user, final XnatProjectdata newProject, final XnatExperimentdata experiment, final String newLabel) throws Exception {
        shareExperimentToProject(user, newProject, experiment, new XnatExperimentdataShare(user), newLabel);
    }

    protected void shareExperimentToProject(final UserI user, final XnatProjectdata newProject, final XnatExperimentdata experiment, final XnatExperimentdataShare shared, final String newLabel) throws Exception {
        shareExperimentToProject(user, newProject, experiment, shared, newLabel, true);
    }

    protected void shareExperimentToProject(final UserI user, final XnatProjectdata newProject, final XnatExperimentdata experiment, final XnatExperimentdataShare shared, final String newLabel, boolean shareAllScans) throws Exception {
        final String newProjectId = newProject.getId();

        shared.setProject(newProjectId);
        shared.setProperty("sharing_share_xnat_experimentda_id", experiment.getId());
        if(StringUtils.isNotBlank(newLabel)) {
            shared.setLabel(newLabel);
        }
        if (shareAllScans) {
            if (experiment instanceof XnatImagesessiondata) {
                for (XnatImagescandataI scan : ((XnatImagesessiondata) experiment).getScans_scan()) {
                    shareScanToProject(user, newProject, (XnatImagescandata) scan);
                }
            }
        }
        BaseXnatExperimentdata.SaveSharedProject(shared, experiment, user, newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.CONFIGURED_PROJECT_SHARING));
        XDAT.triggerXftItemEvent(experiment, XftItemEvent.SHARE, ImmutableMap.<String, Object>of("target", newProjectId));
    }
    
    protected void shareScanToProject(final UserI user, final XnatProjectdata newProject, final XnatImagescandata scan)
            throws Exception {
        XnatImagescandataShare shared = new XnatImagescandataShare(user);
        final String newProjectId = newProject.getId();

        shared.setProject(newProjectId);
        shared.setProperty("sharing_share_xnat_imagescandat_xnat_imagescandata_id", scan.getXnatImagescandataId());
        shared.setLabel(scan.getId());
        BaseXnatImagescandata.SaveSharedProject(shared, scan, user, newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.CONFIGURED_PROJECT_SHARING));
        XDAT.triggerXftItemEvent(scan, XftItemEvent.SHARE, ImmutableMap.<String, Object>of("target", newProjectId));
    }
	
    
    public void delete(ArchivableItem parent, ItemI item, EventDetails event, UserI user) throws Exception {
        final PersistentWorkflowI workflow = WorkflowUtils.getOrCreateWorkflowData(getEventId(), user, parent.getXSIType(), parent.getId(), parent.getProject(), event);
        final EventMetaI          ci       = workflow.buildEvent();

        try {
            XNATUtils.delete(parent, item, ci, isQueryVariableTrue("removeFiles"));
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
    
	
	 protected    List<String> actions = null;
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

	            if (actions == null) actions = new ArrayList<>();
	        }
	        return actions;
	    }
	 public String[] getQueryVariables(String key) {
	        return getVariablesFromForm(getQueryVariableForm(), key);
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
	
	 public EventDetails newEventInstance(EventUtils.CATEGORY cat, String action) {
	        return EventUtils.newEventInstance(cat, getEventType(), (getAction() != null) ? getAction() : action, "", "");
	    }
	 
	  public String getAction() {
	        return "Deleted"; //HC
	        		//getQueryVariable(EventUtils.EVENT_ACTION);
	    }
	public boolean isQueryVariableTrue(String key) {
		return isQueryVariableTrueHelper(getQueryVariable(key));
	}

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
		// getQueryVariable(key, getRequest());
	}


	
	private Integer getEventId() {
		return null;
	}

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
