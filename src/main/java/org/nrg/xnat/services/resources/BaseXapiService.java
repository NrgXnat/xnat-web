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

	public String getResourceType();

	public String getResourceId();

	public boolean allowGet();
	
	public boolean allowPost();
	
	public boolean allowPut();

	public boolean allowDelete();
	
	public void handlePost();

	public void handlePut();

	public void handleDelete();
	
	public void removeRepresentations();
	
	public String getDefaultElementName();
	
	public void acceptRepresentation(Representation entity);

	public Representation represent(Variant variant) throws ResourceException;
	
	public ArrayList<String> getDefaultFields(GenericWrapperElement e);

	public void handleParam(final String key, final Object value) throws ClientException;
}
