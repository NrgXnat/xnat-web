package org.nrg.xnat.services.resources.util;

import java.io.PrintWriter;
import java.util.Map;

import org.apache.turbine.services.rundata.DefaultTurbineRunData;

import com.google.common.collect.Maps;

public class RestletRunDataUtil extends DefaultTurbineRunData {
Map<String,Object> passedObjects=Maps.newHashMap();
	
	public void passObject(String key, Object o){
		passedObjects.put(key,o);
	}
	
	public Object retrieveObject(String key){
		return passedObjects.remove(key);
	}
	
	public void hijackOutput(PrintWriter os){
		this.setOut(os);
	}
}
