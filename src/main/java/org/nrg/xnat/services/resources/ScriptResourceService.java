package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public interface ScriptResourceService {

	public String getResourceType();

	public String getResourceId();

	public boolean allowPut();

	public boolean allowDelete();

	public Representation represent(Variant variant) throws ResourceException;

	public void handlePut();

	public void handleDelete();
}
