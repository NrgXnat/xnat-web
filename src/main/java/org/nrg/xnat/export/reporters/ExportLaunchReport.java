package org.nrg.xnat.export.reporters;

import java.util.List;
import java.util.Map;

/**
 * @author Mohana Ramaratnam
 *
 */
public class ExportLaunchReport {
	int successes, failures;
	
	List<ExportLaunchStatus> experimentLaunchStatus;
	Map<String,String> params;
	/**
	 * @return the successCount
	 */
	public int getSuccesses() {
		return successes;
	}
	/**
	 * @param successCount the successCount to set
	 */
	public void setSuccesses(int successCount) {
		this.successes = successCount;
	}
	/**
	 * @return the failureCount
	 */
	public int getFailures() {
		return failures;
	}
	/**
	 * @param failureCount the failureCount to set
	 */
	public void setFailures(int failureCount) {
		this.failures = failureCount;
	}
	/**
	 * @return the experiments
	 */
	public List<ExportLaunchStatus> getExperimentExportLaunchStatuses() {
		return experimentLaunchStatus;
	}
	/**
	 * @param experiments the experiments to set
	 */
	public void setExperimentExportLaunchStatuses(List<ExportLaunchStatus> experiments) {
		experimentLaunchStatus = experiments;
	}
	/**
	 * @return the launchParameters
	 */
	public Map<String, String> getParams() {
		return params;
	}
	/**
	 * @param launchParameters the launchParameters to set
	 */
	public void setParams(Map<String, String> launchParameters) {
		this.params = launchParameters;
	}

	
}
