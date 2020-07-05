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

public class EndpointInput implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1282167124305413569L;

	private List<EndpointSettingItem> inputs;
	


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
