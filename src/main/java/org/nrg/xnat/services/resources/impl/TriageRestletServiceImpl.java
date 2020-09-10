package org.nrg.xnat.services.resources.impl;

import java.io.File;

import org.nrg.action.ClientException;
import org.nrg.xnat.helpers.uri.archive.ResourceURII;
import org.nrg.xnat.services.resources.TriageRestletService;
import org.restlet.data.Status;

public class TriageRestletServiceImpl implements TriageRestletService {

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
	public void handlePut() {
		
	}

	@Override
	public void handlePost() {
		
	}

	@Override
	public ResourceURII convertValue(String key) throws ClientException {
		return null;
	}

	@Override
	public void workflow(boolean status, String action, String reason, String comment) throws Exception {
		
	}

	@Override
	public void openworkflow(boolean status, String action, String reason, String comment) throws Exception {
		
	}

	@Override
	public void fail(Status status, String msg) {
		
	}

	@Override
	public String relative(File base, File file) {
		return null;
	}

}
