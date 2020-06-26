package org.nrg.xnat.export.resource.selector;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.base.auto.AutoXnatProjectdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.export.manifest.ExportManifest;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohana Ramaratnam
 *
 */

@Slf4j
public class ProjectResourceSelector extends ResourceSelector {

	//This class, given an ExportManifest, returns a list of 
	//selected resources
	
	public ProjectResourceSelector(final ExportManifest exportManifest) {
		super(exportManifest);
	}
	
	public List<XnatAbstractresourceI>  getSelectedResources(XnatProjectdata proj) {
		//Find the project
		//Find the resources which have been requested
		//Return this list
		List<XnatAbstractresourceI> fileteredResources = new ArrayList<XnatAbstractresourceI>();

		try {
			String projectId = _exportManifest.getProjectId();
			final UserI user = _exportManifest.getAuthorizedBy();
			final Hashtable<String,String> projectResourceItemsHash = _exportManifest.getEndpointDefinition().getExportedData().getProjectResources().toHash();
			
			List<XnatAbstractresourceI> projectResources = proj.getResources_resource();
			
			fileteredResources = extractSelectedResources(projectResources, projectResourceItemsHash);
		}catch(NullPointerException e) {
			log.debug("Ecountered " +e.getMessage() + " while getting selected resources for the project");
		}
		
		return fileteredResources;
	}
	
}
