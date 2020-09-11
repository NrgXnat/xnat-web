package org.nrg.xnat.services.resources.impl;

import org.nrg.automation.entities.ScriptTriggerTemplate;
import org.nrg.xnat.services.resources.ScriptTriggerTemplateResourceService;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptTriggerTemplateResourceServiceImpl extends BaseXapiServiceImpl implements ScriptTriggerTemplateResourceService {
	
	private static final Logger _log = LoggerFactory.getLogger(ScriptTriggerTemplateResourceServiceImpl.class);
	
	@Override
	public String getResourceType() {
		return ScriptTriggerTemplate.class.getSimpleName();
	}

	@Override
	public String getResourceId() {
		return "templateId";
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException {
		_log.debug("This is an Representation method in Script Trigger Template Resource Service Impl");
		return null;
	}

	@Override
	public void handlePut() {
		_log.debug("This is an handle put method in Script Trigger Template Resource Service Impl");
	}

	@Override
	public void handleDelete() {
		_log.debug("This is an handle delete method in Script Trigger Template Resource Service Impl");
	}

}
