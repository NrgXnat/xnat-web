package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.helpers.editscript.DicomEdit.ResourceScope;
import org.nrg.xnat.services.resources.DicomEditService;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public class DicomEditServiceImpl extends BaseXapiServiceImpl implements DicomEditService {

	@Override
	public String buildScriptPath(ResourceScope scope, Object identifier) {
		return null;
	}

	@Override
	public String getProjectScriptPath(Object project) {
		return null;
	}

	@Override
	public String getStudyScriptPath(Object study) {
		return null;
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException {
		return null;
	}

	@Override
	public void handlePut() {
		
	}

}
