package org.nrg.xnat.services.resources.search;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface SearchFieldListResourceService {

	boolean allowPut();

	void handlePut();

	Representation getRepresentation(Variant variant);

}