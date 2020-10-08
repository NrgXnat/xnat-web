package org.nrg.xnat.services.resources;

import java.io.IOException;

import org.nrg.action.ClientException;

public interface ExperimentResourceService {
	
	public String getExperimentResource(String experimentId, String resourceId) throws IOException, Exception;

	public String getExperimentResourceFiles(String experimentId, String resourceId) throws ClientException, IOException;
}