package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface StudyRoutingRestletService {

	boolean allowPut();

	boolean allowDelete();

	Representation represent(Variant variant);

	void handlePut();

	void handleDelete();

}