package org.nrg.xnat.services.resources;

import java.io.IOException;

import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public interface ExperimentResourceService {

	//boolean isModifiable();

	//Representation represent(Variant variant) throws ResourceException;

	//void handlePut();

	//void handleDelete();
	public String getExperimentResource(String experimentId, String resourceId) throws IOException, Exception;
}