package org.nrg.xnat.services.runner;

import org.nrg.xapi.exceptions.InitializationException;

public interface RunnerService {

	public String getAutomationRunners(String language) throws InitializationException;
}
