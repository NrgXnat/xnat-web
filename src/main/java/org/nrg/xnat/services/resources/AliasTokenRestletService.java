package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public interface AliasTokenRestletService {
	
	public Representation represent() throws ResourceException;

	public Representation represent(Variant variant) throws ResourceException;
}
