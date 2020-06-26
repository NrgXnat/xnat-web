package org.nrg.xnat.export.interfaces;

import org.nrg.xnat.export.exception.ExporterNotFoundException;

/**
 * @author Mohana Ramaratnam
 *
 */
public interface  ExportManagerI {

	ExporterI getExporterByExportHandlerAnnotation(String exporthandler) throws ExporterNotFoundException;
}
