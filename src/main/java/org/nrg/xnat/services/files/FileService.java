package org.nrg.xnat.services.files;

import java.util.List;

import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xft.security.UserI;

public interface FileService {
	List<XnatResourcecatalog> findByProject(UserI sessionUser, String projectId);
}
