package org.nrg.xnat.services.search;

import java.util.List;

import org.nrg.xdat.om.XdatSearch;
import org.nrg.xdat.om.XdatStoredSearch;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.security.UserI;

public interface SearchService {

	public List<XdatSearch> findAllSearch(UserI user);

	public List<XdatSearch> findAllSearchElements(UserI user);

	public XdatSearch findSearchByElement(UserI user, String element);

	public List<XdatSearch> findAllSavedSearch(UserI user) throws UserNotFoundException, UserInitException;

	public List<XdatSearch> findSavedSearchBySearchId(UserI user, String searchId);
}
