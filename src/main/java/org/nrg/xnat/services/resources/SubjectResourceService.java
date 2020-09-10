package org.nrg.xnat.services.resources;

import org.restlet.data.MediaType;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface SubjectResourceService {

	boolean allowPut();

	void handlePut();

	boolean allowDelete();

	void handleDelete();

	Representation represent(Variant variant);

	Representation representItem(XFTItem item, MediaType mt);

}