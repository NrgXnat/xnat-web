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
public interface SavedSearchResourceService {

	public Representation represent(Variant variant);

	public boolean allowDelete();

	public boolean allowPut();

	public boolean handleDelete();

	public boolean handlePut();
}
