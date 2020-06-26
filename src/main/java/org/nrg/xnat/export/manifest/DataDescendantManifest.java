package org.nrg.xnat.export.manifest;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Mohana Ramaratnam
 *
 */
public class DataDescendantManifest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1693241057864605979L;
	
	private String projectId = null;
	private String subjectId = null;
	private String subjectLabel = null;
	private String experimentId = null;
	private String experimentLabel = null;
	private String scanId = null;
	private String assessorId = null;
	private String assessorLabel = null;
	private String resourceLabel = null;
	
	public DataDescendantManifest() {
		
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
	 * @return the subjectId
	 */
	public String getSubjectId() {
		return subjectId;
	}
	/**
	 * @param subjectId the subjectId to set
	 */
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	/**
	 * @return the experimentId
	 */
	public String getExperimentId() {
		return experimentId;
	}
	/**
	 * @param experimentId the experimentId to set
	 */
	public void setExperimentId(String experimentId) {
		this.experimentId = experimentId;
	}
	/**
	 * @return the scanId
	 */
	public String getScanId() {
		return scanId;
	}
	/**
	 * @param scanId the scanId to set
	 */
	public void setScanId(String scanId) {
		this.scanId = scanId;
	}
	/**
	 * @return the assessorId
	 */
	public String getAssessorId() {
		return assessorId;
	}
	/**
	 * @param assessorId the assessorId to set
	 */
	public void setAssessorId(String assessorId) {
		this.assessorId = assessorId;
	}
	
	
	
	/**
	 * @return the subjectLabel
	 */
	public String getSubjectLabel() {
		return subjectLabel;
	}
	/**
	 * @param subjectLabel the subjectLabel to set
	 */
	public void setSubjectLabel(String subjectLabel) {
		this.subjectLabel = subjectLabel;
	}
	/**
	 * @return the experimentLabel
	 */
	public String getExperimentLabel() {
		return experimentLabel;
	}
	/**
	 * @param experimentLabel the experimentLabel to set
	 */
	public void setExperimentLabel(String experimentLabel) {
		this.experimentLabel = experimentLabel;
	}
	/**
	 * @return the assessorLabel
	 */
	public String getAssessorLabel() {
		return assessorLabel;
	}
	/**
	 * @param assessorLabel the assessorLabel to set
	 */
	public void setAssessorLabel(String assessorLabel) {
		this.assessorLabel = assessorLabel;
	}
	/**
	 * @return the resourceLabel
	 */
	public String getResourceLabel() {
		return resourceLabel;
	}
	/**
	 * @param resourceLabel the resourceLabel to set
	 */
	public void setResourceLabel(String resourceLabel) {
		this.resourceLabel = resourceLabel;
	}
	
	@JsonIgnore
	public String getKey() {
		String str = this.projectId;
		if (subjectId != null) {
			str += "/" + subjectId;
		}
		if (experimentId != null) {
			str += "/" + experimentId;
		}
		if (scanId != null) {
			str += "/" + scanId;
		}
		if (assessorId != null) {
			str += "/" + assessorId;
		}
		if (resourceLabel != null) {
			str += "/" + resourceLabel;
		}
		return str;
	}
	
	@JsonIgnore
	public boolean isAProjectOnlyManifest() {
		boolean isProjectOnly = false;
		if (subjectId == null && experimentId == null && scanId == null && assessorId == null && resourceLabel == null) {
			isProjectOnly = true;
		}
		return isProjectOnly;
	}
	
	@Override
	public String toString() {
		String str = "Project: " + projectId;
		if (subjectId != null) {
			str += " Subject_ID: " + subjectId;
			str += " Subject_Label:" + subjectLabel;
		}
		if (experimentId != null) {
			str += " Experiment_ID: " + experimentId;
			str += " Experiment_Label: " + experimentLabel;
		}
		if (scanId != null) {
			str += " Scan_ID: " + scanId;
		}
		if (assessorId != null) {
			str += " Assessor_ID: "  + assessorId;
			str += " Assessor_Label: "  + assessorLabel;
		}
		if (resourceLabel != null) {
			str += " Resource_Label:" + resourceLabel;
		}
		return str;
	}

}
