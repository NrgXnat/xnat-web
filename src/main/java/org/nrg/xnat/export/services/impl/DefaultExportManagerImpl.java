package org.nrg.xnat.export.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nrg.xdat.XDAT;
import org.nrg.xnat.export.annotation.ByteExportHandler;
import org.nrg.xnat.export.annotation.ExportHandler;
import org.nrg.xnat.export.annotation.TransformerHandler;
import org.nrg.xnat.export.exception.ByteExporterNotFoundException;
import org.nrg.xnat.export.exception.ExporterNotFoundException;
import org.nrg.xnat.export.exception.TransformerNotFoundException;
import org.nrg.xnat.export.interfaces.ByteExporterI;
import org.nrg.xnat.export.interfaces.ExportManagerI;
import org.nrg.xnat.export.interfaces.ExporterI;
import org.nrg.xnat.export.interfaces.TransformerI;
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
	
	public TransformerI getTransformerByTransportHandlerAnnotation(String exporthandler) throws TransformerNotFoundException{
		List<TransformerI> transformers = null;
		TransformerI handlerExporter = null;
	    try {
	        Map<String, TransformerI> serviceMap =  XDAT.getContextService().getBeansOfType(TransformerI.class);
	        if (serviceMap != null) {
	        	transformers = new ArrayList<>(serviceMap.values());
	        }
	    } catch (Exception e) {
	        log.error("Unable to retrieve injected Exporter beans", e);
	        throw new TransformerNotFoundException("Could not retrieve transformer of class " + TransformerI.class.getName());
	    }

	    if (transformers == null || transformers.isEmpty()) {
	        log.trace("No Export beans");
	        throw new TransformerNotFoundException("No transformer beans ");
	    }

	    for (TransformerI e : transformers) {
	        final TransformerHandler annotation = e.getClass().getAnnotation(TransformerHandler.class);
	        if (annotation != null) {
	        	if (exporthandler.equals(annotation.handler())) {
	        		handlerExporter = e;
	        		break;
	        	}
	        }

	    }
	    return handlerExporter;
	}
	
	public ByteExporterI getByteExporterByAnnotation(String byteExporthandler) throws ByteExporterNotFoundException{
		List<ByteExporterI> byteExporters = null;
		ByteExporterI handlerExporter = null;
	    try {
	        Map<String, ByteExporterI> serviceMap =  XDAT.getContextService().getBeansOfType(ByteExporterI.class);
	        if (serviceMap != null) {
	        	byteExporters = new ArrayList<>(serviceMap.values());
	        }
	    } catch (Exception e) {
	        log.error("Unable to retrieve injected ByteExporter beans", e);
	        throw new ByteExporterNotFoundException("Could not retrieve byte exporter of class " + ByteExporterI.class.getName());
	    }

	    if (byteExporters == null || byteExporters.isEmpty()) {
	        log.trace("No Export beans");
	        throw new ByteExporterNotFoundException("No byte exporter beans ");
	    }

	    for (ByteExporterI e : byteExporters) {
	        final ByteExportHandler annotation = e.getClass().getAnnotation(ByteExportHandler.class);
	        if (annotation != null) {
	        	if (byteExporthandler.equals(annotation.handler())) {
	        		handlerExporter = e;
	        		break;
	        	}
	        }

	    }
	    return handlerExporter;
	}
	
}
