package org.nrg.xnat.services.resources;

public interface AuthenticationRestletService {

	boolean allowGet();
	
	void handlePut();

	void handlePost();

}