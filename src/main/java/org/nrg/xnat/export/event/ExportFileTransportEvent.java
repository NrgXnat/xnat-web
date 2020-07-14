package org.nrg.xnat.export.event;

import org.nrg.xft.security.UserI;

/**
 * @author Mohana Ramaratnam
 *
 */
public class ExportFileTransportEvent  {

	private UserI user;
	private String message;
	private String projectId;
	private String trackingId;
	private boolean complete = false;
 
    public ExportFileTransportEvent(UserI user, String projectId, String projectTrackingId, String message, boolean complete) {
    	this.user = user;
    	this.projectId = projectId;
    	this.trackingId = projectTrackingId;
    	this.message = message;
    	this.complete = complete;
    }

    public ExportFileTransportEvent(UserI user, String projectId, String projectTrackingId, String message) {
    	this(user, projectId, projectTrackingId, message,false);
    }

    
    
    
	/**
	 * @return the user
	 */
	public UserI getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(UserI user) {
		this.user = user;
	}

	/**
	 * @return the trackingId
	 */
	public String getTrackingId() {
		return trackingId;
	}

	/**
	 * @param trackingId the trackingId to set
	 */
	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}


	/**
	 * @return the complete
	 */
	public boolean isComplete() {
		return complete;
	}

	/**
	 * @param complete the complete to set
	 */
	public void setComplete(boolean complete) {
		this.complete = complete;
	}


    
    
}
