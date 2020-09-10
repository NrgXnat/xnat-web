package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.services.resources.EcatDumpService;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public class EcatDumpServiceImpl implements EcatDumpService {

	@Override
	public boolean allowPost() {
		return false;
	}

	@Override
	public boolean allowPut() {
		return false;
	}

	@Override
	public Representation represent(Variant variant) {
		return null;
	}

}
