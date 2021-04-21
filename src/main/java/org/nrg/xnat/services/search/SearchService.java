package org.nrg.xnat.services.search;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.collections.DisplayFieldCollection.DisplayFieldNotFoundException;
import org.nrg.xdat.om.XdatSearch;
import org.nrg.xdat.om.XdatStoredSearch;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.exception.DBPoolException;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.FieldNotFoundException;
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.dto.search.DisplayVersionDto;
import org.nrg.xnat.dto.search.SearchElementDto;
import org.nrg.xnat.dto.search.VersionDto;
import org.nrg.xnat.dto.search.XnatSearchElementDto;

public interface SearchService {

	public Optional<List<XdatSearch>> findAllSearch(UserI user) throws NotFoundException;

	public Optional<List<SearchElementDto>> findAllSearchElements(UserI user,String secured, String readable, String used) throws NotFoundException ;
	
	public Optional<List<XnatSearchElementDto>> findAllSearchElementsByElementName(UserI user, String elementName) ;

	public Optional<List<XdatStoredSearch>> findAllSavedSearch(UserI user) throws NotFoundException;

	public Optional<XdatStoredSearch>  findSavedSearchBySearchId(UserI user, String searchId) throws InsufficientPrivilegesException;

	public void deleteSavedSearchBySearchId(UserI user, String searchId) throws SQLException, Exception;
	
	public XdatStoredSearch updateStoredSearch(UserI user, XdatStoredSearch xdatStoredSearch, String searchId,  Boolean saveAs) throws XFTInitException, ElementNotFoundException, FieldNotFoundException, Exception;
	
	public Optional<DisplayVersionDto> findSearchElementVersionByElementName(UserI user, String elementName) throws DisplayFieldNotFoundException, NotFoundException ;

	public void updateSearchElement(UserI user, XdatSearch xdatSearch, String elementName, boolean secure, String singular, String plural, String code );
	
	public XdatStoredSearch create(UserI user, XdatStoredSearch xdatStoredSearch);
	
	public Optional<XdatStoredSearch> findSavedSearchByProjectIdAndSearchId(UserI user, String projectId, String searchId) throws DataFormatException, NotFoundException ;

	public void deleteSavedSearchByProjectIdAndSearchId(UserI user, String projectId, String searchId) throws Exception;
}
