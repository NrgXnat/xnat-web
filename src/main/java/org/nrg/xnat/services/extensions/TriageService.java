package org.nrg.xnat.services.extensions;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.nrg.xft.security.UserI;
import org.nrg.xnat.extensions.util.TriageUtil;

public interface TriageService {

	List<TriageUtil> findTriageByProjectId(UserI user, String projectId, HttpServletRequest request);
	
	void findTriageByProjectIdAndXname(UserI user, String projectId, String xName, HttpServletRequest request);
	
	void findTriagefilesByProjectIdAndXname(UserI user, String projectId, String xName, HttpServletRequest request);
	
	void findTriagefilesByProjectIdAndXnameAndFiles(UserI user, String projectId, String xName, String file, HttpServletRequest request);
	
	void deleteTriage(UserI user, String projectId, String xname, String file, String eventReason, String eventComment, String eventId);
}
