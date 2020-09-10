package org.nrg.xnat.services.resources.prearchive;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface RecentPrearchiveSessionsService {
	
	public boolean allowPut();

	public boolean allowPost();

	public Representation represent(final Variant variant);
}
