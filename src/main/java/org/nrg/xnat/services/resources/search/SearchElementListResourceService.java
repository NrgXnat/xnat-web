package org.nrg.xnat.services.resources.search;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface SearchElementListResourceService {

	Representation represent(Variant variant);

}