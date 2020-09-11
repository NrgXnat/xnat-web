/**
 * 
 */
package org.nrg.xnat.services.resources;

import java.util.ArrayList;

import org.nrg.action.ClientException;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

/**
 * @author afour
 *
 */
public interface BaseXapiService {

	String getResourceType();

	String getResourceId();

	public boolean allowPut();

	public boolean allowDelete();

	public Representation represent(Variant variant) throws ResourceException;

	public void handlePut();

	public void handleDelete();

	public void handlePost();

	void removeRepresentations();

	void acceptRepresentation(Representation entity);

	public boolean allowGet();

	public ArrayList<String> getDefaultFields(GenericWrapperElement e);

	String getDefaultElementName();

	public void handleParam(final String key, final Object value) throws ClientException;
}
