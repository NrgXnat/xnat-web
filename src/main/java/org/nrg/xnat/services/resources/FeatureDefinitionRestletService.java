package org.nrg.xnat.services.resources;

import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public interface FeatureDefinitionRestletService {

	public void handlePost();

	public Representation represent(Variant variant) throws ResourceException;
}
