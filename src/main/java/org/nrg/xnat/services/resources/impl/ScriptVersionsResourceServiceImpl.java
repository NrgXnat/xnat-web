package org.nrg.xnat.services.resources.impl;

import org.nrg.automation.entities.Script;
import org.nrg.xnat.services.resources.ScriptVersionsResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptVersionsResourceServiceImpl implements ScriptVersionsResourceService {
	
	 private static final Logger _log = LoggerFactory.getLogger(ScriptVersionsResourceServiceImpl.class);
	@Override
	public String getResourceType() {
		return  Script.class.getSimpleName();
	}

	@Override
	public String getResourceId() {
		return "scriptId";
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException {
		_log.debug("This is a represent method in Script Versions Resource Service Impl ");
		return null;
	}

}
