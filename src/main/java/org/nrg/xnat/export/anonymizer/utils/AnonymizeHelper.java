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
	 * @param anonymizeScriptFile the anonymizeScriptFile to set
	 */
	public void setAnonymizeScriptFile(File anonymizeScriptFile) {
		this.anonymizeScriptFile = anonymizeScriptFile;
	}
	/**
	 * @return the lookUpTable
	 */
	public File getLookUpTable() {
		return lookUpTable;
	}
	/**
	 * @param lookUpTable the lookUpTable to set
	 */
	public void setLookUpTable(File lookUpTable) {
		this.lookUpTable = lookUpTable;
	}

	
}
