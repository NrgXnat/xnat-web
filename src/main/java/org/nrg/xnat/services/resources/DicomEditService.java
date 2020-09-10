package org.nrg.xnat.services.resources;

import org.nrg.xnat.helpers.editscript.DicomEdit.ResourceScope;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public interface DicomEditService {
	
	public String buildScriptPath(ResourceScope scope, Object identifier);

	public String getProjectScriptPath(final Object project);

	public String getStudyScriptPath(final Object study);

	public Representation represent(Variant variant) throws ResourceException;

	public boolean allowGet();

	public boolean allowPost();

	public void handlePut();
}
