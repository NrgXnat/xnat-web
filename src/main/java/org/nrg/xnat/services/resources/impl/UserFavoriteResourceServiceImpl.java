package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.services.resources.UserFavoriteResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public class UserFavoriteResourceServiceImpl extends BaseXapiServiceImpl implements UserFavoriteResourceService {

	@Override
	public boolean allowGet() {
		return false;
	}

	@Override
	public void handlePut() {
		
	}

	@Override
	public void handleDelete() {
		
	}

	@Override
	public Representation getRepresentation(Variant variant) {
		return null;
	}

}
