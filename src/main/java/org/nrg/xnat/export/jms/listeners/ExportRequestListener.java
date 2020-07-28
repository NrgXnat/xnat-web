package org.nrg.xnat.export.jms.listeners;

import java.util.ArrayList;
import java.util.List;

import org.nrg.xdat.XDAT;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.export.interfaces.ExportManagerI;
import org.nrg.xnat.export.interfaces.ExporterI;
import org.nrg.xnat.export.jms.requests.ExportRequest;
import org.nrg.xnat.export.manifest.ExportManifest;
import org.nrg.xnat.export.notifications.NotifyProjectExportListeners;
import org.nrg.xnat.export.utils.ExportConstants;
import org.nrg.xnat.helpers.resource.direct.DirectProjResourceImpl;
import org.nrg.xnat.helpers.uri.UriParserUtils;
import org.nrg.xnat.services.archive.CatalogService;
import org.nrg.xnat.utils.WorkflowUtils;
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
            log.trace("Triggering export for {} to {}", exportManifest.getProjectId(), exportManifest.getEndpointDefinition().getLabel());
		} else {
            log.debug("Triggering export for {} to {}", exportManifest.getProjectId(), exportManifest.getEndpointDefinition().getLabel());
        }

		UserI user = exportManifest.getAuthorizedBy();
		String exportHandler = exportManifest.getEndpointDefinition().getExportHandler();
		String projectId = exportManifest.getProjectId();
		XnatProjectdata project = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
		List<String> emails = new ArrayList<String>();
		String notification = exportManifest.getEndpointDefinition().getNotificationEmails();
		if (notification != null) {
			String[] emailStrArr = notification.split(",");
			for (String e:emailStrArr) {
				emails.add(e);
			}
		}

		try {
			createExportLogResource(project,user);
			ExporterI exporter = _exportManager.getExporterByExportHandlerAnnotation(exportHandler);
			exporter.export(exportManifest, user);
		}catch(Exception e) {
			log.error(e.getMessage());
			try {
				new NotifyProjectExportListeners(project, "email/Export_Failure.vm", user, null, "export.lst", emails, "failure").send();
			} catch (Exception e1) {
				log.error(e1.getMessage());
			}
		}
	}
    
	
	private void createExportLogResource(XnatProjectdata project, UserI user) throws Exception {
		List<XnatAbstractresourceI> aResources =  project.getResources_resource();
		boolean exists = false;
		for (XnatAbstractresourceI a:aResources) {
			if (a instanceof XnatResourcecatalog) {
				if (((XnatResourcecatalog)a).getLabel().equals(ExportConstants.EXPORT_LOGS)) {
					exists = true; 
					break;
				}
			}
		}
		if (!exists) {
			XnatResourcecatalog catRes = new XnatResourcecatalog();
			catRes.setLabel(ExportConstants.EXPORT_LOGS);
            PersistentWorkflowI wrk = PersistentWorkflowUtils.getWorkflowByEventId(user, null);
			insertCatalogWrap(project, catRes, wrk, user);
		}
	}
	
	  private void insertCatalogWrap(XnatProjectdata project, XnatResourcecatalog catResource, PersistentWorkflowI wrk, UserI user) throws Exception {
	        final boolean isNew;
	        final Integer wrkId;
	        if (wrk == null) {
	            isNew = true;
	            wrk = PersistentWorkflowUtils.buildOpenWorkflow(user, project.getItem(), newEventInstance(EventUtils.CATEGORY.DATA,  EventUtils.CREATE_RESOURCE));
	            if (wrk == null) {
	                throw new Exception("Unable to build open workflow for inserting catalog " + catResource.getUri());
	            }

	            wrk.setStatus(PersistentWorkflowUtils.IN_PROGRESS);
	            PersistentWorkflowUtils.save(wrk, wrk.buildEvent());
	            wrkId=wrk.getWorkflowId();
	        } else {
	            wrkId=null;
	            isNew = false;
	        }

	        final CatalogService _catalogService = XDAT.getContextService().getBean(CatalogService.class);

	        _catalogService.insertResourceCatalog(user, UriParserUtils.getArchiveUri(project), catResource, wrkId);

	        if (isNew) {
	            WorkflowUtils.complete(wrk, wrk.buildEvent());
	        }
	    }
	  
	    private EventDetails newEventInstance(EventUtils.CATEGORY cat, String action) {
	        return EventUtils.newEventInstance(cat, EventUtils.TYPE.PROCESS,  action, null, null);
	    }

	

    private ExportManagerI _exportManager;
}
