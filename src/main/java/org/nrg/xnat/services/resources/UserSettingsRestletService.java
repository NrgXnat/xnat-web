package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public interface UserSettingsRestletService {
	
	public void handlePut();

	public void handlePost();

	public void handleDelete();

	public Representation represent(Variant variant) throws ResourceException;
}
