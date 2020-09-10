package org.nrg.xnat.services.resources.impl;

import org.nrg.automation.runners.ScriptRunner;
import org.nrg.xnat.services.resources.ScriptRunnerResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptRunnerResourceServiceImpl implements ScriptRunnerResourceService {
	
	private static final Logger _log = LoggerFactory.getLogger(ScriptRunnerResourceServiceImpl.class);
	
	@Override
	public String getResourceType() {
		return ScriptRunner.class.getSimpleName();
	}

	@Override
	public String getResourceId() {
		return "langugae";
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException {
		_log.debug("This represent methid in Script Runner Resource Service Impl");
		return null;
	}

}
