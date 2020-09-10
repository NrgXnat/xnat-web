package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface WorkflowsRestletService {

	public boolean allowPost();

	public boolean allowPut();

	public boolean allowDelete();

	public Representation represent(Variant variant);

}
