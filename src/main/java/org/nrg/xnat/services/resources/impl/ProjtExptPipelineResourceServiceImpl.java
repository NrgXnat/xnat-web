package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.services.resources.ProjtExptPipelineResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public class ProjtExptPipelineResourceServiceImpl implements ProjtExptPipelineResourceService {

	@Override
	public boolean allowPost() {
		return false;
	}

	@Override
	public void handlePost() {
		
	}

	@Override
	public Representation represent(Variant variant) {
		return null;
	}

}
