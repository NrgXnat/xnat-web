package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.services.resources.BaseXapiService;

public abstract class BaseXapiServiceImpl implements BaseXapiService{

	@Override
	public boolean allowPut() {
		return true;
		
	}
	
	@Override
	public boolean allowDelete() {
		return true;
		
	}
	
	@Override
	public boolean allowGet() {
		return true;
		
	}
	
	@Override
	public boolean allowPost() {
		return true;
		
	}
}
