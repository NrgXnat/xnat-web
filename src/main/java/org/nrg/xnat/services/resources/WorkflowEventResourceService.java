package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public interface WorkflowEventResourceService {

	String getResourceType();

	String getResourceId();

	Representation represent(Variant variant) throws ResourceException;

}