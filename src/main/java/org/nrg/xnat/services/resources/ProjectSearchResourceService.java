package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface ProjectSearchResourceService {

	Representation represent(Variant variant);

	boolean allowDelete();

	boolean allowPut();

	void handlePut();

	void handleDelete();

}