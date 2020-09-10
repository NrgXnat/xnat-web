package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.services.resources.FeatureDefinitionRestletService;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public class FeatureDefinitionRestletServiceImpl implements FeatureDefinitionRestletService {

	@Override
	public boolean allowPost() {
		return false;
	}

	@Override
	public void handlePost() {
		
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException {
		return null;
	}

}
