package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public interface PrearcScanResourceService {
	
	public boolean allowDelete();

	public boolean allowGet();

	public void handleDelete();

	public Representation represent(final Variant variant) throws ResourceException;
}
