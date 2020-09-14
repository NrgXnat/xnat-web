package org.nrg.xnat.services.resources;

import java.io.File;

import org.nrg.action.ClientException;
import org.nrg.xnat.helpers.uri.archive.ResourceURII;
import org.restlet.data.Status;

public interface TriageRestletService {
	
	public void handleGet();

	public void handleDelete();

	public void handlePut();

	public void handlePost();

	public ResourceURII convertValue(final String key) throws ClientException;

	public void workflow(boolean status, String action, String reason, String comment) throws Exception;

	public void openworkflow(boolean status, String action, String reason, String comment) throws Exception;

	public void fail(Status status, String msg);

	public String relative(final File base, final File file);

}
