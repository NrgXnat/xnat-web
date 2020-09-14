package org.nrg.xnat.services.resources.impl;

import org.nrg.action.ClientException;
import org.nrg.xnat.services.resources.MailRestletService;

public class MailRestletServiceImpl extends BaseXapiServiceImpl implements MailRestletService{

	@Override
	public boolean allowPost() {
		return false;
	}

	@Override
	public void handlePost() {
		
	}

	@Override
	public void handleParam(String key, Object value) throws ClientException {
		
	}

}
