package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.services.resources.ProtocolResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public class ProtocolResourceServiceImpl implements ProtocolResourceService{

	@Override
	public boolean allowPut() {
		return false;
	}

	@Override
	public boolean allowDelete() {
		return false;
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException {
		return null;
	}

	@Override
	public void handlePut() {
		
	}

	@Override
	public void handleDelete() {
		// TODO Auto-generated method stub
		
	}
	
}
