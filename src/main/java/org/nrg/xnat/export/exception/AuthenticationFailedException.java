package org.nrg.xnat.export.exception;

/**
 * @author Mohana Ramaratmam
 *
 */
public class AuthenticationFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2250173138672277445L;

	public AuthenticationFailedException(String string){
		super(string);
	}
}

