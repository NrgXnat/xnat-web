package org.nrg.xnat.export.resource.selector;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.model.XnatImageassessordataI;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.model.XnatImagesessiondataI;
import org.nrg.xnat.export.manifest.ExportManifest;
import org.nrg.xnat.export.model.endpoint.EndpointExportedData;
import org.nrg.xnat.export.model.endpoint.EndpointImagingSessionAssessorXsiType;
import org.nrg.xnat.export.model.endpoint.EndpointImagingSessionScanType;
import org.nrg.xnat.export.model.endpoint.EndpointImagingSessionXsiType;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohana Ramaratnam
 *
 */
@Slf4j
public class ImagingSessionResourceSelector extends ResourceSelector {

	public ImagingSessionResourceSelector(final ExportManifest exportManifest) {
		super(exportManifest);
	}
	
	public List<XnatAbstractresourceI>  getSelectedResources(XnatImagesessiondataI imageSession) {
		//Find the resources which have been requested
		//Return this list
		List<XnatAbstractresourceI> fileteredResources = new ArrayList<XnatAbstractresourceI>();

		try {
			EndpointImagingSessionXsiType imagingSessionEndpointDefinition = _exportManifest.getEndpointDefinition().getExportedData().getExportedDataForImagingSessionByXsiType(imageSession.getXSIType());
			final Hashtable<String,String> imageSessionResourceItemsHash = imagingSessionEndpointDefinition.getResources().toHash();
			List<XnatAbstractresourceI> imageSessionResources = imageSession.getResources_resource();
			
			fileteredResources = extractSelectedResources(imageSessionResources, imageSessionResourceItemsHash);
		}catch(NullPointerException e) {
			log.debug("Ecountered " +e.getMessage() + " while getting selected resources for the project");
		}
		
		return fileteredResources;
	}

	public List<String>  getSelectedScanTypes(XnatImagesessiondataI imageSession) throws NullPointerException{
		List<String> fileteredResources = new ArrayList<String>();
		EndpointImagingSessionXsiType imagingSessionEndpointDefinition = _exportManifest.getEndpointDefinition().getExportedData().getExportedDataForImagingSessionByXsiType(imageSession.getXSIType());
		EndpointImagingSessionScanType scanTypeDef = imagingSessionEndpointDefinition.getScanTypes();
		fileteredResources =  scanTypeDef.getItems();
		
		return fileteredResources;
	}
	
	public List<String>  getSelectedScanIds(XnatImagesessiondataI imageSession) throws NullPointerException{
		List<String> fileteredResources = new ArrayList<String>();
		EndpointImagingSessionXsiType imagingSessionEndpointDefinition = _exportManifest.getEndpointDefinition().getExportedData().getExportedDataForImagingSessionByXsiType(imageSession.getXSIType());
		EndpointImagingSessionScanType scanTypeDef = imagingSessionEndpointDefinition.getScanTypes();
		fileteredResources =  scanTypeDef.getIds();
		
		return fileteredResources;
	}

	public boolean  include(XnatImagesessiondataI imageSession) throws NullPointerException {
		boolean includeSession = false;
		EndpointImagingSessionXsiType imagingSessionEndpointDefinition = _exportManifest.getEndpointDefinition().getExportedData().getExportedDataForImagingSessionByXsiType(imageSession.getXSIType());
		List<String> ids  = imagingSessionEndpointDefinition.getIds();
		if (null != ids && ids.size() > 0) {
			for (String i: ids) {
				if (imageSession.getId().equals(i)) {
					includeSession = true;
					break;
				}
			}
		}
		return includeSession;
	}


	
	public boolean  isIncludedByIdAndType(XnatImagesessiondataI imageSession, XnatImagescandataI imageScan) throws NullPointerException{
		boolean includeScan = false;
		EndpointExportedData exportData = _exportManifest.getEndpointDefinition().getExportedData();
		if (null != exportData) {
			EndpointImagingSessionXsiType imagingSessionEndpointDefinition =	exportData.getExportedDataForImagingSessionByXsiType(imageSession.getXSIType());
			if (null != imagingSessionEndpointDefinition) {
				EndpointImagingSessionScanType scanType = imagingSessionEndpointDefinition.getScanTypes();
				if (null != scanType) {
					List<String> ids  = scanType.getIds();
					if (null != ids && ids.size() > 0) {
						for (String i: ids) {
							if (imageScan.getId().equals(i)) {
								includeScan = true;
								break;
							}
						}
					}else {
						//Is the scan of the exported type?
						List<String> scanTypes = imagingSessionEndpointDefinition.getScanTypes().getItems();
						if (scanTypes != null && scanTypes.size() > 0) {
							for (String s: scanTypes) {
								if (imageScan.getType().equals(s)) {
									includeScan = true;
									break;
								}
							}
						}
					}
				}
			}
		}
		return includeScan;
	}

	
	public List<XnatAbstractresourceI>  getSelectedScanResources(XnatImagesessiondataI imageSession, XnatImagescandataI imageScan) throws NullPointerException {
		//Find the resources which have been requested
		//Return this list
		List<XnatAbstractresourceI> fileteredResources = new ArrayList<XnatAbstractresourceI>();

		EndpointImagingSessionXsiType imagingSessionEndpointDefinition = _exportManifest.getEndpointDefinition().getExportedData().getExportedDataForImagingSessionByXsiType(imageSession.getXSIType());
		final Hashtable<String,String> imageSessionScanResourceItemsHash = imagingSessionEndpointDefinition.getScanResources().toHash();
		
		List<XnatAbstractresourceI> imageScanResources = imageScan.getFile();
		
		fileteredResources = extractSelectedResources(imageScanResources, imageSessionScanResourceItemsHash);
		
		return fileteredResources;
	}

	
	public List<XnatAbstractresourceI>  getSelectedAssessorResources(XnatImagesessiondataI imageSession, XnatImageassessordataI imageAssessor) {
		//Find the resources which have been requested
		//Return this list
		List<XnatAbstractresourceI> fileteredResources = new ArrayList<XnatAbstractresourceI>();

		try {
			EndpointImagingSessionXsiType imagingSessionEndpointDefinition = _exportManifest.getEndpointDefinition().getExportedData().getExportedDataForImagingSessionByXsiType(imageSession.getXSIType());
			EndpointImagingSessionAssessorXsiType imagingSessionAssessorEndpointDefinition  = imagingSessionEndpointDefinition.getSessionAssessors().getExportedDataForImagingSessionAssessorByXsiType(imageAssessor.getXSIType());
			final Hashtable<String,String> imageSessionAssessorResourceItemsHash = imagingSessionAssessorEndpointDefinition.getResources().toHash();
			
			List<XnatAbstractresourceI> imageSessionAssessorResources = imageAssessor.getResources_resource();
			
			fileteredResources = extractSelectedResources(imageSessionAssessorResources, imageSessionAssessorResourceItemsHash);
		}catch(NullPointerException e) {
			log.debug("Ecountered " +e.getMessage() + " while getting selected resources for the project");
		}
		
		return fileteredResources;
	}
	
	public boolean hasImageAssessorExportDefinition(XnatImagesessiondataI imageSession, XnatImageassessordataI imageAssessor) {
		boolean hasDefinition = false;
		EndpointExportedData exportedData = _exportManifest.getEndpointDefinition().getExportedData();
		if (exportedData != null) {
			EndpointImagingSessionXsiType imagingSessionEndpointDefinition = exportedData.getExportedDataForImagingSessionByXsiType(imageSession.getXSIType());
		}else {
			//everything needs to be sent.
			hasDefinition = true;
		}
		return hasDefinition;
	}
	
	public boolean isScanTypeIncluded(XnatImagesessiondataI imageSession) {
		boolean hasDefinition = false;
		EndpointExportedData exportedData = _exportManifest.getEndpointDefinition().getExportedData();
		if (exportedData != null) {
			EndpointImagingSessionXsiType imagingSessionEndpointDefinition = exportedData.getExportedDataForImagingSessionByXsiType(imageSession.getXSIType());
			if (imagingSessionEndpointDefinition.getScanTypes() != null) {
				hasDefinition = true;
			}
		}
		return hasDefinition;
	}
	

	public boolean  isIncludedById(XnatImagesessiondataI imageSession, XnatImageassessordataI imageAssessor) {
		boolean includeAssessor = false;
		try {
			EndpointImagingSessionXsiType imagingSessionEndpointDefinition = _exportManifest.getEndpointDefinition().getExportedData().getExportedDataForImagingSessionByXsiType(imageSession.getXSIType());
			if (null != imagingSessionEndpointDefinition) {
				EndpointImagingSessionAssessorXsiType imagingSessionAssessorEndpointDefinition  = imagingSessionEndpointDefinition.getSessionAssessors().getExportedDataForImagingSessionAssessorByXsiType(imageAssessor.getXSIType());
				if (null != imagingSessionAssessorEndpointDefinition) {
					List<String> ids = imagingSessionAssessorEndpointDefinition.getIds();
					if (null != ids && ids.size() > 0) {
						for (String i: ids) {
							if (imageAssessor.getId().equals(i) || imageAssessor.getLabel().equals(i)) {
								includeAssessor = true;
								break;
							}
						}
					}
				}
			}
		}catch(NullPointerException e) {
			log.debug("Ecountered " +e.getMessage() + " while getting selected resources for the project");
		}
		
		return includeAssessor;
	}

}
