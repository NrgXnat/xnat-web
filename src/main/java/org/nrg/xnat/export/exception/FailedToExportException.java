package org.nrg.xnat.export.exception;

/**
 * @author Mohana Ramaratnam
 *
 */
public class FailedToExportException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4901965983527030044L;

	public FailedToExportException(String string,IllegalArgumentException illegalArgumentException) {
		super(string,illegalArgumentException);
	}
}
