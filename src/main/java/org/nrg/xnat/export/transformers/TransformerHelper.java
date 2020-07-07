package org.nrg.xnat.export.transformers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohana Ramaratnam
 *
 */
public class TransformerHelper implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String  transformedRootPath;
	String projectRootPath;
	String transformerHandler;
	String byteExporterHandler;
	Map<String, Object> transformerSettings = new HashMap<String, Object>();
	
	
	/**
	 * @return the byteExporterHandler
	 */
	public String getByteExporterHandler() {
		return byteExporterHandler;
	}

	/**
	 * @param byteExporterHandler the byteExporterHandler to set
	 */
	public void setByteExporterHandler(String byteExporterHandler) {
		this.byteExporterHandler = byteExporterHandler;
	}
	
	/**
	 * @return the projectRootPath
	 */
	public String getProjectRootPath() {
		return projectRootPath;
	}

	/**
	 * @param projectRootPath the projectRootPath to set
	 */
	public void setProjectRootPath(String projectRootPath) {
		this.projectRootPath = projectRootPath;
	}

	/**
	 * @return the transformedRootPath
	 */
	public String getTransformedRootPath() {
		return transformedRootPath;
	}

	/**
	 * @param transformedRootPath the transformedRootPath to set
	 */
	public void setTransformedRootPath(String transformedRootPath) {
		this.transformedRootPath = transformedRootPath;
	}

	/**
	 * @return the transformerHandler
	 */
	public String getTransformerHandler() {
		return transformerHandler;
	}

	/**
	 * @param transformerHandler the transformerHandler to set
	 */
	public void setTransformerHandler(String transformerHandler) {
		this.transformerHandler = transformerHandler;
	}

	/**
	 * @return the transformerSettings
	 */
	public Map getTransformerSettings() {
		return transformerSettings;
	}

	/**
	 * @param transformerSettings the transformerSettings to set
	 */
	public void setTransformerSettings(Map transformerSettings) {
		this.transformerSettings = transformerSettings;
	}
	
	public void addToTransformerSetting(String key, Object value) {
		transformerSettings.put(key, value);
	}
	
	public Object getTransformerSettingValue(String key) {
		return transformerSettings.get(key);
	}
	
}
