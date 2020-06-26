package org.nrg.xnat.export.resource.selector;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.model.XnatSubjectdataI;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.export.manifest.ExportManifest;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohana Ramaratnam
 *
 */
@Slf4j
public class SubjectResourceSelector extends ResourceSelector {

	//This class, given an ExportManifest, returns a list of 
	//selected resources for a subject
	
	public SubjectResourceSelector(final ExportManifest exportManifest) {
		super(exportManifest);
	}
	
	public List<XnatAbstractresourceI>  getSelectedResources(XnatSubjectdataI subject) {
		//Find the resources which have been requested
		//Return this list
		List<XnatAbstractresourceI> fileteredResources = new ArrayList<XnatAbstractresourceI>();

		try {
			final Hashtable<String,String> subjectResourceItemsHash = _exportManifest.getEndpointDefinition().getExportedData().getSubjectResources().toHash();

			List<XnatAbstractresourceI> subjectResources = subject.getResources_resource();
			
			fileteredResources = extractSelectedResources(subjectResources, subjectResourceItemsHash);
		}catch(NullPointerException e) {
			log.debug("Ecountered " +e.getMessage() + " while getting selected resources for the project");
		}
		
		return fileteredResources;
	}
}