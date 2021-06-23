package org.nrg.xnat.services.extensions.impl;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.exceptions.NrgServiceRuntimeException;
import org.nrg.framework.services.SerializerService;
import org.nrg.pipeline.PipelineDetailsHelper;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.extensions.PipelineDetailsService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PipelineDetailsServiceImpl implements PipelineDetailsService{
	
	public PipelineDetailsServiceImpl() {
		_serializer = XDAT.getSerializerService();
		if (null == _serializer) {
			throw new NrgServiceRuntimeException("ERROR: Serializer service was not properly initialized.");
		}
	}

	@Override
	public Optional<String> findAllPiperlineDetails(UserI user, String projectId, String pipelineName) throws DataFormatException, InitializationException {
		if (StringUtils.isBlank(projectId)) {
			throw new DataFormatException("No project specified"); 
        }
		if (StringUtils.isBlank(pipelineName)) {
			throw new DataFormatException("No pipelineName specified"); 
        }
		if (log.isDebugEnabled()) {
			log.debug("Returning pipeline details");
		}
		try {
			PipelineDetailsHelper pipelineDetailsHelper = new PipelineDetailsHelper(projectId);
			Map<String, Object> pipelineDetails = pipelineDetailsHelper.getPipelineDetailsMap(pipelineName);
			// Make a json object from the pipelineDetails map
			String result =  getSerializer().toJson(pipelineDetails);
			if(Objects.isNull(result)) {
	    		throw new  NotFoundException("The pipeline details was't found with specified project Id" + projectId + "And with specified Pipeline Name "+ pipelineName) ;
	    	}
			return Optional.of(result);

		} catch (Exception exception) {
			log.error("There was an error rendering the pipeline details", exception);
			throw new InitializationException("There was an error rendering the pipeline details" +  exception);
		}
	}
	
	protected SerializerService getSerializer() {
		return _serializer;
	}

	private final SerializerService _serializer;

}
