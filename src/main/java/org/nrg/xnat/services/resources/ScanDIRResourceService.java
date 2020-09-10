package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface ScanDIRResourceService {

	boolean allowPut();

	Representation represent(Variant variant);

}