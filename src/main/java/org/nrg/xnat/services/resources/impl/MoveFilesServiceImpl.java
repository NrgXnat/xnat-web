package org.nrg.xnat.services.resources.impl;

import org.nrg.action.ClientException;
import org.nrg.xnat.helpers.uri.URIManager.UserCacheURI;
import org.nrg.xnat.helpers.uri.archive.ResourceURII;
import org.nrg.xnat.services.resources.MoveFilesService;

public class MoveFilesServiceImpl extends BaseXapiServiceImpl implements MoveFilesService {

	@Override
	public void handlePost() {
		
	}

	@Override
	public void handleParam(String key, Object value) throws ClientException {
		
	}

	@Override
	public UserCacheURI convertKey(String key) throws ClientException {
		return null;
	}

	@Override
	public ResourceURII convertValue(String key) throws ClientException {
		return null;
	}

}
