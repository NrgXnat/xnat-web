package org.nrg.xnat.services.search;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.collections.DisplayFieldCollection.DisplayFieldNotFoundException;
import org.nrg.xdat.om.XdatSearch;
import org.nrg.xdat.om.XdatStoredSearch;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.ActionNameAbsent;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.JustificationAbsent;
import org.nrg.xft.exception.DBPoolException;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.FieldNotFoundException;
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.dto.search.DisplayVersionDto;
import org.nrg.xnat.dto.search.SearchElementDto;
import org.nrg.xnat.dto.search.VersionDto;
import org.nrg.xnat.dto.search.XnatSearchElementDto;
import org.nrg.xnat.model.util.XnatEventUtil;

public interface SearchService {

	 List<XdatSearch> findAllSearch(UserI user) throws NotFoundException;

	 List<SearchElementDto> findAllSearchElements(UserI user,String secured, String readable, String used) throws NotFoundException ;
	
	 List<XnatSearchElementDto> findAllSearchElementsByElementName(UserI user, String elementName) ;

	 List<XdatStoredSearch> findAllSavedSearch(UserI user,String username, String allBundles, String includeTag) throws NotFoundException;

	 Optional<XdatStoredSearch>  findSavedSearchBySearchId(UserI user, String searchId, String dv,String project) throws InsufficientPrivilegesException;

	 void deleteSavedSearchBySearchId(UserI user, String searchId,  XnatEventUtil event) throws SQLException;
	
	 XdatStoredSearch updateStoredSearch(UserI user, XdatStoredSearch xdatStoredSearch, String searchId,  Boolean saveAs, XnatEventUtil event) throws InitializationException;
	 
	 Optional<DisplayVersionDto> findSearchElementVersionByElementName(UserI user, String elementName) throws DisplayFieldNotFoundException, NotFoundException ;

	 void updateSearchElement(UserI user, XdatSearch xdatSearch, String elementName, boolean secure, String singular, String plural, String code );
	
	 XdatStoredSearch create(UserI user, XdatStoredSearch xdatStoredSearch);
	
	 Optional<XdatStoredSearch> findSavedSearchByProjectIdAndSearchId(UserI user, String projectId, String searchId) throws DataFormatException, NotFoundException ;

	 void deleteSavedSearchByProjectIdAndSearchId(UserI user, String projectId, String searchId) throws JustificationAbsent, ActionNameAbsent ;
}
