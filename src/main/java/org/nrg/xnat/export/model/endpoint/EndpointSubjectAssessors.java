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
@Entity
public class EndpointSubjectAssessors implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1655755593968231433L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@JsonIgnore
	private Integer pKey;
	
	/**
	 * @return the id
	 */
	public Integer getpKey() {
		return pKey;
	}

	/**
	 * @param id the id to set
	 */
	public void setpKey(Integer id) {
		this.pKey = id;
	}


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
