package org.nrg.xnat.services.resources.files;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface CatalogResourceService {

	boolean allowPut();

	boolean allowPost();

	boolean allowDelete();

	Representation represent(Variant variant);

	void handlePut();

	void handlePost();

	void handleDelete();

}