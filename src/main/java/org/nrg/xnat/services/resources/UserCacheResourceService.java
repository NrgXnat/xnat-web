package org.nrg.xnat.services.resources;

import org.restlet.data.Status;

public interface UserCacheResourceService {

	public void handleGet();

	public void handleDelete();

	public void handlePost();

	public void handlePut();

	public void fail(final Status status, final String message);

	public void success(Status status, String msg);
}
