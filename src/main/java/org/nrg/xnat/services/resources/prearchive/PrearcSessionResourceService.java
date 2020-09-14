package org.nrg.xnat.services.resources.prearchive;

import org.nrg.action.ClientException;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface PrearcSessionResourceService {
	
	public void handlePost();

	public void handleDelete();

	public Representation represent(final Variant variant);

	public void handleParam(final String key, final Object value) throws ClientException;
}
