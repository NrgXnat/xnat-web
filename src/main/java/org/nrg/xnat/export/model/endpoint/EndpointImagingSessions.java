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

public class EndpointImagingSessions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6449913217692330521L;

	@JsonProperty("xsi_types")
	private List<EndpointImagingSessionXsiType> xsiTypes;
	

	/**
	 * @return the xsiTypes
	 */
	public List<EndpointImagingSessionXsiType> getXsiTypes() {
		return xsiTypes;
	}

	/**
	 * @param xsiTypes the xsiTypes to set
	 */
	public void setXsiTypes(List<EndpointImagingSessionXsiType> xsiTypes) {
		this.xsiTypes = xsiTypes;
	}
	
	public EndpointImagingSessionXsiType getExportedDataForImagingSessionByXsiType(String xsiType) {
		EndpointImagingSessionXsiType matchingXsiTypesDefinition = null;
		List<EndpointImagingSessionXsiType> xsiTypes = getXsiTypes();
		for (EndpointImagingSessionXsiType x: xsiTypes) {
			if (x.getXsiType().equals(xsiType)) {
				matchingXsiTypesDefinition = x;
			}
		}
		return matchingXsiTypesDefinition;
	}

}
