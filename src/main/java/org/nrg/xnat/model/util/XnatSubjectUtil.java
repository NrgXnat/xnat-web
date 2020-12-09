package org.nrg.xnat.model.util;

import static org.nrg.xft.event.XftItemEventI.DELETE;

import org.nrg.action.ClientException;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatPvisitdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.nrg.xnat.utils.WorkflowUtils;
import org.restlet.data.Status;

import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XnatSubjectUtil {

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
}
