package org.nrg.xnat.export.anonymizer.utils;

import java.io.File;

/**
 * @author Mohana Ramaratnam
 *
 */
public class AnonymizeHelper {

	File anonymizeScriptFile;
	File lookUpTable;

	public AnonymizeHelper(File a, File l) {
		this.anonymizeScriptFile = a;
		this.lookUpTable = l;
	}
	
	
	
	/**
	 * @return the anonymizeScriptFile
	 */
	public File getAnonymizeScriptFile() {
		return anonymizeScriptFile;
	}
	
	/**
	 * @return the lookUpTable
	 */
	public File getLookUpTable() {
		return lookUpTable;
	}

	
}
