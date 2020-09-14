package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface ProjectGroupResourceService {
	
	public void handleDelete();

	public void handlePost();

	public void handlePut();

	public Representation represent(Variant variant);
}
