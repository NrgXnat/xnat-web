package org.nrg.xnat.services.resources.prearchive;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface PrearcScanResourceListService {
	
	public Representation getRepresentation(Variant variant);
}
