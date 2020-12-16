package org.nrg.xnat.model.util;

import static org.nrg.xft.event.XftItemEventI.DELETE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.model.XnatProjectparticipantI;
import org.nrg.xdat.om.XnatDemographicdata;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatProjectparticipant;
import org.nrg.xdat.om.XnatPvisitdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.turbine.utils.PopulateItem;
import org.nrg.xdat.turbine.utils.TurbineUtils;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFT;
import org.nrg.xft.XFTItem;
import org.nrg.xft.db.MaterializedView;
import org.nrg.xft.db.ViewManager;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.XftItemEvent;
import org.nrg.xft.event.EventUtils.TYPE;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.FieldNotFoundException;
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xft.schema.Wrappers.XMLWrapper.SAXReader;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xft.utils.XftStringUtils;
import org.nrg.xft.utils.ValidationUtils.ValidationResults;
import org.nrg.xnat.archive.Rename;
import org.nrg.xnat.exceptions.InvalidArchiveStructure;
import org.nrg.xnat.helpers.xmlpath.XMLPathShortcuts;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.nrg.xnat.utils.WorkflowUtils;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XnatSubjectUtil {

	protected boolean completeDocument = false;
	public Map<String, String> fieldMapping = new HashMap<>();

	// start -create XnatSubjectData methods
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
	
	 
	public void postSaveManageStatus(ItemI i, UserI user) throws ActionException {
		try {
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
				} else {
					// getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Specified user
					// account has insufficient activation privileges for experiments in this
					// project.");
				}
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
				} else {
					// getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Specified user
					// account has insufficient activation privileges for experiments in this
					// project.");
				}
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
				} else {
					throw new ClientException(Status.CLIENT_ERROR_FORBIDDEN,
							"Specified user account has insufficient activation privileges for experiments in this project.",
							new Exception());
				}
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
				} else {
					throw new ClientException(Status.CLIENT_ERROR_FORBIDDEN,
							"Specified user account has insufficient activation privileges for experiments in this project.",
							new Exception());
				}
			}
		} catch (ActionException e) {
			throw e;
		} catch (Exception e) {
			log.error("", e);
			throw new org.nrg.action.ServerException("Error modifying status", e);
		}
	}
	 
	
	public void validateSubject(final XnatSubjectdata subject) throws Exception {
        if (StringUtils.isNotBlank(subject.getLabel()) && !XftStringUtils.isValidId(subject.getId())) 
        	throw new ClientException("Invalid character in subject label.");

        final ValidationResults results = subject.validate();
        if (results != null && !results.isValid()) 
        	throw new ClientException(results.toFullString());
    }
	
	
	 public  XnatSubjectdata verifyXnatProjectdataAndGetXnatSubjectdata(XnatProjectdata proj, XnatSubjectdata sub, UserI user) throws Exception {
	    	if (proj != null) {
				if (sub.getProject() == null || sub.getProject().equals("")) {
					sub.setProject(proj.getId());
				} else if (sub.getProject().equals(proj.getId())) {
				} else {
					boolean matched = false;
					for (XnatProjectparticipantI pp : sub.getSharing_share()) {
						if (pp.getProject().equals(proj.getId())) {
							matched = true;
							break;
						}
					}
					if (!matched) {
						final XnatProjectparticipant participant = new XnatProjectparticipant(user);
						participant.setProject(proj.getId());
						sub.setSharing_share(participant);
					}
				}
			} else {
				throw new ClientException(Status.CLIENT_ERROR_CONFLICT,"Submitted subject record must include the project attribute.", new Exception());
			}
			return sub;
		}

	 public XnatSubjectdata verifyExistingXnatSubject(XnatSubjectdata sub, UserI user, boolean completeDocument) throws Exception {
	    	XnatSubjectdata existing = null;
			if (sub.getId() != null) 
				existing = XnatSubjectdata.getXnatSubjectdatasById(sub.getId(), user, completeDocument);

			if (existing == null && sub.getProject() != null && sub.getLabel() != null) 
				existing = XnatSubjectdata.GetSubjectByProjectIdentifier(sub.getProject(), sub.getLabel(), user, completeDocument);

			if (existing == null) {
				for (XnatProjectparticipantI pp : sub.getSharing_share()) {
					existing = XnatSubjectdata.GetSubjectByProjectIdentifier(pp.getProject(), pp.getLabel(), user, completeDocument);
					if (existing != null) {
						break;
					}
				}
			}
			if (existing == null) {
				if (!Permissions.canCreate(user, sub)) 
					throw new ClientException("Specified user account has insufficient create privileges for subjects in this project.");
				//IS NEW
				if (StringUtils.isBlank(sub.getId())) 
					sub.setId(XnatSubjectdata.CreateNewID());
			} else 
				throw new ClientAlreadyExistsException("Subject already exists.");
			
			return sub;
		}
	 
	public boolean populateFromDB() {
		return getQueryVariable("populateFromDB") == null || isQueryVariableTrue("populateFromDB");
	}

	public String getQueryVariable(String key) {
		return null;
		// getQueryVariable(key, getRequest());
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

	private Integer getEventId() {
		return null;
	}
	
	
	
	
	// End -create XnatSubjectData methods

	
	
	
	 public boolean rename(final XnatProjectdata proj, final ArchivableItem existing, final String label, final UserI user) {
	        try {
	            new Rename(proj, existing, label, user, getReason(), getEventType()).call();
	        } catch (Rename.ProcessingInProgress e) {
	            final String message = "Specified session is being processed (" + e.getPipelineName() + ").";
	            log.error(message, e);
	           // getResponse().setStatus(Status.CLIENT_ERROR_CONFLICT, message);
	            return false;
	        } catch (Rename.DuplicateLabelException | Rename.LabelConflictException e) {
	            final String message = "Specified label " + label + " is already in use.";
	            log.error(message, e);
	            //getResponse().setStatus(Status.CLIENT_ERROR_CONFLICT, message);
	            return false;
	        } catch (Rename.FolderConflictException e) {
	            final String message = "File system destination contains pre-existing files";
	            log.error(message, e);
	            //getResponse().setStatus(Status.CLIENT_ERROR_CONFLICT, message);
	            return false;
	        } catch (InvalidArchiveStructure | URISyntaxException e) {
	            final String message = "Non-standard archive structure in existing experiment directory.";
	            log.error(message, e);
	            //getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, message);
	            return false;
	        } catch (Exception e) {
	            log.error(e.getMessage(), e);
	            //getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,e.getMessage());
	            return false;
	        }
	        return true;
	    }
	
	 private String getReason() {
			return null;
		}
	
	
	// start -Delete XnatSubjectData methods
	public void deleteItem(final XnatProjectdata proj, final BaseElement item, final UserI user) throws ClientException {
       
		if (!ArchivableItem.class.isAssignableFrom(item.getClass())) {
            throw new IllegalArgumentException("The BaseElement item must also implement the ArchivableItem interface, but the class " + item.getClass().getName() + " doesn't.");
        }
        try {
            final XnatProjectdata newProject = getProjectFromFilePath(proj, (ArchivableItem) item, user);
            final PersistentWorkflowI wrk  = WorkflowUtils.buildOpenWorkflow(user, item.getItem(), newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.getDeleteAction(item.getXSIType())));
            final EventMetaI c  = wrk.buildEvent();

            try {
                final boolean removeFiles = true; //HC
                		//isQueryVariableTrue("removeFiles");
                final XnatProjectdata project  = (newProject != null) ? newProject : proj;
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
                    throw new ClientException(Status.CLIENT_ERROR_FORBIDDEN , message);
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
                throw new ClientException(Status.SERVER_ERROR_INTERNAL , e.getMessage());
            }
        } catch (PersistentWorkflowUtils.EventRequirementAbsent e) {
        	log.error("Forbidden: " + e.getMessage(), e);
        	 throw new ClientException(Status.CLIENT_ERROR_FORBIDDEN , e.getMessage());
        } catch (NotFoundException e) {
        	 throw new ClientException(Status.CLIENT_ERROR_NOT_FOUND ,"Unable to identify project: " + e.getMessage());
        } catch (IllegalArgumentException e) {
        	 throw new ClientException(Status.CLIENT_ERROR_BAD_REQUEST , e.getMessage());
        }
    }
   
    protected XnatProjectdata getProjectFromFilePath(final XnatProjectdata project, final ArchivableItem item, final UserI user) throws NotFoundException {
        final String newProjectId = project.getId();
        final XnatProjectdata newProject   = XnatProjectdata.getXnatProjectdatasById(newProjectId, user, false);
        if (newProject == null) {
            throw new NotFoundException(newProjectId);
        }
        return newProject;
    }
    
    public EventDetails newEventInstance(EventUtils.CATEGORY cat, String action) {
        return EventUtils.newEventInstance(cat, getEventType(), (getAction() != null) ? getAction() : action, "", "");
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
    
    public String getAction() {
        return "Deleted"; //HC
        		//getQueryVariable(EventUtils.EVENT_ACTION);
    }
    
//    public String getReason() {
//        return getQueryVariable(EventUtils.EVENT_REASON);
//    }
//    
//    public String getComment() {
//        return getQueryVariable(EventUtils.EVENT_COMMENT);
//    }
    
 // End -Delete XnatSubjectData methods
}
