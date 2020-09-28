package org.nrg.xnat.services.resources;

import java.io.IOException;

import org.nrg.xft.security.UserI;

public interface ScanListService {

	String getScanResource(UserI userI, String accessedId) throws IOException;

}