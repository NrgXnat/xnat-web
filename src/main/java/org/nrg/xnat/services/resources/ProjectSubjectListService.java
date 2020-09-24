package org.nrg.xnat.services.resources;

import java.io.IOException;
import java.util.ArrayList;

import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperElement;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

public interface ProjectSubjectListService {
	
	public String getProjectSubjectById(String projectId) throws IOException;

	public String getProjectSubjectById(String projectId, String subjectId) throws IOException;

	public String getProjectSubjectResource(String projectId, String subjectId) throws IOException;
	
	//boolean allowPost();

	//void handlePost();

	//ArrayList<String> getDefaultFields(GenericWrapperElement e);

	//String getDefaultElementName();

	//Representation represent(Variant variant);

}