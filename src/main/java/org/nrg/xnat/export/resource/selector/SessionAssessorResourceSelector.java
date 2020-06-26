package org.nrg.xnat.export.resource.selector;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.model.XnatImageassessordataI;
import org.nrg.xdat.model.XnatImagesessiondataI;
import org.nrg.xnat.export.manifest.ExportManifest;
import org.nrg.xnat.export.model.endpoint.EndpointImagingSessionAssessorXsiType;
import org.nrg.xnat.export.model.endpoint.EndpointImagingSessionXsiType;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohana Ramaratnam
 *
 */
@Slf4j
public class SessionAssessorResourceSelector extends ResourceSelector {

	public SessionAssessorResourceSelector(final ExportManifest exportManifest) {
		super(exportManifest);
	}
	
	public List<XnatAbstractresourceI>  getSelectedResources(XnatImagesessiondataI imageSession, XnatImageassessordataI imageAssessor) {
		//Find the resources which have been requested
		//Return this list
		List<XnatAbstractresourceI> fileteredResources = new ArrayList<XnatAbstractresourceI>();

		try {
			EndpointImagingSessionXsiType imagingSessionEndpointDefinition = _exportManifest.getEndpointDefinition().getExportedData().getExportedDataForImagingSessionByXsiType(imageSession.getXSIType());
			if (null == imagingSessionEndpointDefinition) {
				return fileteredResources;
			}
			
			EndpointImagingSessionAssessorXsiType imagingSessionAssessor = imagingSessionEndpointDefinition.getSessionAssessors().getExportedDataForImagingSessionAssessorByXsiType(imageAssessor.getXSIType());
			if (null == imagingSessionAssessor) {
				return fileteredResources;
			}
			
			final Hashtable<String,String> imageAssessorResourceItemsHash = imagingSessionAssessor.getResources().toHash();
			List<XnatAbstractresourceI> imageAssessorResources = imageAssessor.getResources_resource();
			
			fileteredResources = extractSelectedResources(imageAssessorResources, imageAssessorResourceItemsHash);
		}catch(NullPointerException e) {
			log.debug("Ecountered " +e.getMessage() + " while getting selected resources for the project");
		}
		
		return fileteredResources;
	}



}
