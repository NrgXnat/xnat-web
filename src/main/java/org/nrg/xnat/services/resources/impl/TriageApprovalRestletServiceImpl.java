package org.nrg.xnat.services.resources.impl;

import org.nrg.action.ClientException;
import org.nrg.xnat.helpers.uri.URIManager.DataURIA;
import org.nrg.xnat.helpers.uri.archive.ResourceURII;
import org.nrg.xnat.services.resources.TriageApprovalRestletService;
import org.nrg.xnat.services.triage.TriageService;

public class TriageApprovalRestletServiceImpl implements TriageApprovalRestletService{

	@Override
	public boolean allowPost() {
		return false;
	}

	@Override
	public boolean allowPut() {
		return false;
	}

	@Override
	public void handlePut() {
		
	}

	@Override
	public void handlePost() {
		
	}

	@Override
	public void handleParam(String key, Object value) throws ClientException {
		
	}

	@Override
	public DataURIA convertKey(String key) throws ClientException {
		return null;
	}

	@Override
	public ResourceURII convertValue(String key) throws ClientException {
		return null;
	}

	@Override
	public void setTriageService(TriageService triageService) {
		// TODO Auto-generated method stub
		
	}

}
