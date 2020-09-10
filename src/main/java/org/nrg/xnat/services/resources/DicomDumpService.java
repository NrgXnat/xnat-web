package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public interface DicomDumpService {
	
	public boolean allowPost();

	public boolean allowPut();

	public Representation represent(final Variant variant) throws ResourceException;
}
