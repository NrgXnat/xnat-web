/**
 * 
 */
package org.nrg.xnat.services.resources.files;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

/**
 * @author afour
 *
 */
public interface DIRResourceService {

	public final static String[] FILE_HEADERS = { "Name", "DIR", "Size", "URI" };

	public Representation represent(final Variant variant);

}
