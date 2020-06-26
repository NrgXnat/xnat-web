package org.nrg.xnat.export.resource.selector;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.model.XnatExperimentdataI;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.export.manifest.ExportManifest;
import org.nrg.xnat.export.model.endpoint.EndpointIdXsiType;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohana Ramaratnam
 *
 */
@Slf4j
public class SubjectAssessorResourceSelector extends ResourceSelector {

	//This class, given an ExportManifest, returns a list of 
	//selected resources for a subject
	
	public SubjectAssessorResourceSelector(final ExportManifest exportManifest) {
		super(exportManifest);
	}
	
	public List<XnatAbstractresourceI>  getSelectedResources(XnatExperimentdataI subjectAssessor) {
		//Find the resources which have been requested
		//Return this list
		List<XnatAbstractresourceI> fileteredResources = new ArrayList<XnatAbstractresourceI>();

		try {
			final UserI user = _exportManifest.getAuthorizedBy();
			
			
			EndpointIdXsiType subjectAssessorEndpointDefinition = _exportManifest.getEndpointDefinition().getExportedData().getExportedDataForSubjectAssessorByXsiType(subjectAssessor.getXSIType());
			final Hashtable<String,String> subjectAssessorResourceItemsHash = subjectAssessorEndpointDefinition.getResources().toHash();
			List<XnatAbstractresourceI> subjectAssessorResources = subjectAssessor.getResources_resource();
			
			fileteredResources = extractSelectedResources(subjectAssessorResources, subjectAssessorResourceItemsHash);
		}catch(NullPointerException e) {
			log.debug("Ecountered " +e.getMessage() + " while getting selected resources for the project");
		}
		
		return fileteredResources;
	}
}
