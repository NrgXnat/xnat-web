package org.nrg.xnat.services.resources.impl;

import java.util.ArrayList;

import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperElement;
import org.nrg.xnat.services.resources.SubjectListResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public class SubjectListResourceServiceImpl implements SubjectListResourceService {

	@Override
	public Representation represent(Variant variant) {
		return null;
	}

	@Override
	public ArrayList<String> getDefaultFields(GenericWrapperElement e) {
		return null;
	}

}
