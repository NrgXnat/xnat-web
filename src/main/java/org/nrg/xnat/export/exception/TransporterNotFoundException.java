package org.nrg.xnat.export.exception;

/**
 * @author Mohana Ramaratnam
 *
 */
public class TransporterNotFoundException extends Exception{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransporterNotFoundException(String string,IllegalArgumentException illegalArgumentException) {
		super(string,illegalArgumentException);
	}
}
