package org.nrg.xnat.export.model.endpoint;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Mohana Ramaratnam
 *
 */

public class EndpointImagingSessionAssessor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3878378307200577442L;

	@JsonProperty("xsi_types")
	private List<EndpointImagingSessionAssessorXsiType> xsiTypes;
	


	/**
	 * @return the xsiTypes
	 */
	public List<EndpointImagingSessionAssessorXsiType> getXsiTypes() {
		return xsiTypes;
	}

	/**
	 * @param xsiTypes the xsiTypes to set
	 */
	public void setXsiTypes(List<EndpointImagingSessionAssessorXsiType> xsiTypes) {
		this.xsiTypes = xsiTypes;
	}
	
	public EndpointImagingSessionAssessorXsiType getExportedDataForImagingSessionAssessorByXsiType(String xsiType) {
		EndpointImagingSessionAssessorXsiType matchingXsiTypesDefinition = null;
		List<EndpointImagingSessionAssessorXsiType> xsiTypes = getXsiTypes();
		for (EndpointImagingSessionAssessorXsiType x: xsiTypes) {
			if (x.getXsiType().equals(xsiType)) {
				matchingXsiTypesDefinition = x;
			}
		}
		return matchingXsiTypesDefinition;
	}
	
}
