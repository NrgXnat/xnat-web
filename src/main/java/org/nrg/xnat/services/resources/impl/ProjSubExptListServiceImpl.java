package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.services.resources.ProjSubExptListService;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public class ProjSubExptListServiceImpl implements ProjSubExptListService {

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
