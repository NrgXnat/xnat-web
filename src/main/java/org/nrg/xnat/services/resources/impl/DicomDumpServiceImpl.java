package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.services.resources.DicomDumpService;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public class DicomDumpServiceImpl implements DicomDumpService {

	@Override
	public boolean allowPost() {
		return false;
	}

	@Override
	public boolean allowPut() {
		return false;
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException {
		return null;
	}

}
