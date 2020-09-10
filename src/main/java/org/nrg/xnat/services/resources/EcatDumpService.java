package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface EcatDumpService {
	
	public boolean allowPost();

	public boolean allowPut();

	public Representation represent(final Variant variant);
}
