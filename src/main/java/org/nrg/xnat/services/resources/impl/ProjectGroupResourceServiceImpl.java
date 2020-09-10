package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.services.resources.ProjectGroupResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public class ProjectGroupResourceServiceImpl implements ProjectGroupResourceService {

	@Override
	public boolean allowPut() {
		return false;
	}

	@Override
	public boolean allowPost() {
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
	public void handlePost() {
		
	}

	@Override
	public void handlePut() {
		
	}

	@Override
	public Representation represent(Variant variant) {
		return null;
	}

}
