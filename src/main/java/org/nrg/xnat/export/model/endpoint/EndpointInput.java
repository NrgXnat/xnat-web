package org.nrg.xnat.export.model.endpoint;

import java.io.Serializable;
import java.util.List;

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

public class EndpointInput implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1282167124305413569L;

	private List<EndpointSettingItem> inputs;
	
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


	/**
	 * @return the inputs
	 */
	public List<EndpointSettingItem> getInputs() {
		return inputs;
	}

	/**
	 * @param inputs the inputs to set
	 */
	public void setInputs(List<EndpointSettingItem> inputs) {
		this.inputs = inputs;
	}
	
	
}
