package org.nrg.xnat.services.resources.prearchive.impl;

import org.nrg.action.ClientException;
import org.nrg.xnat.services.resources.prearchive.PrearcSessionResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public class PrearcSessionResourceServiceImpl implements PrearcSessionResourceService {

	@Override
	public boolean allowPost() {
		return false;
	}

	@Override
	public boolean allowDelete() {
		return false;
	}

	@Override
	public void handlePost() {

	}

	@Override
	public void handleDelete() {

	}

	@Override
	public Representation represent(Variant variant) {
		return null;
	}

	@Override
	public void handleParam(String key, Object value) throws ClientException {

	}

}
