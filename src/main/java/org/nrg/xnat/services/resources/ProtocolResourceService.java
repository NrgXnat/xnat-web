package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public interface ProtocolResourceService {
	
	public Representation represent(final Variant variant) throws ResourceException;

	public void handlePut();

	public void handleDelete();
}
