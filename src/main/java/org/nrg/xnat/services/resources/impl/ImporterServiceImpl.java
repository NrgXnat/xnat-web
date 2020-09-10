package org.nrg.xnat.services.resources.impl;

import org.nrg.action.ClientException;
import org.nrg.xnat.archive.operations.DicomImportOperation;
import org.nrg.xnat.restlet.actions.importer.ImporterHandlerA;
import org.nrg.xnat.services.resources.ImporterService;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public class ImporterServiceImpl implements ImporterService {

	@Override
	public boolean allowGet() {
		return false;
	}

	@Override
	public boolean allowPost() {
		return false;
	}

	@Override
	public void handlePost() {
		
	}

	@Override
	public boolean storeStatusList(ImporterHandlerA importer) {
		return false;
	}

	@Override
	public boolean storeStatusList(DicomImportOperation operation) {
		return false;
	}

	@Override
	public void handleParam(String key, Object value) throws ClientException {
		
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException {
		return null;
	}

}
