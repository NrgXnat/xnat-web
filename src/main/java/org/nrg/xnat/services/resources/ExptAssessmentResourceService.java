package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface ExptAssessmentResourceService {

	boolean allowPut();

	void handlePut();

	boolean allowDelete();

	void handleDelete();

	Representation represent(Variant variant);

}