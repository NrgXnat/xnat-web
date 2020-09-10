package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.services.resources.ProjectResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public class ProjectResourceServiceImpl implements ProjectResourceService {

	@Override
	public boolean allowDelete() {
		return false;
	}

	@Override
	public boolean allowPut() {
		return false;
	}

	@Override
	public void handleDelete() {

	}

	@Override
	public void handlePut() {

	}

	@Override
	public Representation represent(Variant variant) {
		return null;
	}

	@Override
	public String getProjectId() {
		return null;
	}

}
