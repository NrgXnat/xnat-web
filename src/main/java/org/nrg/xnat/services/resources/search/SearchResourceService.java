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
public interface SearchResourceService {

	public boolean allowGet();

	public boolean allowPost();

	public boolean handlePost();

	public Representation represent(Variant variant);
}
