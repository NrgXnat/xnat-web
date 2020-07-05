package org.nrg.xnat.export.interfaces;

import org.nrg.xnat.export.exception.ByteExporterNotFoundException;
import org.nrg.xnat.export.exception.ExporterNotFoundException;
import org.nrg.xnat.export.exception.TransformerNotFoundException;

/**
 * @author Mohana Ramaratnam
 *
 */
public interface  ExportManagerI {

	ExporterI getExporterByExportHandlerAnnotation(String exporthandler) throws ExporterNotFoundException;
	TransformerI getTransformerByTransportHandlerAnnotation(String transporthandler) throws TransformerNotFoundException;
	ByteExporterI getByteExporterByAnnotation(String byteExporterHandler) throws ByteExporterNotFoundException;

}
