package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public interface SQListenerRepresentationService {

	public void removeRepresentations() throws ResourceException;

	public Representation represent(final Variant variant) throws ResourceException;

	public void acceptRepresentation(final Representation entity) throws ResourceException;
}
