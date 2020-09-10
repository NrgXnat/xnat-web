package org.nrg.xnat.services.resources.prearchive.impl;

import org.nrg.xnat.services.resources.prearchive.PrearcScanResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public class PrearcScanResourceServiceImpl implements PrearcScanResourceService {

	@Override
	public boolean allowDelete() {
		return false;
	}

	@Override
	public boolean allowGet() {
		return false;
	}

	@Override
	public void handleDelete() {
		
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException {
		return null;
	}

}
