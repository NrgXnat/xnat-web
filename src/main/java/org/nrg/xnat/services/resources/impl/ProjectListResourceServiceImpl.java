package org.nrg.xnat.services.resources.impl;

import java.util.ArrayList;

import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperElement;
import org.nrg.xnat.services.resources.ProjectListResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public class ProjectListResourceServiceImpl extends BaseXapiServiceImpl implements ProjectListResourceService {

	@Override
	public void handlePost() {
		
	}

	@Override
	public String getDefaultElementName() {
		return null;
	}

	@Override
	public ArrayList<String> getDefaultFields(GenericWrapperElement e) {
		return null;
	}

	@Override
	public Representation represent(Variant variant) {
		return null;
	}

}
