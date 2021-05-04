package org.nrg.xnat.services.runner;

import java.util.Optional;

import org.nrg.xapi.exceptions.InitializationException;

public interface RunnerService {

	 Optional<String> getAutomationRunners(String language) throws InitializationException;
}
