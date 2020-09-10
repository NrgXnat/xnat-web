package org.nrg.xnat.services.resources.prearchive.impl;

import org.nrg.xnat.services.resources.prearchive.PrearcSessionListResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public class PrearcSessionListResourceServiceImpl implements PrearcSessionListResourceService {

	@Override
	public boolean allowPut() {
		return false;
	}

	@Override
	public void handlePut() {

	}

	@Override
	public Representation represent(Variant variant) {
		return null;
	}

}
