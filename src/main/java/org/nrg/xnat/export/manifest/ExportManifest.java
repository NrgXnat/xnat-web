package org.nrg.xnat.export.manifest;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.OneToOne;

import org.nrg.xft.security.UserI;
import org.nrg.xnat.export.interfaces.ExportCredentialsI;
import org.nrg.xnat.export.model.endpoint.EndpointDefinition;

/**
 * @author Mohana Ramaratnam
 *
 */
public class ExportManifest implements Serializable {


	private static final long serialVersionUID = 9201152542393027813L;

	 String projectId;
     Date exportStartDate;
     Date exportComplete;
 	 UserI authorizedBy;
 	 
 	 ExportCredentialsI credentials;
 	 
	 EndpointDefinition endpointDefinition;

     
     /**
	 * @return the exportStartDate
	 */
	public Date getExportStartDate() {
		return exportStartDate;
	}
	
	/**
	 * @param exportStartDate the exportStartDate to set
	 */
	public void setExportStartDate(Date exportStartDate) {
		this.exportStartDate = exportStartDate;
	}
	
	/**
	 * @return the exportComplete
	 */
	public Date getExportComplete() {
		return exportComplete;
	}
	
	/**
	 * @param exportComplete the exportComplete to set
	 */
	public void setExportComplete(Date exportComplete) {
		this.exportComplete = exportComplete;
	}
	
	/**
	 * @return the authorizedBy
	 */
	public UserI getAuthorizedBy() {
		return authorizedBy;
	}
	
	/**
	 * @param authorizedBy the authorizedBy to set
	 */
	public void setAuthorizedBy(UserI authorizedBy) {
		this.authorizedBy = authorizedBy;
	}
	
	/**
	 * @return the endpointDefinition
	 */
	public EndpointDefinition getEndpointDefinition() {
		return endpointDefinition;
	}
	
	/**
	 * @param endpointDefinition the endpointDefinition to set
	 */
	public void setEndpointDefinition(EndpointDefinition endpointDefinition) {
		this.endpointDefinition = endpointDefinition;
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
	 * @return the credentials
	 */
	public ExportCredentialsI getCredentials() {
		return credentials;
	}

	/**
	 * @param credentials the credentials to set
	 */
	public void setCredentials(ExportCredentialsI credentials) {
		this.credentials = credentials;
	}
	
	
	
}
