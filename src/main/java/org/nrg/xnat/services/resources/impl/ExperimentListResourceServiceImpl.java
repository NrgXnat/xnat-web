package org.nrg.xnat.services.resources.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.nrg.xft.XFTTable;
import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperElement;
import org.nrg.xnat.restlet.resources.ExperimentListResource;
import org.nrg.xnat.restlet.resources.ExperimentListResource.FilteredExptListHandlerI;
import org.nrg.xnat.services.resources.ExperimentListResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public class ExperimentListResourceServiceImpl implements ExperimentListResourceService {

	@Override
	public ArrayList<String> getDefaultFields(GenericWrapperElement e) {
		return null;
	}

	@Override
	public String getDefaultElementName() {
		return null;
	}

	@Override
	public Representation represent(Variant variant) {
		return null;
	}

	@Override
	public XFTTable build(ExperimentListResource resource, Hashtable<String, Object> params) throws Exception {
		return null;
	}

	@Override
	public List<FilteredExptListHandlerI> getHandlers() throws InstantiationException, IllegalAccessException {
		return null;
	}

}
