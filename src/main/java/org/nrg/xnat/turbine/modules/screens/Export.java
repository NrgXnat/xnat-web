/*
 * web: org.nrg.xnat.turbine.modules.screens.UploadOptions
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.turbine.modules.screens;

import java.util.ArrayList;
import java.util.List;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.nrg.config.entities.Configuration;
import org.nrg.config.services.ConfigService;
import org.nrg.framework.constants.Scope;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.turbine.modules.screens.SecureReport;
import org.nrg.xnat.export.model.endpoint.EndpointDefinition;
import org.nrg.xnat.export.utils.ExportConstants;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("unused")
public class Export  extends SecureReport {
	
	public void finalProcessing(RunData data, Context context) {
		ConfigService configService = XDAT.getConfigService();
		List<EndpointDefinition> projEndpointDefinition = new ArrayList<EndpointDefinition>();
		try {
		List<Configuration> projectConfigs = configService.getConfigsByTool(ExportConstants.TOOL_ID, Scope.Project, (String)om.getProperty("ID"));
			for (Configuration c:projectConfigs) {
				String jsonStr = c.getContents();
					ObjectMapper objectMapper = new ObjectMapper();	
				 	EndpointDefinition endPointDefinition = objectMapper.readValue(jsonStr, EndpointDefinition.class);  
				 	projEndpointDefinition.add(endPointDefinition);
			}
		}catch(Exception e) {
			logger.error("Looks like configuration content is not set properly on " + ExportConstants.TOOL_ID);
		}
		context.put("projEndpointDefinition", projEndpointDefinition);
	}
}
