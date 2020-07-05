package org.nrg.xnat.export.jms.listeners;

import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.export.interfaces.ExportManagerI;
import org.nrg.xnat.export.interfaces.ExporterI;
import org.nrg.xnat.export.jms.requests.ExportRequest;
import org.nrg.xnat.export.manifest.ExportManifest;
import org.nrg.xnat.export.notifications.NotifyProjectExportListeners;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohana Ramaratnam
 *
 */
@Slf4j
@Component

public class ExportRequestListener {

    @Autowired
    public ExportRequestListener(final ExportManagerI exportManager) {
        this._exportManager = exportManager;
    }

	@JmsListener(containerFactory = "exportQueueListenerFactory", destination = "exportRequest")
	public void onRequest(ExportRequest exportManifestRequest) {
		ExportManifest exportManifest = exportManifestRequest.get_exportManifest();
		if (log.isTraceEnabled()) {
            log.trace("Triggering export for {} to {}", exportManifest.getProjectId(), exportManifest.getEndpointDefinition().getExportHandler());
		} else {
            log.debug("Triggering export for {} to {}", exportManifest.getProjectId(), exportManifest.getEndpointDefinition().getExportHandler());
        }

		UserI user = exportManifest.getAuthorizedBy();
		String exportHandler = exportManifest.getEndpointDefinition().getExportHandler();
		String projectId = exportManifest.getProjectId();
		XnatProjectdata project = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
		try {
			ExporterI exporter = _exportManager.getExporterByExportHandlerAnnotation(exportHandler);
			exporter.export(exportManifest, user);
		}catch(Exception e) {
			log.error(e.getMessage());
			try {
				new NotifyProjectExportListeners(project, "export_failure.vm", user, null, "export.lst", null, "failure").send();
			} catch (Exception e1) {
				log.error(e1.getMessage());
			}
		}
	}
    

    private ExportManagerI _exportManager;
}
