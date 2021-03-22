package org.nrg.xnat.services.users;

import javax.servlet.http.HttpServletRequest;

import org.nrg.xft.security.UserI;

public interface UserCacheService {
	
	public void findUserCacheResources(UserI user, HttpServletRequest request);

	public void findUserCacheResourceByXname(String xName);

	public void findUserCacheResourceFilesByXname(String xName);

	public void findUserCacheResourceFilesByXnameAndFileName(String xName, String fileName);

}
