package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.services.resources.SQListenerRepresentationService;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public class SQListenerRepresentationServiceImpl implements SQListenerRepresentationService {

	@Override
	public boolean allowDelete() {
		return false;
	}

	@Override
	public boolean allowPost() {
		return false;
	}

	@Override
	public void removeRepresentations() throws ResourceException {
		
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException {
		return null;
	}

	@Override
	public void acceptRepresentation(Representation entity) throws ResourceException {
		
	}

}
