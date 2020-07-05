package org.nrg.xnat.export.utils;

import org.nrg.framework.status.StatusMessage;

/**
 * @author Mohana Ramaratnam
 *
 */
public class ExportConstants {

    public static final String EXPORT_HANDLER_ATTR = "export-handler";
    public static final String EXPORT_ALL_XSITYPE = "ALL";
    public static final String TOOL_ID = "export-service";
    public static final String URL_PROP_NAME = "url";
    public static final String URL_PROP_PORT = "port";
    public static final String TRANSFORMER_HANDLER = "transformer-handler";
    
    
    public enum ExportType {
    	Project,Project_Resource,Subject,Subject_Resource,Experiment, Experiment_Resource, Scan_Resource;
    	
    	public String type() {
	            switch (this) {
	                case Project:
	                	return "Project";
	            	case Project_Resource:
	                    return "Project_Resource";
	                case Subject:
	                    return "Subject";
	                case Subject_Resource:
	                    return "Subject_Resource";
	                case Experiment:
	                    return "Experiment";
	                case Experiment_Resource:
	                    return "Experiment_Resource";
	                case Scan_Resource:
	                    return "Scan_Resource";
	                default:
	                    return "XNAT_ENTITY";
	            }
	        }
    }

}
