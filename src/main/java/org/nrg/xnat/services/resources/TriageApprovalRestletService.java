package org.nrg.xnat.services.resources;

import org.nrg.action.ClientException;
import org.nrg.xnat.helpers.uri.URIManager;
import org.nrg.xnat.helpers.uri.archive.ResourceURII;
import org.nrg.xnat.services.triage.TriageService;

public interface TriageApprovalRestletService {

	public void handlePut();

	public void handlePost();

	public void handleParam(final String key, final Object value) throws ClientException;

	public URIManager.DataURIA convertKey(final String key) throws ClientException;

	public ResourceURII convertValue(final String key) throws ClientException;

	public void setTriageService(TriageService triageService);
}
