package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface UserFavoriteResourceService {
	
	public boolean allowGet();

	public void handlePut();

	public void handleDelete();

	public Representation getRepresentation(Variant variant);
}
