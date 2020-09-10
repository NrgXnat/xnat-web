package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public interface UserRolesRestletService {
	
	public boolean allowPost();

	public void handlePost();

	public Representation represent(Variant variant) throws ResourceException;
}
