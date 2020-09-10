package org.nrg.xnat.services.resources.impl;

import org.nrg.automation.entities.Script;
import org.nrg.xnat.services.resources.ScriptResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptResourceServiceImpl implements ScriptResourceService {

	private static final Logger _log = LoggerFactory.getLogger(ScriptResourceServiceImpl.class);
	@Override
	public String getResourceType() {
		return Script.class.getSimpleName();
	}

	@Override
	public String getResourceId() {
		return "scriptId";
	}

	@Override
	public boolean allowPut() {
		return true;
	}

	@Override
	public boolean allowDelete() {
		return true;
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException {
		_log.debug("This is an Representation method in Script Resource Service Impl");
		return null;
	}

	@Override
	public void handlePut() {
		_log.debug("This is an handle put method in Script Resource Service Impl");
	}

	@Override
	public void handleDelete() {
		_log.debug("This is an handle delete method in Script Resource Service Impl");
	}

}
