package org.nrg.xnat.export.exception;

/**
 * @author Mohana Ramaratnam
 *
 */
public class ExporterNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2574131840559037780L;

	public ExporterNotFoundException(String string,IllegalArgumentException illegalArgumentException) {
		super(string,illegalArgumentException);
	}
}