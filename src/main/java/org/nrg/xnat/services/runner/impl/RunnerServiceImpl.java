package org.nrg.xnat.services.runner.impl;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.nrg.automation.services.ScriptRunnerService;
import org.nrg.framework.services.SerializerService;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xnat.services.runner.RunnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RunnerServiceImpl implements RunnerService{

	@Autowired
	public RunnerServiceImpl() {
		_runnerService = XDAT.getContextService().getBean(ScriptRunnerService.class);
		_serializer = XDAT.getSerializerService();
	}
	
	@Override
	public Optional<String> getAutomationRunners(String language) throws InitializationException {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			if (StringUtils.isNotBlank(language)) {
				if (!_runnerService.hasRunner(language)) {
					throw new NotFoundException(String.format("No script runner found for %s", language));
				}
				 String json = toJson(_runnerService.getRunner(language));
				 if(StringUtils.isBlank(json)) {
					 throw new  NotFoundException("language json wasn't founds");
				 }
				return Optional.of(objectMapper.writeValueAsString(json));
			} else {
				final List<String> runners = _runnerService.getRunners();
				return Optional.of( objectMapper.writeValueAsString(runners));
			}
		} catch (java.io.IOException | NotFoundException e) {
				throw new InitializationException("There was an error processing the script runners to JSON");
		}
	}

	protected <T> String toJson(final T instance) throws IOException {
		return getSerializer().toJson(instance);
	}

	protected SerializerService getSerializer() {
		return _serializer;
	}

	private final ScriptRunnerService _runnerService;
	private final SerializerService _serializer;
}
