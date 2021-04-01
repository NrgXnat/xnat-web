package org.nrg.xnat.services.search.impl;

import java.util.List;

import org.nrg.xdat.om.XdatSearch;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.search.SearchService;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService{
	
	@Override
	public List<XdatSearch> findAllSearch(UserI user) {
		return null;
	}

	@Override
	public List<XdatSearch> findAllSearchElements(UserI user) {
		return null;
	}

	@Override
	public XdatSearch findSearchByElement(UserI user, String element) {
		return null;
	}

	@Override
	public List<XdatSearch> findAllSavedSearch(UserI user) throws UserNotFoundException, UserInitException {
		return null;
	}
	
	@Override
	public List<XdatSearch> findSavedSearchBySearchId(UserI user, String searchId) {
		return null;
	}

}
