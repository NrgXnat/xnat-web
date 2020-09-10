package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.services.resources.ProjectAccessibilityResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public class ProjectAccessibilityResourceServiceImpl implements ProjectAccessibilityResourceService {

	@Override
	public boolean allowGet() {
		return false;
	}

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
