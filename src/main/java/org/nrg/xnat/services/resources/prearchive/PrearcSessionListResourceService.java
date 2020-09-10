package org.nrg.xnat.services.resources.prearchive;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface PrearcSessionListResourceService {
	
	public boolean allowPut();

	public void handlePut();

	public Representation represent(final Variant variant);
}
