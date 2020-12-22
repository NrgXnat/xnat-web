package org.nrg.xnat.model.util;

import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xft.XFTItem;
import org.nrg.xft.db.MaterializedView;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.XftItemEvent;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.nrg.xnat.utils.WorkflowUtils;

public class XnatProjectUtil {

	//Start -Update XnatSubjectData methods
	
		public boolean update(final ArchivableItem item, boolean overwriteSecurity, boolean allowDataDeletion, final EventDetails event, UserI user) throws Exception {
	        final PersistentWorkflowI workflow = WorkflowUtils.getOrCreateWorkflowData(getEventId(), user, item.getItem(), event);
	        final EventMetaI meta = workflow.buildEvent();
	        return update(item, overwriteSecurity, allowDataDeletion, workflow, meta, user);
	    }
		
		  private Integer getEventId() {
			  final String id = getQueryVariable(EventUtils.EVENT_ID);
		        if (id != null) {
		            return Integer.valueOf(id);
		        } else {
		            return null;
		        }
		}

		private String getQueryVariable(String eventId) {
			return null;
		}

		public boolean update(final ArchivableItem item, boolean overwriteSecurity, boolean allowDataDeletion, final PersistentWorkflowI workflow, final EventMetaI meta, UserI user) throws Exception {
		        return createOrUpdateImpl(false, item, overwriteSecurity, allowDataDeletion, workflow, meta, user);
		    }
		  
		  private boolean createOrUpdateImpl(final boolean isCreate, final ArchivableItem item, final boolean overwriteSecurity, final boolean allowDataDeletion, final PersistentWorkflowI workflow, final EventMetaI meta, UserI user) throws Exception {
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
		
		
		//End -Update XnatSubjectData methods
}
