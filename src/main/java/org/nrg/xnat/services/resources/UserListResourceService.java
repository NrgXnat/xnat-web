package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface UserListResourceService {
	
	public boolean allowGet();

	public Representation represent(Variant variant);
}
