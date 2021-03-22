package org.nrg.xnat.services.users.impl;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.services.cache.UserDataCache;
import org.nrg.xft.XFTTable;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.users.UserCacheService;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserCacheServiceImpl implements UserCacheService {

	public UserCacheServiceImpl(UserI user, String pXname, String pFile) {

		this._pFile = pFile;
		this._pXname = pXname;
		_globalCachePath = Paths.get(XDAT.getSiteConfigPreferences().getCachePath());
		_userDataCache = XDAT.getContextService().getBean(UserDataCache.class);
		_userPath = _userDataCache.getUserDataCache(user).toAbsolutePath().toString();
		_hasPXname = StringUtils.isNotBlank(_pXname);
		_hasPFile = StringUtils.isBlank(_pFile);

	}

	@Override
	public void findUserCacheResources(UserI user, HttpServletRequest request) {

	}

	@Override
	public void findUserCacheResourceByXname(String xName) {

	}

	@Override
	public void findUserCacheResourceFilesByXname(String xName) {

	}

	@Override
	public void findUserCacheResourceFilesByXnameAndFileName(String xName, String fileName) {

	}

	private final UserDataCache _userDataCache;
	private final Path _globalCachePath;
	private final String _userPath;
	private final String _pXname;
	private final String _pFile;
	private final boolean _hasPXname;
	private final boolean _hasPFile;

}
