/**
 * 
 */
package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

/**
 * @author afour
 *
 */
public interface ProjectUserListResourceService {

	public Representation represent(Variant variant);
	
	public boolean isWhitelisted();
}
