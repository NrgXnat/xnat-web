package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.services.resources.ProjectPipelineListResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public class ProjectPipelineListResourceServiceImpl implements ProjectPipelineListResourceService{

	@Override
	public boolean allowGet() {
		return false;
	}

	@Override
	public boolean allowDelete() {
		return false;
	}

	@Override
	public void handleDelete() {
		
	}

	@Override
	public Representation represent(Variant variant) {
		return null;
	}

}
