package org.nrg.xnat.services.resources;

import org.nrg.action.ClientException;
import org.nrg.xnat.archive.operations.DicomImportOperation;
import org.nrg.xnat.restlet.actions.importer.ImporterHandlerA;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public interface ImporterService {
	
	public boolean allowGet();

	public void handlePost();

	public boolean storeStatusList(final ImporterHandlerA importer);

	public boolean storeStatusList(final DicomImportOperation operation);

	public void handleParam(String key, Object value) throws ClientException;

	public Representation represent(Variant variant) throws ResourceException;
}
