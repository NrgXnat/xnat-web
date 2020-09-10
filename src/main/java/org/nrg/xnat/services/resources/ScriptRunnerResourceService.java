package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public interface ScriptRunnerResourceService {
	
	public String getResourceType();

	public String getResourceId();

	public Representation represent(Variant variant) throws ResourceException;
}
