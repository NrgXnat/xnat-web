package org.nrg.xnat.services.resources.impl;

import org.nrg.xnat.services.resources.UserCacheResourceService;
import org.restlet.data.Status;

public class UserCacheResourceServiceImpl implements UserCacheResourceService {

	@Override
	public boolean allowGet() {
		return false;
	}

	@Override
	public boolean allowPut() {
		return false;
	}

	@Override
	public boolean allowPost() {
		return false;
	}

	@Override
	public boolean allowDelete() {
		return false;
	}

	@Override
	public void handleGet() {
		
	}

	@Override
	public void handleDelete() {
		
	}

	@Override
	public void handlePost() {
		
	}

	@Override
	public void handlePut() {
		
	}

	@Override
	public void fail(Status status, String message) {
		
	}

	@Override
	public void success(Status status, String msg) {
		
	}

}
