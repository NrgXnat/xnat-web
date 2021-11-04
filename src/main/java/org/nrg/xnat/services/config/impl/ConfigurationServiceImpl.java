package org.nrg.xnat.services.config.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.nrg.config.entities.Configuration;
import org.nrg.config.exceptions.ConfigServiceException;
import org.nrg.config.services.ConfigService;
import org.nrg.framework.constants.Scope;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xft.security.UserI;
import org.nrg.xapi.model.config.ConfigModel;
import org.nrg.xnat.helpers.merge.anonymize.DefaultAnonUtils;
import org.nrg.xnat.services.config.ConfigurationService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConfigurationServiceImpl implements ConfigurationService {
	
	private ConfigService configService;
	private static final String TOOL_NAME = "tool";
	
	@Override
	public List<Map<String, String>> findAllConfigs(UserI user) throws NotFoundException {
		configService = XDAT.getConfigService();
		final List<String> tools;
		tools = configService.getTools();
		return getListConfigData(tools);
	}
	
	@Override
	public List<Configuration> findAllByToolName(UserI user, String toolName, String projectId) throws NotFoundException {
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
		 return getListConfigData(tools);
	}

	@Override
	public List<Configuration> findAllByToolNameAndPath(UserI user,  String toolName, String projectId,String path, boolean defaultToSiteWide, String history, String requestVersion) {
		final List<Configuration> configurations = new ArrayList<>();
		Integer version = null;
		if(StringUtils.isNotBlank(requestVersion)){
			version = Integer.parseInt(requestVersion);
		}
		configService = XDAT.getConfigService();
		Configuration configuration = null;
		if (Objects.isNull(projectId)) {
			projectId = "";
		}
		final boolean isSiteWide = StringUtils.isBlank(projectId);
		if (Objects.isNull(version)) {
			if (isSiteWide) {
				configuration = configService.getConfig(toolName, path);
			} else {
				configuration = getProjectConfiguration(configuration, toolName, path, projectId, defaultToSiteWide);
			}
			if (configuration != null) {
				configurations.add(configuration);
			}
		} else {
			 configurations.add(isSiteWide ? configService.getConfigByVersion(toolName, path, version) : configService.getConfigByVersion(toolName, path, version, Scope.Project, projectId));
		}
		return configurations;
	}
	
	@Override
	public void deleteConfig(UserI user, String toolName, String projectId, String path) throws ConfigServiceException, InsufficientPrivilegesException {
		 if (StringUtils.isBlank(projectId)) {
             if (!Roles.isSiteAdmin(user)) {
                 final String message = String.format("User %s is not an administrator and can't disable the configuration setting %s for the tool %s", user.getUsername(), path, toolName);
                 log.info(message);
                 throw new InsufficientPrivilegesException(message);
             }
             configService.disable(user.getLogin(), "Disabling this setting", toolName, path);
         } else {
        	 try {
             if (!(Permissions.canDelete(user, "xnat:subjectData/project", projectId) || Roles.isSiteAdmin(user))) {  //Users should be able to delete project config if have project edit permissions or are site admins. Otherwise they are forbidden.
                 final String message = String.format("User %s can not access project %s to modify configuration setting %s for the tool %s", user.getUsername(), projectId, path, toolName);
                 log.info(message);
                 throw new InsufficientPrivilegesException(message);
				}
			} catch (Exception e) {
			}
			configService.disable(user.getLogin(), "Disabling this setting", toolName, path, Scope.Project, projectId);
		}	
	}
	
	@Override
	public void updateConfig(UserI user, ConfigModel config, String toolName, String projectId, String path, String status, String reason, String unversioned) throws ConfigServiceException, DataFormatException {
		configService = XDAT.getConfigService();
		
		fixAnonPath(toolName, projectId, path);
		
		boolean handledStatus = false;
		
		statusUpdate(user, toolName, projectId, path, status, handledStatus, reason);
		 
		boolean hasBodyContent = (config != null && Objects.nonNull(config.getContents()));

		 final String contents = hasBodyContent ? getBodyContents(config) : "";
         if (contents == null) {
             throw new ConfigServiceException("No contents provided");
         }

         final Configuration prevConfig = StringUtils.isBlank(projectId) ? configService.getConfig(toolName, path) : configService.getConfig(toolName, path, Scope.Project, projectId);
         
         saveAndUpdateConfigration(user, contents,prevConfig,reason,toolName,path,unversioned,projectId);

	}
	
	private void saveAndUpdateConfigration(UserI user, String contents, Configuration prevConfig, String reason, String toolName, String path, String unversioned, String projectId) throws ConfigServiceException {
		 if (prevConfig != null && contents.equals(prevConfig.getContents())) {
        	 return ;
         } else {
             //save/update the configuration
             if (StringUtils.isBlank(unversioned)) {
                 configService.replaceConfig(user.getUsername(), reason, toolName, path, contents, StringUtils.isBlank(projectId) ? Scope.Site : Scope.Project, projectId);
             } else {
                 boolean isUnversioned = Boolean.parseBoolean(unversioned);
                 configService.replaceConfig(user.getUsername(), reason, toolName, path, isUnversioned, contents, StringUtils.isBlank(projectId) ? Scope.Site : Scope.Project, projectId);
             }
             if(projectId==null){
                 DefaultAnonUtils.invalidateSitewideAnonCache();
             }
         }
		
	}

	private void statusUpdate(UserI user, String toolName, String projectId, String path, String status, boolean handledStatus, String reason) throws ConfigServiceException, DataFormatException {
		if (StringUtils.isNotBlank(status)) {
            final Matcher matcher = REGEX_ENABLED_VALUES.matcher(status);
            // Add support for true or false to make compatible with generic controls in settingsManager.js.
            if (!matcher.matches() && !status.equals("true") && !status.equals("false")) {
            	throw new DataFormatException( "Only valid values for the status flag are enabled or true and disabled or false: " + status);
            }
            if ("enabled".equals(status) || "true".equals(status)) {
                if (StringUtils.isBlank(projectId)) {
                    configService.enable(user.getUsername(), reason, toolName, path);
                } else {
                    configService.enable(user.getUsername(), reason, toolName, path, Scope.Project, projectId);
                }
                handledStatus = true;
            } else {
                if (StringUtils.isBlank(projectId)) {
                    configService.disable(user.getUsername(), reason, toolName, path);
                } else {
                    configService.disable(user.getUsername(), reason, toolName, path, Scope.Project, projectId);
                }
                return ;
            }

            if(StringUtils.isBlank(projectId)) {
                DefaultAnonUtils.invalidateSitewideAnonCache();
            }
        }
		
	}

	private String getBodyContents(ConfigModel config) {
        if (config != null) {
            return config.getContents();
        }
		return null; 
    }

	
	 private void fixAnonPath(String toolName, String projectId, String path) {
	        //This is a bit of a hack, but doing the proper fix would introduce risk in the anonymization feature.  Which would be better done in a feature release, then a bug fix release.
	        //The anon feature pre-dated the config service, but was migrated to use the config service for storage of the anonymization script.
	        //However, it *appears* that the 'path' being set when the anonymization file is added (DicomEdit.buildScriptPath) is incorrect.  It is has a / at the beginning of the path, whereas other scripts in the config service don't.
	        //So the ConfigResource correctly creates the path without the / at the beginning, but that fails to match the entry stored in the service by DicomEdit.  DicomEdit should be fixed, but that would introduce a lot of headaches.
	        //So, for now, we'll just hack ConfigResource to support the erroneous path in this one use case.
	        if (toolName != null && StringUtils.equals("anon", toolName) && projectId != null && StringUtils.equals("projects/" + projectId, path)) {
	            path = "/projects/" + projectId;
	        }
	    }
	
	
	private Configuration getProjectConfiguration(Configuration configuration, String toolName, String path, String projectId, boolean defaultToSiteWide) {
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
		return configuration;
	}

	private List<Map<String, String>> getListConfigData(List<String> tools) throws NotFoundException {
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

	 private static final Pattern REGEX_ENABLED_VALUES = Pattern.compile("(en|dis)abled");

}
