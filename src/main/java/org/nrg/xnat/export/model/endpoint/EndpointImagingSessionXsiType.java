package org.nrg.xnat.export.model.endpoint;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * @author Mohana Ramaratnam
 *
 */
@Entity

public class EndpointImagingSessionXsiType extends EndpointXsiType implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2405597157915412147L;


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@JsonIgnore
	private Integer pKey;
	
	/**
	 * @return the pKey
	 */
	public Integer getpKey() {
		return pKey;
	}

	/**
	 * @param id the pKey to set
	 */
	public void setpKey(Integer id) {
		this.pKey = id;
	}

	
	@JsonProperty("session_assessors")
	private EndpointImagingSessionAssessor sessionAssessors;

	
	@JsonProperty("scan_types")
	private EndpointImagingSessionScanType scanTypes;

	@JsonProperty("scan_resources")
	private EndpointItems scanResources;

	private List<String> ids;
	/**
	 * @return the sessionAssessors
	 */
	public EndpointImagingSessionAssessor getSessionAssessors() {
		return sessionAssessors;
	}
	/**
	 * @param sessionAssessors the sessionAssessors to set
	 */
	public void setSessionAssessors(EndpointImagingSessionAssessor sessionAssessors) {
		this.sessionAssessors = sessionAssessors;
	}
	/**
	 * @return the scanTypes
	 */
	public EndpointImagingSessionScanType getScanTypes() {
		return scanTypes;
	}
	/**
	 * @param scanTypes the scanTypes to set
	 */
	public void setScanTypes(EndpointImagingSessionScanType scanTypes) {
		this.scanTypes = scanTypes;
	}
	/**
	 * @return the scanResources
	 */
	public EndpointItems getScanResources() {
		return scanResources;
	}
	/**
	 * @param scanResources the scanResources to set
	 */
	public void setScanResources(EndpointItems scanResources) {
		this.scanResources = scanResources;
	}
	/**
	 * @return the ids
	 */
	public List<String> getIds() {
		return ids;
	}
	/**
	 * @param ids the ids to set
	 */
	public void setIds(List<String> ids) {
		this.ids = ids;
	}
	
	
	
}
