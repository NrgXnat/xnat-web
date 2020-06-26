package org.nrg.xnat.export.event;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.nrg.framework.services.NrgEventService;
import org.nrg.xdat.XDAT;
import org.nrg.xnat.export.manifest.DataDescendantManifest;
import org.nrg.xnat.tracking.model.TrackableEvent;

import lombok.extern.slf4j.Slf4j;
/**
 * @author Mohana Ramaratnam
 *
 */


@Slf4j
public class ProjectEvent implements TrackableEvent {

	    private static final long serialVersionUID = 1378574682471630433L;
	    private final String trackingId;
//	    private Integer failures = null;
	    private boolean success = false;
	    private boolean completed = false;
	    private String message = null;
	    private Integer userId =null;

	    private DataDescendantManifest dataDescendantManifest = null;
	    private String exportStatus = null;
		private String exportMessage = null;
		private boolean exportComplete = false;
    	private long numberOfFiles = -1;
    	private long fileSize = -1;


	    public ProjectEvent(String id, Integer uId) {
	        trackingId = id;
	        userId = uId;
	    }

	    public ProjectEvent(String id,Integer uId,DataDescendantManifest dataDescendantManifest,
	    					String exportStatus,String exportMessage,
	    					long numberOfFiles, long fileSize,
	    					boolean exportComplete) {
	        this(id, uId);
	        this.dataDescendantManifest = dataDescendantManifest;
	        this.exportStatus = exportStatus;
	        this.exportMessage = exportMessage;
	        this.numberOfFiles = numberOfFiles;
	        this.fileSize = fileSize;
	        this.exportComplete = exportComplete;
	    }


	    private ProjectEvent(String trackingId, Integer userId, boolean success, boolean completed, String message) {
	        this(trackingId, userId);
	        this.success = success;
	        this.completed = completed;
	        this.message = message;
	    }

	    public static ProjectEvent initial(String trackingId, Integer userId) {
	        ProjectEvent ble = new ProjectEvent(trackingId, userId);
	        return ble;
	    }

	    @Nonnull
	    @Override
	    public String getTrackingId() {
	        return trackingId;
	    }

		public Integer getUserId() {
			return userId;
		}

		public void setUserId(Integer uId) {
			userId = uId;
		}


	    @Override
	    public boolean isSuccess() {
	        return success;
	    }

	    @Override
	    public boolean isCompleted() {
	        return completed;
	    }

	    @Nullable
	    @Override
	    public String getMessage() {
	        return message;
	    }

	    @Override
	    public String updateTrackingPayload(@Nullable String currentPayload) throws IOException {
	    	ProjectEventTrackingLog statusLog;
	    	if (currentPayload != null) {
	            statusLog = XDAT.getSerializerService().getObjectMapper()
	                    .readValue(currentPayload, ProjectEventTrackingLog.class);
	        } else {
	            statusLog = new ProjectEventTrackingLog();
	        }
	    	if (dataDescendantManifest != null ) {
	            statusLog.addOrUpdateExport(dataDescendantManifest, exportStatus, exportMessage,numberOfFiles, fileSize, exportComplete );
	        }
	    	if (statusLog.isExportComplete()) {
	        	statusLog.saveToDb(trackingId);
	    		XDAT.getContextService().getBean(NrgEventService.class).triggerEvent(
	                    new ProjectEvent(trackingId, userId, statusLog.exportSuccess(), true, statusLog.exportMessage()));
	        }
	        return XDAT.getSerializerService().getObjectMapper().writeValueAsString(statusLog);
	    }

	    @Override
	    public String toString() {
	    	String me = trackingId + "(Success:" + success + ", Message:" + message + ", Completed: " + completed +")";
	    	return me;
	    }

}



