package org.nrg.xnat.services.config;

import java.util.List;
import java.util.Map;

import org.nrg.config.entities.Configuration;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;

public interface ConfigurationService {

	List<Map<String, String>> findAllConfigs(UserI user) throws NotFoundException;

	List<Configuration> findByToolName(UserI user, String toolName, String projectId) throws NotFoundException;
}
