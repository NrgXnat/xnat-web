/**
 * 
 */
package org.nrg.xnat.services.resources.search;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

/**
 * @author afour
 *
 */
public interface CachedSearchResourceService {

	public Representation getRepresentation(Variant variant);
}
