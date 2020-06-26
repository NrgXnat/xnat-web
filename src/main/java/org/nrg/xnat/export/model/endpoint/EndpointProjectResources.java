package org.nrg.xnat.export.model.endpoint;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Mohana Ramaratnam
 *
 */
@Entity

public class EndpointProjectResources extends EndpointItems implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 100794384256308649L;

	public  EndpointProjectResources() {
		super();
	}
	
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


}
