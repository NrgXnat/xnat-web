package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface ScanResourceService {

	public boolean allowPut();

	public void handlePut();

	public boolean allowDelete();

	public void handleDelete();
	
	public Representation represent(Variant variant);
}
