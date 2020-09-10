package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface ProjectMemberResourceService {

	boolean allowPost();

	void handleDelete();

	void handlePut();

	Representation represent(Variant variant);

}