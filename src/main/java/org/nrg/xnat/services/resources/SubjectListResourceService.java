package org.nrg.xnat.services.resources;

import java.io.IOException;

import org.nrg.xft.security.UserI;

public interface SubjectListResourceService {
	public String getSubjectResource(UserI userI, String subjectId) throws IOException, Exception;

}
