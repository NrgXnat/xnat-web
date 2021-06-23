package org.nrg.xnat.services.extensions;

import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xft.security.UserI;

public interface PipelineDetailsService {

	Optional<String> findAllPiperlineDetails(UserI user, String projectId , String pipelineName) throws DataFormatException, InitializationException;
}
