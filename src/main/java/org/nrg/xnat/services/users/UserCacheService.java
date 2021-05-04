package org.nrg.xnat.services.users;

public interface UserCacheService {
	
	 void findUserCacheResourceByXname(String xName);

	 void findUserCacheResourceFilesByXname(String xName);

	 void findUserCacheResourceFilesByXnameAndFileName(String xName, String fileName);

}
