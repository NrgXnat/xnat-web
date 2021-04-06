package org.nrg.xnat.services.search;

import java.sql.SQLException;
import java.util.List;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XdatSearch;
import org.nrg.xdat.om.XdatStoredSearch;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.FieldNotFoundException;
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xft.security.UserI;

public interface SearchService {

	public List<XdatSearch> findAllSearch(UserI user);

	public List<XdatSearch> findAllSearchElements(UserI user);

	public XdatSearch findSearchByElement(UserI user, String element);

	public List<XdatStoredSearch> findAllSavedSearch(UserI user) throws UserNotFoundException, UserInitException, DataFormatException;

	public XdatStoredSearch  findSavedSearchBySearchId(UserI user, String searchId) throws InsufficientPrivilegesException, NotFoundException, Exception;

	public void deleteSavedSearchBySearchId(UserI user, String searchId) throws SQLException, Exception;
}
