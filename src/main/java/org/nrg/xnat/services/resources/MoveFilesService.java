package org.nrg.xnat.services.resources;

import org.nrg.action.ClientException;
import org.nrg.xnat.helpers.uri.URIManager;
import org.nrg.xnat.helpers.uri.archive.ResourceURII;

public interface MoveFilesService {
	
	public boolean allowPost();

	public void handlePost();

	public void handleParam(final String key, final Object value) throws ClientException;

	public URIManager.UserCacheURI convertKey(final String key) throws ClientException;

	public ResourceURII convertValue(final String key) throws ClientException;

}
