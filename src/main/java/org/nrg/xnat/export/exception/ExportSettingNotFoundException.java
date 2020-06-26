package org.nrg.xnat.export.exception;

/**
 * @author Mohana Ramaratnam
 *
 */
public class ExportSettingNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3244965773128470651L;

	public ExportSettingNotFoundException(String string,IllegalArgumentException illegalArgumentException) {
		super(string,illegalArgumentException);
	}
}