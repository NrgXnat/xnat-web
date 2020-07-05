package org.nrg.xnat.export.model.endpoint;

import java.io.Serializable;

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

public class EndpointXsiType implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 5044082363398634600L;


	@JsonProperty("xsi_type")
	private String xsiType;
	
	private EndpointItems resources;
	
	/**
	 * @return the xsiType
	 */
	public String getXsiType() {
		return xsiType;
	}
	/**
	 * @param xsiType the xsiType to set
	 */
	public void setXsiType(String xsiType) {
		this.xsiType = xsiType;
	}
	/**
	 * @return the resources
	 */
	public EndpointItems getResources() {
		return resources;
	}
	/**
	 * @param resources the resources to set
	 */
	public void setResources(EndpointItems resources) {
		this.resources = resources;
	}
	
}
