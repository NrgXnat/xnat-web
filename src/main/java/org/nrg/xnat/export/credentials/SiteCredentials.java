package org.nrg.xnat.export.credentials;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.nrg.xnat.export.interfaces.ExportCredentialsA;
import org.nrg.xnat.export.interfaces.ExportCredentialsI;

/**
 * @author Mohana Ramaratnam
 *
 */
public class SiteCredentials extends ExportCredentialsA implements ExportCredentialsI, Serializable {
	
	String _username;
	String _password;
	
	
	public SiteCredentials(final String username, final String password) {
		super();
		this._username = username;
		this._password = password;
	}
	
	public ExportCredentialsI getCredentials() {
		return this;
	}
	
}
