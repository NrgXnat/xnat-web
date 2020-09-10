package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.services.resources.IpWhitelistService;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public class IpWhitelistServiceImpl implements IpWhitelistService {

	@Override
	public boolean allowPut() {
		return false;
	}

	@Override
	public void handlePut() {

	}

	@Override
	public Representation represent(Variant variant) throws ResourceException {
		return null;
	}

}
