package org.nrg.xnat.services.config.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.nrg.config.entities.Configuration;
import org.nrg.config.services.ConfigService;
import org.nrg.framework.constants.Scope;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.config.ConfigurationService;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {
	
	private ConfigService configService;
	private static final String TOOL_NAME = "tool";
	
	@Override
	public List<Map<String, String>> findAllConfigs(UserI user) throws NotFoundException {
		configService = XDAT.getConfigService();
		final List<String> tools;
		tools = configService.getTools();
		return getListConfigDate(tools);
	}
	
	@Override
	public List<Configuration> findByToolName(UserI user, String toolName, String projectId) throws NotFoundException {
		 final List<Configuration> configurations = new ArrayList<>();
		configService = XDAT.getConfigService();
		 final List<Configuration> l = StringUtils.isBlank(projectId)
                 ? configService.getConfigsByTool(toolName)
                 : configService.getConfigsByTool(toolName, Scope.Project, projectId);
         if (l != null) {
             configurations.addAll(l);  //addAll is not null safe.
         }else {
        	 throw new NotFoundException("configurations list wassn't found");
         }
		 return configurations;
	}

	@Override
	public List<Map<String, String>> findAllProjectConfigs(UserI user, String projectId) throws NotFoundException {
	      final List<String> tools;
	      configService = XDAT.getConfigService();
		 tools = configService.getTools(Scope.Project, projectId);
		 return getListConfigDate(tools);
	}

	@Override
	public List<Configuration> findByToolNameAndPath(UserI user,  String toolName, String projectId,String path, boolean defaultToSiteWide) {
		final List<Configuration> configurations = new ArrayList<>();
		configService = XDAT.getConfigService();
		 Configuration configuration = null;
		 if(Objects.isNull(projectId))
			 projectId = "";
		 
		  final boolean isSiteWide = StringUtils.isBlank(projectId);
         if (isSiteWide) {
             configuration = configService.getConfig(toolName, path);
         } else {
             try {
                 configuration = configService.getConfig(toolName, path, Scope.Project, projectId);
                 if (configuration == null && defaultToSiteWide) {
                     //if project specific config is missing, allow fail over to site wide config
                     configuration = configService.getConfig(toolName, path);
                 }
             } catch (Exception e) {
                 // assume project config is missing
                 if (defaultToSiteWide) {
                	//if project specific config is missing, allow fail over to site wide configService.getConfig(toolName, path))
                     configuration = configService.getConfig(toolName, path);
                 }
             }
         }
         if (configuration != null) {
             configurations.add(configuration);
         }
		return configurations;
	}
	
	private List<Map<String, String>> getListConfigDate(List<String> tools) throws NotFoundException {
		  final List<Map<String, String>> list = new ArrayList<>();
		 if (tools != null) {
       	 tools.forEach(tool->{
       		 Map<String, String>map = new HashMap<>();
       		 map.put(TOOL_NAME, tool);
       		 list.add(map);
       	 });
        }else {
       	 throw new NotFoundException("config tool list wasn't found");
        }
		return list;
	}

}
