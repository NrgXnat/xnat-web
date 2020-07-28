package org.nrg.xnat.export.anonymizer.utils;

import java.io.File;

/**
 * @author Mohana Ramaratnam
 *
 */
public class AnonymizeHelper {

	File anonymizeScriptFile = null;
	File lookUpTable = null;
	String anonStr = null;
	String lookupStr = null;
	
	public AnonymizeHelper(File a, File l) {
		this.anonymizeScriptFile = a;
		this.lookUpTable = l;
	}

	public AnonymizeHelper(String a, String l) {
		this.anonStr = a;
		this.lookupStr = l;
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

	/**
	 * @return the anonStr
	 */
	public String getAnonStr() {
		return anonStr;
	}

	/**
	 * @return the lookupStr
	 */
	public String getLookupStr() {
		return lookupStr;
	}


	
}
