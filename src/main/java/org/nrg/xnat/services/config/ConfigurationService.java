package org.nrg.xnat.services.config;

import java.util.List;
import java.util.Map;

import org.nrg.config.entities.Configuration;
import org.nrg.config.exceptions.ConfigServiceException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xapi.model.config.ConfigModel;

public interface ConfigurationService {

	List<Map<String, String>> findAllConfigs(UserI user) throws NotFoundException;

	List<Configuration> findAllByToolName(UserI user, String toolName, String projectId) throws NotFoundException;

	List<Map<String, String>> findAllProjectConfigs(UserI user, String projectId) throws NotFoundException;

	List<Configuration> findAllByToolNameAndPath(UserI user,String toolName, String projectId,  String path, boolean defaultToSiteWide,  String history, String requestVersion);
	
	 void updateConfig(UserI user, ConfigModel config, String toolName, String projectId, String path, String status, String reason, String unversioned) throws ConfigServiceException, DataFormatException;
	 
	 void deleteConfig(UserI user,String toolName, String projectId,  String path) throws ConfigServiceException, InsufficientPrivilegesException;
}
