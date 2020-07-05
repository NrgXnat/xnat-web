package org.nrg.xnat.export.model.endpoint;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.nrg.xnat.export.utils.ExportConstants;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Mohana Ramaratnam
 *
 */
public class EndpointExportedData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9018412439679995267L;



	@JsonProperty("project_resources")
	private EndpointProjectResources projectResources;

	@JsonProperty("subject_resources")
	private EndpointSubjectResources subjectResources;
	
	@JsonProperty("subject_assessors")
	private EndpointSubjectAssessors subjectAssessors;
	
	@JsonProperty("imaging_sessions")
	private EndpointImagingSessions imagingSessions;
	/**
	 * @return the projectResources
	 */
	public EndpointProjectResources getProjectResources() {
		return projectResources;
	}
	/**
	 * @param projectResources the projectResources to set
	 */
	public void setProjectResources(EndpointProjectResources projectResources) {
		this.projectResources = projectResources;
	}
	/**
	 * @return the subjectResources
	 */
	public EndpointSubjectResources getSubjectResources() {
		return subjectResources;
	}
	/**
	 * @param subjectResources the subjectResources to set
	 */
	public void setSubjectResources(EndpointSubjectResources subjectResources) {
		this.subjectResources = subjectResources;
	}
	/**
	 * @return the subjectAssessors
	 */
	public EndpointSubjectAssessors getSubjectAssessors() {
		return subjectAssessors;
	}
	/**
	 * @param subjectAssessors the subjectAssessors to set
	 */
	public void setSubjectAssessors(EndpointSubjectAssessors subjectAssessors) {
		this.subjectAssessors = subjectAssessors;
	}
	/**
	 * @return the imagingSessions
	 */
	public EndpointImagingSessions getImagingSessions() {
		return imagingSessions;
	}
	/**
	 * @param imagingSessions the imagingSessions to set
	 */
	public void setImagingSessions(EndpointImagingSessions imagingSessions) {
		this.imagingSessions = imagingSessions;
	}
	
	//Convenience Methods
	
	/*
	 * Gets the Subject Assessor for the given xsiType
	 */
	public EndpointIdXsiType getExportedDataForSubjectAssessorByXsiType(String xsiType) {
		EndpointIdXsiType matchingXsiTypesDefinition = null;
		List<EndpointIdXsiType> xsiTypes = subjectAssessors.getXsiTypes();
		for (EndpointIdXsiType x: xsiTypes) {
			if (x.getXsiType().equals(xsiType)) {
				matchingXsiTypesDefinition = x;
			}
		}
		return matchingXsiTypesDefinition;
	}

	/*
	 * Gets the Imaging Session for the given xsiType
	 */

	public EndpointImagingSessionXsiType getExportedDataForImagingSessionByXsiType(String xsiType) {
		EndpointImagingSessionXsiType matchingXsiTypesDefinition = null;
		List<EndpointImagingSessionXsiType> xsiTypes = imagingSessions.getXsiTypes();
		if (xsiTypes != null && xsiTypes.size() == 1 && xsiTypes.get(0).getXsiType().equals(ExportConstants.EXPORT_ALL_XSITYPE)) {
			matchingXsiTypesDefinition = xsiTypes.get(0);
		}else {
			for (EndpointImagingSessionXsiType x: xsiTypes) {
				if (x.getXsiType().equals(xsiType)) {
					matchingXsiTypesDefinition = x;
					break;
				}
			}
		}
		return matchingXsiTypesDefinition;
	}
	
	
	/*
	 * This method looks at the "ids" field at the subject level
	 * If this is not present, all subjects are to be exported. 
	 * If the "ids" field is present, only the a subset identified by ids is sent.
	 */
	public boolean exportAllSubjects() throws NullPointerException {
		boolean exportAll = true;
		List<String> subjectIds = getSubjectResources().getIds();
		if (null != subjectIds && subjectIds.size() >= 1 ) {
			exportAll = false;
		}
		return exportAll;
	}

	


}
