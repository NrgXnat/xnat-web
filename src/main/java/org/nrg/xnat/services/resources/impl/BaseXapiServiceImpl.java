package org.nrg.xnat.services.resources.impl;

public abstract class BaseXapiServiceImpl{

	public boolean allowPut() {
		return true;
		
	}
	
	public boolean allowDelete() {
		return true;
		
	}
	
	public boolean allowGet() {
		return true;
		
	}
	
	public boolean allowPost() {
		return true;
		
	}
}
