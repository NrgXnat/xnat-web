package org.nrg.xnat.export.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nrg.xdat.XDAT;
import org.nrg.xnat.export.annotation.ExportHandler;
import org.nrg.xnat.export.exception.ExporterNotFoundException;
import org.nrg.xnat.export.interfaces.ExportManagerI;
import org.nrg.xnat.export.interfaces.ExporterI;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohana Ramaratnam
 *
 */
@Slf4j
@Component
public class DefaultExportManagerImpl implements ExportManagerI {
	
	public ExporterI getExporterByExportHandlerAnnotation(String exporthandler) throws ExporterNotFoundException{
		List<ExporterI> exporters = null;
		ExporterI handlerExporter = null;
	    try {
	        Map<String, ExporterI> serviceMap =  XDAT.getContextService().getBeansOfType(ExporterI.class);
	        if (serviceMap != null) {
	            exporters = new ArrayList<>(serviceMap.values());
	        }
	    } catch (Exception e) {
	        log.error("Unable to retrieve injected Exporter beans", e);
	        throw new ExporterNotFoundException("Could not retrieve exporter of class " + ExporterI.class.getName(),new IllegalArgumentException());
	    }

	    if (exporters == null || exporters.isEmpty()) {
	        log.trace("No Export beans");
	        throw new ExporterNotFoundException("No export beans ",new IllegalArgumentException());
	    }

	    for (ExporterI e : exporters) {
	        final ExportHandler annotation = e.getClass().getAnnotation(ExportHandler.class);
	        if (annotation != null) {
	        	if (exporthandler.equals(annotation.handler())) {
	        		handlerExporter = e;
	        		break;
	        	}
	        }

	    }
	    return handlerExporter;
	}
}
