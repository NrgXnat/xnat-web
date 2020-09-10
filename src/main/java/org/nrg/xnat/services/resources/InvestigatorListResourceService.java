package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface InvestigatorListResourceService {

	boolean allowGet();

	Representation represent(Variant variant);

}