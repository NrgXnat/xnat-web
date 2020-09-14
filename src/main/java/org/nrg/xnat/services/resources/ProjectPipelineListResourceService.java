package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface ProjectPipelineListResourceService {
	
	public void handleDelete();

	public Representation represent(Variant variant);
}
