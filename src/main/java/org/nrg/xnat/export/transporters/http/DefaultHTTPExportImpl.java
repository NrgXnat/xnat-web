package org.nrg.xnat.export.transporters.http;

import java.util.List;
import java.util.concurrent.Callable;

import org.nrg.action.ServerException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.om.XnatImageassessordata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectassessordata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.export.event.ExportEvent;
import org.nrg.xnat.export.event.ProjectEvent;
import org.nrg.xnat.export.interfaces.TransformerI;
import org.nrg.xnat.export.manifest.DataDescendantManifest;
import org.nrg.xnat.export.manifest.TransportManifest;
import org.nrg.xnat.export.utils.ExportConstants;
import org.restlet.data.Status;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohana Ramaratnam
 *
 */
@Slf4j
public class DefaultHTTPExportImpl  implements Callable<String>  {
	TransportManifest _transportManifest;
	XnatProjectdata project = null;
	String projectRootPath = null;

	long fileCounter = 0;
	long fileSize = 0;

	String destinationUrl = null;
	String destinationPort = null;
	TransformerI transformer = null;
	

	public DefaultHTTPExportImpl(final TransportManifest transportManifest) {
		_transportManifest = transportManifest;
	}

	
	public DefaultHTTPExportImpl(final TransportManifest transportManifest, final TransformerI transformer) {
		this(transportManifest);
		this.transformer = transformer;
	}



    @Override
    public String call() throws Exception {
		//Go through all the resources and send one file at a time
		DataDescendantManifest dataDesManifest = new DataDescendantManifest();
		final UserI authorizedBy = _transportManifest.getAuthorizedBy();


    	try {
    		
 			if (projectRootPath == null || project == null) {
				project = _transportManifest.getProject();
				projectRootPath = project.getArchiveRootPath();
			}
			dataDesManifest.setProjectId(project.getId());
	   		if (destinationUrl == null) {
				destinationUrl = _transportManifest.getExportManifest().getEndpointDefinition().getExportSettingValueForProp(ExportConstants.URL_PROP_NAME);
			}
			if (destinationPort == null) {
				destinationPort = _transportManifest.getExportManifest().getEndpointDefinition().getExportSettingValueForProp(ExportConstants.URL_PROP_PORT);
			}

			
			XDAT.triggerEvent(ProjectEvent.initial(_transportManifest.getExportEventId(), authorizedBy.getID()));
			//TODO - Credentials
			//ExportCredentialsI credentials = _transportManifest.getExportManifest().getCredentials();
			//Authenticator.setDefault(new BasicAuthenticator(credentials.getUsername(), credentials.getPassword()));

			List<XnatAbstractresourceI> projectResources = _transportManifest.getProject().getResources_resource();

			exportToDestination(projectResources, dataDesManifest);

			List<XnatSubjectdata> subjects = _transportManifest.getSubjects();
			if (subjects == null || subjects.size() > 0) {
				DataDescendantManifest dataDescendantManifest = new DataDescendantManifest();
				dataDescendantManifest.setProjectId(project.getId());
				for (XnatSubjectdata s : subjects) {
					dataDescendantManifest.setSubjectId(s.getId());
					dataDescendantManifest.setSubjectLabel(s.getLabel());
					exportToDestination(s.getResources_resource(),dataDescendantManifest);

					List<XnatSubjectassessordata> experiments = s.getExperiments_experiment();
					if (experiments != null || experiments.size() > 0) {
						for (XnatSubjectassessordata sa : experiments) {
							dataDescendantManifest.setExperimentId(sa.getId());
							dataDescendantManifest.setExperimentLabel(sa.getLabel());
							exportToDestination(sa.getResources_resource(),dataDescendantManifest);

							if (sa instanceof XnatImagesessiondata) {
								XnatImagesessiondata img = (XnatImagesessiondata)sa;
								List<XnatImagescandata> scans = img.getScans_scan();
								if (scans != null && scans.size() > 0) {
									for (XnatImagescandata sc : scans) {
										dataDescendantManifest.setScanId(sc.getId());
										boolean somethingWasExported = exportToDestination(sc.getFile(),dataDescendantManifest);
										if (!somethingWasExported) {
											dataDescendantManifest.setScanId(null);
										}
									}
								}
								List<XnatImageassessordata> assessors = img.getAssessors_assessor();
								if (assessors != null && assessors.size() > 0) {
									for (XnatImageassessordata iAss : assessors) {
										dataDescendantManifest.setAssessorId(iAss.getId());
										dataDescendantManifest.setAssessorLabel(iAss.getLabel());
										boolean somethingWasExported = exportToDestination(iAss.getResources_resource(),dataDescendantManifest);
										if (!somethingWasExported) {
											dataDescendantManifest.setAssessorId(null);
											dataDescendantManifest.setAssessorLabel(null);
										}
									}
								}
							}
						}
					}
				}
			}
			fireProjectExportComplete(dataDesManifest);
			String msg = "Exported project  " + project.getId() + " successfully. Total Files exported " + fileCounter + ". Total bytes exported " + fileSize;
			log.debug(msg);
			return msg;
    	}catch(Exception e) {
			fireExportFailed(dataDesManifest, e.getMessage());
            throw new ServerException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}
    }

	private boolean exportToDestination(List<XnatAbstractresourceI> resources, DataDescendantManifest dataDescendantManifest) throws Exception {
		boolean somethingWasExported = false;
		if (resources != null && resources.size() > 0) {
			somethingWasExported = true;
			HTTPExport httpExport = new HTTPExport(destinationUrl, destinationPort);
			for (XnatAbstractresourceI a: resources) {
				dataDescendantManifest.setResourceLabel(a.getLabel());
				fireStartOfExportEvent(a, dataDescendantManifest);
				HTTPResponseHolder aggregatedResponse = httpExport.send(a, projectRootPath, transformer);
				this.fileCounter += aggregatedResponse.getFilesSentCount();
				this.fileSize += aggregatedResponse.getFilesSentSize();
				fireExportEvent(a, aggregatedResponse, dataDescendantManifest);
			}
		}
		return somethingWasExported;
	}


	private void fireProjectExportComplete(DataDescendantManifest dataDescendantManifest) {
		final String eventTrackingId = _transportManifest.getExportEventId();
		final UserI authorizedBy = _transportManifest.getAuthorizedBy();


		String msg = "Data export complete";

		ExportEvent exportEvent = new ExportEvent(eventTrackingId,authorizedBy.getID(), dataDescendantManifest,"Complete",msg, true);
		exportEvent.setFileSize(fileSize);
		exportEvent.setNumberOfFiles(fileCounter);
		XDAT.triggerEvent(exportEvent);
	}

	private void fireExportFailed(DataDescendantManifest dataDescendantManifest, String msg) {
		fireExportStatus("Failed",dataDescendantManifest,msg);
	}


	private void fireExportStatus(String status,DataDescendantManifest dataDescendantManifest, String msg) {
		final String eventTrackingId = _transportManifest.getExportEventId();
		final UserI authorizedBy = _transportManifest.getAuthorizedBy();

		ExportEvent exportEvent = new ExportEvent(eventTrackingId, authorizedBy.getID(), dataDescendantManifest,status,"Data  export " + status + "(" +   msg + ")", true);
		exportEvent.setFileSize(fileSize);
		exportEvent.setNumberOfFiles(fileCounter);
		XDAT.triggerEvent(exportEvent);
		log.debug(exportEvent.toString());
		System.out.println(exportEvent.toString());
	}


	private void fireStartOfExportEvent(XnatAbstractresourceI a, DataDescendantManifest dataDescendantManifest) {
		final String eventTrackingId = _transportManifest.getExportEventId();
		final UserI authorizedBy = _transportManifest.getAuthorizedBy();

		dataDescendantManifest.setResourceLabel(a.getLabel());

		String status = "In Progress";
		String msg = "";

		ExportEvent exportEvent = new ExportEvent(eventTrackingId,authorizedBy.getID(), dataDescendantManifest,status,msg, false);
		try {
			exportEvent.setFileSize(Long.parseLong(a.getFileSize().toString()));
		}catch(Exception e) {e.printStackTrace();}
		exportEvent.setNumberOfFiles(a.getFileCount());
		XDAT.triggerEvent(exportEvent);
		System.out.println(exportEvent.toString());
	}


	private void fireExportEvent(XnatAbstractresourceI a, HTTPResponseHolder aggregatedResponse, DataDescendantManifest dataDescendantManifest) {
//		final NrgEventService eventService =  XDAT.getContextService().getBean(NrgEventService.class);
		final String eventTrackingId = _transportManifest.getExportEventId();
		final UserI authorizedBy = _transportManifest.getAuthorizedBy();

		dataDescendantManifest.setResourceLabel(a.getLabel());
		int httpStatus = aggregatedResponse.getStatusCode();
		String status = httpStatus == 200 ? "Complete" : "Failed";
		String msg = aggregatedResponse.getStatusMessage();

		ExportEvent exportEvent = new ExportEvent(eventTrackingId,authorizedBy.getID(),dataDescendantManifest,status,msg, false);
		if (httpStatus == 200) {
			exportEvent.setExported(true);
		}
		try {
			exportEvent.setFileSize(Long.parseLong(a.getFileSize().toString()));
		}catch(Exception e) {e.printStackTrace();}
		exportEvent.setNumberOfFiles(a.getFileCount());
		XDAT.triggerEvent(exportEvent);
		System.out.println(exportEvent.toString());
	}








}
