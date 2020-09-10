package org.nrg.xnat.services.resources;

import org.nrg.action.ClientException;

public interface MailRestletService {
	
	public boolean allowGet();

	public boolean allowPost();

	public void handlePost();

	public void handleParam(final String key, final Object value) throws ClientException;
}
