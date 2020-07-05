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

public class EndpointSubjectResources extends EndpointItems implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3741951568220458783L;

	public  EndpointSubjectResources() {
		super();
	}
	
	

	/**
	 * @return the ids
	 */
	public List<String> getIds() {
		return ids;
	}



	/**
	 * @param ids the ids to set
	 */
	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	private List<String> ids;


}
