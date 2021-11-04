package org.nrg.xnat.services.search;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.model.xft.DisplayVersionModel;
import org.nrg.xapi.model.SearchElement;
import org.nrg.xapi.model.XnatSearchElement;
import org.nrg.xdat.om.XdatSearchI;
import org.nrg.xdat.om.XdatStoredSearchI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.ActionNameAbsent;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.JustificationAbsent;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.model.util.XnatEventUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SearchService {
    List<XdatSearchI> findAllSearch(UserI user);

    List<SearchElement> findAllSearchElements(UserI user, boolean secured, boolean readable, boolean used);

    List<XnatSearchElement> findAllSearchElementsByElementName(UserI user, String elementName);

    List<XdatStoredSearchI> findAllSavedSearch(UserI user, String username, String allBundles, String includeTag) throws NotFoundException;

    Optional<XdatStoredSearchI> findSavedSearchBySearchId(UserI user, String searchId, String dv, String project) throws InsufficientPrivilegesException;

    void deleteSavedSearchBySearchId(UserI user, String searchId, XnatEventUtil event) throws SQLException;

    XdatStoredSearchI updateStoredSearch(UserI user, XdatStoredSearchI xdatStoredSearch, String searchId, Boolean saveAs, XnatEventUtil event) throws InitializationException;

    Optional<DisplayVersionModel> findSearchElementVersionByElementName(UserI user, String elementName);

    void updateSearchElement(UserI user, XdatSearchI xdatSearch, String elementName, boolean secure, String singular, String plural, String code);

    XdatStoredSearchI create(UserI user, XdatStoredSearchI xdatStoredSearch);

    /**
     * Returns the {@link XdatStoredSearchI stored search} with the requested search ID in the specified project.
     *
     * @param user      The user requesting the stored search.
     * @param projectId The project in which the search should be located.
     * @param searchId  The ID of the search to retrieve.
     *
     * @return The specified stored search as an optional.
     *
     * @throws DataFormatException Thrown when the required parameters aren't valid.
     * @throws NotFoundException   Thrown only when the specified project doesn't exist.
     */
    Optional<XdatStoredSearchI> findSavedSearchByProjectIdAndSearchId(UserI user, String projectId, String searchId) throws DataFormatException, NotFoundException;

    void deleteSavedSearchByProjectIdAndSearchId(UserI user, String projectId, String searchId) throws JustificationAbsent, ActionNameAbsent;
}
