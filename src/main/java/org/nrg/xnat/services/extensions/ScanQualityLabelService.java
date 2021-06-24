package org.nrg.xnat.services.extensions;

import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xft.security.UserI;

public interface ScanQualityLabelService {

	String findAllScanQualityLable(UserI user, String projectId) throws InitializationException;  
}
