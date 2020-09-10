package org.nrg.xnat.services.resources;

public interface AuthenticationRestletService {

	boolean allowGet();

	boolean allowPost();

	boolean allowPut();

	void handlePut();

	void handlePost();

}