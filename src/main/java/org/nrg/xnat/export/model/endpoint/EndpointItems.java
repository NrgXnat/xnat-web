package org.nrg.xnat.export.model.endpoint;

import java.io.Serializable;
import java.util.Hashtable;
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
public  class EndpointItems implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5144369147158643546L;

	protected EndpointItems() {
		
	}

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
	 * @return the items
	 */
	public List<String> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<String> items) {
		this.items = items;
	}
	
	public Hashtable<String, String> toHash() {
		Hashtable<String,String> hash = new Hashtable<String, String>();
		for (String i: items) {
			hash.put(i, "");
		}
		return hash;
	}

	private List<String> items;

}
