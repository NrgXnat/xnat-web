package org.nrg.xnat.services.resources.files;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface CatalogResourceListService {

	boolean allowPut();

	boolean allowPost();

	void handlePut();

	void handlePost();

	Representation represent(Variant variant);

}