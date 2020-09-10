package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public interface UserSessionService {

	boolean allowDelete();

	boolean allowPost();

	void removeRepresentations();

	void acceptRepresentation(Representation entity);

	Representation represent(Variant variant) throws ResourceException;

}