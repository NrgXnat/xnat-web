package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface WorkflowResourceService {
	
	public void handlePut();;

	public Representation represent(Variant variant);
}
