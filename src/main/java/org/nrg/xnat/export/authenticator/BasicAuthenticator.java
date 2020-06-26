package org.nrg.xnat.export.authenticator;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * @author Mohana Ramaratnam
 *
 */
public class BasicAuthenticator extends Authenticator {
	
	String username = null;
	String password = null;
	
	public BasicAuthenticator(final String uName, final String pWord) {
		username = uName;
		password = pWord;
	}
	
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password.toCharArray());
	}

}