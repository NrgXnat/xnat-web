package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.services.resources.ScanDIRResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public class ScanDIRResourceServiceImpl implements ScanDIRResourceService {

	@Override
	public boolean allowPut() {
		return false;
	}

	@Override
	public Representation represent(Variant variant) {
		// TODO Auto-generated method stub
		return null;
	}

}
