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

public class EndpointScanType extends EndpointItems implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5074476066832087669L;

	private List<String> ids;


	
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
	
}
