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
public class EndpointSubjectAssessors implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1655755593968231433L;


	@JsonProperty("xsi_types")
	private List<EndpointIdXsiType> xsiTypes;

	/**
	 * @return the xsiTypes
	 */
	public List<EndpointIdXsiType> getXsiTypes() {
		return xsiTypes;
	}

	/**
	 * @param xsiTypes the xsiTypes to set
	 */
	public void setXsiTypes(List<EndpointIdXsiType> xsiTypes) {
		this.xsiTypes = xsiTypes;
	}
}
