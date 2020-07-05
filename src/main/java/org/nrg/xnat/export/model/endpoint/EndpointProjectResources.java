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

public class EndpointProjectResources extends EndpointItems implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 100794384256308649L;

	public  EndpointProjectResources() {
		super();
	}
	


}
