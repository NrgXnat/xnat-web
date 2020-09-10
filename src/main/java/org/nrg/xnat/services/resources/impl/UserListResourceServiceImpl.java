package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.services.resources.UserListResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public class UserListResourceServiceImpl implements UserListResourceService {

	@Override
	public boolean allowGet() {
		return false;
	}

	@Override
	public Representation represent(Variant variant) {
		return null;
	}

}
