package org.nrg.xnat.export.resource.selector;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xnat.export.manifest.ExportManifest;

/**
 * @author Mohana Ramaratnam
 *
 */
public class ResourceSelector {

	protected ResourceSelector(final ExportManifest exportManifest) {
		_exportManifest = exportManifest;
	}
	
	protected List<XnatAbstractresourceI> extractSelectedResources(List<XnatAbstractresourceI> resources, Hashtable<String, String> itemsAsHash) {
		List<XnatAbstractresourceI> fileteredResources = new ArrayList<XnatAbstractresourceI>();
		for (XnatAbstractresourceI absRes : resources ) {
			if(itemsAsHash.containsKey(absRes.getLabel())) {
				fileteredResources.add(absRes);
			}
		}
		return fileteredResources;
	}
	
	protected ExportManifest _exportManifest;

}
