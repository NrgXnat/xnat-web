package org.nrg.xnat.services.search.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.generics.GenericUtils;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.model.*;
import org.nrg.xapi.model.xft.DisplayFieldReference;
import org.nrg.xapi.model.xft.DisplayVersionModel;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.collections.DisplayFieldCollection.DisplayFieldNotFoundException;
import org.nrg.xdat.display.*;
import org.nrg.xdat.om.*;
import org.nrg.xdat.schema.SchemaElement;
import org.nrg.xdat.search.CriteriaCollection;
import org.nrg.xdat.search.DisplaySearch;
import org.nrg.xdat.security.ElementSecurity;
import org.nrg.xdat.security.SecurityManager;
import org.nrg.xdat.security.XDATUser;
import org.nrg.xdat.security.helpers.Groups;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.helpers.UserHelper;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFT;
import org.nrg.xft.XFTItem;
import org.nrg.xft.XFTTool;
import org.nrg.xft.collections.ItemCollection;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.ActionNameAbsent;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.JustificationAbsent;
import org.nrg.xft.exception.*;
import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperElement;
import org.nrg.xft.schema.Wrappers.XMLWrapper.SAXReader;
import org.nrg.xft.search.ItemSearch;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    public SearchServiceImpl(final NamedParameterJdbcTemplate template) {
        _template = template;
    }

    @Override
    public List<XdatSearchI> findAllSearch(final UserI user) {
        return GenericUtils.convertToTypedList(XdatSearch.getAllXdatSearchs(user, false), XdatSearchI.class);
    }

    @Override
    public List<SearchElement> findAllSearchElements(final UserI user, final boolean secured, final boolean readable, final boolean used) {
        try {
            final Map<String, ElementSecurity> elementSecurities = ElementSecurity.GetElementSecurities();
            if (!elementSecurities.isEmpty()) {
                final Map<String, Long> counts = readable ? UserHelper.getUserHelperService(user).getReadableCounts() : XDAT.getTotalCounts();
                return elementSecurities.entrySet().stream()
                                        .filter(entry -> !StringUtils.startsWith(entry.getKey(), "xdat:"))
                                        .filter(entry -> !secured || entry.getValue().isSecure())
                                        .filter(entry -> !used || counts.containsKey(entry.getKey()))
                                        .map(entry -> SearchElement.builder()
                                                                   .singular(StringUtils.defaultIfBlank(entry.getValue().getSingularDescription(), entry.getKey()))
                                                                   .plural(StringUtils.defaultIfBlank(entry.getValue().getPluralDescription(), entry.getKey()))
                                                                   .secured(BooleanUtils.toBooleanDefaultIfNull(entry.getValue().isSecure(), false))
                                                                   .elementName(entry.getKey())
                                                                   .count(counts.getOrDefault(entry.getKey(), 0L))
                                                                   .build())
                                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("User {} searched for element securities but an error occurred", user.getUsername(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<XnatSearchElement> findAllSearchElementsByElementName(final UserI user, final String elementNames) {
        if (StringUtils.isBlank(elementNames)) {
            return Collections.emptyList();
        }
        final List<XnatSearchElement> searchElements = new ArrayList<>();
        for (final String elementName : elementNames.split("[\\s]*,[\\s]*")) {
            SchemaElement schemaElement = null;
            try {
                schemaElement = SchemaElement.GetElement(elementName);
            } catch (XFTInitException e) {
                log.error(XDAT.XFT_INIT_EXCEPTION_MESSAGE, e);
            } catch (ElementNotFoundException e) {
                log.error(XDAT.ELEMENT_NOT_FOUND_MESSAGE, e.ELEMENT, e);
            }
            if (schemaElement == null) {
                log.info("No schema element found for {}, skipping for now", elementName);
                continue;
            }
            final ElementDisplay                    elementDisplay = schemaElement.getDisplay();
            final Hashtable<String, DisplayVersion> versions       = elementDisplay.getVersions();
            try {
                searchElements.add(getVersionElementData(versions));
            } catch (DisplayFieldNotFoundException e) {
                log.error("Display field not found", e);
            }

            searchElements.addAll(getXnatSearchElements(schemaElement, elementDisplay.getSortedFields()));

            @SuppressWarnings("rawtypes") final List<List> customFields = new ArrayList<>();
            try {
                customFields.addAll(UserHelper.getUserHelperService(user).getQueryResultsAsArrayList(String.format(QUERY_PROJECT_SEARCH_FIELDS, elementName)));
            } catch (SQLException | DBPoolException e) {
                log.warn("An error occurred trying to retrieve custom fields for element {}", elementName, e);
            }

            try {
                searchElements.addAll(getXnatSearchDataElements(user, schemaElement, customFields, elementDisplay.getProjectIdentifierField()));
            } catch (XFTInitException e) {
                log.error(XDAT.XFT_INIT_EXCEPTION_MESSAGE, e);
            } catch (ElementNotFoundException e) {
                log.error(XDAT.ELEMENT_NOT_FOUND_MESSAGE, e.ELEMENT, e);
            } catch (FieldNotFoundException e) {
                log.error(XDAT.FIELD_NOT_FOUND_MESSAGE, e.FIELD, e);
            }
        }
        return searchElements;
    }

    @Override
    public Optional<DisplayVersionModel> findSearchElementVersionByElementName(UserI user, String elementName) {
        try {
            final SchemaElement  schemaElement  = SchemaElement.GetElement(elementName);
            final ElementDisplay elementDisplay = schemaElement.getDisplay();
            return Optional.ofNullable(getDisplayVersions(elementDisplay.getVersions()));
        } catch (XFTInitException e) {
            log.error(XDAT.XFT_INIT_EXCEPTION_MESSAGE, e);
        } catch (ElementNotFoundException e) {
            log.error(XDAT.ELEMENT_NOT_FOUND_MESSAGE, e.ELEMENT, e);
        } catch (DisplayFieldNotFoundException e) {
            log.error("Display field not found", e);
        }
        return Optional.empty();
    }

    @Override
    public List<XdatStoredSearchI> findAllSavedSearch(UserI user, String usernameToGetListFor, String getAllBundles, String includeTagged) throws NotFoundException {
        String query = null;
        try {
            query = getSavedSearchQuery(user, usernameToGetListFor, getAllBundles, includeTagged);
        } catch (UserNotFoundException | DataFormatException | UserInitException e) {
            e.printStackTrace();
        }
        List<XdatStoredSearchI> savedSearches = _template.query(query, new MapSqlParameterSource(), new XdatStoredSearchRowMapper(user));
        if (Objects.isNull(savedSearches) || savedSearches.isEmpty()) {
            throw new NotFoundException(XdatStoredSearch.SCHEMA_ELEMENT_NAME);
        }
        return savedSearches;
    }

    @SuppressWarnings("CommentedOutCode")
    @Override
    public Optional<XdatStoredSearchI> findSavedSearchBySearchId(UserI user, String searchId, String displayVersion, String project) throws InsufficientPrivilegesException {
        final XdatStoredSearchI storedSearch = getXssData(user, searchId, displayVersion);
        if (storedSearch != null) {
            verifyXss(user, storedSearch);
            return Optional.of(storedSearch);
        }

        // TODOR2X: This code doesn't do anything. Need to track to wherever this came from originally to see how it's supposed to recover.
        /*
        if (storedSearch != null) {
            getXnatStoredSearchData();
        }
        */
        try {
            return Optional.ofNullable(getXssDataAfterValidate(user, project, searchId));
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<XdatStoredSearchI> findSavedSearchByProjectIdAndSearchId(UserI user, String projectId, String searchId) throws DataFormatException, NotFoundException {
        if (StringUtils.isBlank(projectId)) {
            throw new DataFormatException("You must specify a valid project ID when calling this method");
        }
        if (Objects.isNull(searchId) || searchId.isEmpty()) {
            throw new DataFormatException("You must specify a valid search ID when calling this method");
        }

        final XnatProjectdata project = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
        if (Objects.isNull(project)) {
            throw new NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME, projectId);
        }

        if (searchId.startsWith("@")) {
            return Optional.ofNullable(project.getDefaultSearch(searchId.substring(1)));
        }

        final XdatStoredSearchI storedSearch = XdatStoredSearch.getXdatStoredSearchsById(searchId, user, true);
        return storedSearch == null || !StringUtils.equals(projectId, storedSearch.getTag()) ? Optional.empty() : Optional.of(storedSearch);
    }

    @Override
    public void deleteSavedSearchByProjectIdAndSearchId(UserI user, String projectId, String searchId) throws JustificationAbsent, ActionNameAbsent {
        if (searchId != null) {
            XdatStoredSearchI search = XdatStoredSearch.getXdatStoredSearchsById(searchId, user, false);
            if (search != null) {
                XdatStoredSearchAllowedUserI mine = verifyUserLogin(search, user);
                if (mine != null) {
                    deleteSavedSearch(search, user, mine);
                }
            }
        }
    }

    @Override
    public XdatStoredSearchI updateStoredSearch(UserI user, XdatStoredSearchI xdatStoredSearch, String searchId, Boolean saveAs, XnatEventUtil event) throws InitializationException {
        boolean isNew = false;

        if (xdatStoredSearch.getId() == null || !xdatStoredSearch.getId().equals(searchId)) {
            xdatStoredSearch.setId(searchId);
            isNew = true;
        } else {
            XFTItem xss = ((BaseElement) xdatStoredSearch).getCurrentDBVersion(false);
            if (xss == null) {
                isNew = true;
            } else if (saveAs) {
                while (xss != null) {
                    xdatStoredSearch.setId(xdatStoredSearch.getId() + "_1");
                    xss = ((BaseElement) xdatStoredSearch).getCurrentDBVersion(false);


                }
                isNew = true;
            }
        }

        try {
            if (isNew && xdatStoredSearch.getTag() != null) {
                CriteriaCollection cc = new CriteriaCollection("AND");
                cc.addClause("xdat:stored_search/tag", xdatStoredSearch.getTag());
                cc.addClause("xdat:stored_search/brief-description", xdatStoredSearch.getBriefDescription());
                ItemCollection result = ItemSearch.GetItems(cc, user, false);
                if (result.size() > 0) {
                    isNew = false;
                    xdatStoredSearch.setId(result.getFirst().getStringProperty("ID"));
                }
            }

            verifyPermission(user, xdatStoredSearch);

            isNew = getIsNew(xdatStoredSearch, isNew, user);

            xdatStoredSearch = getXdatStoredSearchWithSaveAs(xdatStoredSearch, saveAs);

            final boolean found = getFoundWithUser(xdatStoredSearch, user) || getFoundWithSearchGroup(xdatStoredSearch, user);

            // TODOR2X: Fairly inscrutable. I'm not sure what this is doing.
            xdatStoredSearch = getXdatStoredSearchWithNotIsNewAndNotFound(isNew, found, xdatStoredSearch, user);
            xdatStoredSearch = getXdatStoredSearchWithIsNewAndNotFound(isNew, found, xdatStoredSearch, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            SaveItemHelper.unauthorizedSave((ItemI) xdatStoredSearch, user, false, true,
                                            XnatEventUtil.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, (isNew) ? "Creating new stored search" : "Modified existing stored search", event));
        } catch (Exception e) {
            log.error("", e);
            throw new InitializationException("Something went wrong", e);
        }
        return XdatStoredSearch.getXdatStoredSearchsById(searchId, user, false);
    }

    @Override
    public XdatStoredSearchI create(UserI user, XdatStoredSearchI search) {
        return null;
    }

    @Override
    public void updateSearchElement(UserI user, XdatSearchI xdatSearch, String elementName, boolean secure, String singular, String plural, String code) {
        try {
            if (XFTTool.ValidateElementName(elementName)) {
                try {
                    XFTItem       found = XFTItem.NewItem(elementName, user);
                    SchemaElement se    = SchemaElement.GetElement(elementName);
                    if (!secure && se.hasField(se.getFullXMLName() + "/project") && se.hasField(se.getFullXMLName() + "/sharing/share/project")) {
                        found.setProperty("secure", Boolean.TRUE);
                        found.setProperty("primary_security_fields.primary_security_field__0", se.getFullXMLName() + "/project");
                        found.setProperty("primary_security_fields.primary_security_field__1", se.getFullXMLName() + "/sharing/share/project");
                    }

                    // TODOR2X: The values for these properties are never actually submitted. The setBooleanProperty() method calls isQueryVariableTrue()
                    //  and isQueryVariableFalse(), but we don't have query variables in service land.
                    setBooleanProperty(found, "browseable", true);
                    setBooleanProperty(found, "searchable", true);
                    setBooleanProperty(found, "secure_read", true);
                    setBooleanProperty(found, "secure_edit", true);
                    setBooleanProperty(found, "secure_create", true);
                    setBooleanProperty(found, "secure_delete", true);
                    setBooleanProperty(found, "accessible", true);
                    setBooleanProperty(found, "secondary_password", false);
                    setBooleanProperty(found, "secure_ip", false);
                    setBooleanProperty(found, "quarantine", false);
                    setBooleanProperty(found, "pre_load", false);

                    if (StringUtils.isNotBlank(singular)) {
                        found.setProperty("singular", singular);
                    }
                    if (StringUtils.isNotBlank(plural)) {
                        found.setProperty("plural", plural);
                    }
                    if (StringUtils.isNotBlank(code)) {
                        found.setProperty("code", code);
                    }

                    int count = 0;
                    setAction(found, count++, "edit", "Edit", "e.gif", "edit", null);
                    setAction(found, count++, "xml", "View XML", "r.gif", null, null);
                    setAction(found, count++, "xml_file", "Download XML", "save.gif", null, null);
                    setAction(found, count, "email_report", "Email", "right2.gif", null, "always");
                } catch (ElementNotFoundException e) {
                    log.error(XDAT.ELEMENT_NOT_FOUND_MESSAGE, e.ELEMENT, e);
                } catch (FieldNotFoundException e) {
                    log.error(XDAT.FIELD_NOT_FOUND_MESSAGE, e.FIELD, e);
                } catch (InvalidValueException e) {
                    log.error("An invalid value was specified for a search property", e);
                }
            }
        } catch (XFTInitException e) {
            log.error("", e);
        }
    }

    @Override
    public void deleteSavedSearchBySearchId(UserI user, String searchId, XnatEventUtil event) {
        if (Objects.nonNull(searchId)) {
            XdatStoredSearchI search = XdatStoredSearch.getXdatStoredSearchsById(searchId, user, false);

            if (search != null) {
                XdatStoredSearchAllowedUserI mine  = null;
                XdatStoredSearchGroupidI     group = null;

                mine = getXdatStoredSearch(mine, search, user);

                group = getXdatStoredSearchGroupid(group, search, user);

                deleteStoredSearch(mine, search, group, user, event);
            }
        }
    }

    //pending impl for confirmation
    @SuppressWarnings("unused")
    private void getXnatStoredSearchData() {

    }

    private XnatSearchElement getVersionElementData(Hashtable<String, DisplayVersion> versions) throws DisplayFieldNotFoundException {
        return XnatSearchElement.builder().displayVersion(getDisplayVersions(versions)).build();
    }

    private DisplayVersionModel getDisplayVersions(final Hashtable<String, DisplayVersion> versions) throws DisplayFieldNotFoundException {
        List<Version> versionDtos = new ArrayList<>();
        for (Entry<String, DisplayVersion> entry : versions.entrySet()) {
            List<DisplayFieldReference> fields = new ArrayList<>();
            for (final DisplayFieldReferenceI field : entry.getValue().getAllFields()) {
                fields.add(DisplayFieldReference.builder().id(field.getId())
                                                .elementName(field.getElementName())
                                                .value(field.getValue())
                                                .visible(field.isVisible())
                                                .type(field.getType())
                                                .header(field.getHeader())
                                                .build());
            }
            versionDtos.add(Version.builder().name(entry.getKey())
                                   .lightColor(entry.getValue().getLightColor())
                                   .darkColor(entry.getValue().getDarkColor())
                                   .defaultSortOrder(entry.getValue().getDefaultSortOrder())
                                   .orderBy(entry.getValue().getDefaultOrderBy())
                                   .fields(fields)
                                   .build());
        }
        return DisplayVersionModel.builder().versions(versionDtos).build();
    }

    @SuppressWarnings("rawtypes")
    private List<XnatSearchElement> getXnatSearchDataElements(final UserI user, final SchemaElement schemaElement, List<List> customFields, DisplayField pi) throws XFTInitException, ElementNotFoundException, FieldNotFoundException {
        final String                  projectXmlPath = schemaElement.getFullXMLName() + "/project";
        final List<XnatSearchElement> searchElements = new ArrayList<>();
        if (GenericWrapperElement.GetFieldForXMLPath(projectXmlPath) != null) {
            for (final Object object : Permissions.getAllowedValues(user, schemaElement.getFullXMLName(), projectXmlPath, "read")) {
                searchElements.add(getSearchElement(schemaElement.getFullXMLName(), pi.getId() + "=" + object, object.toString(), "Label within the " + object + " project.", "string", false, "Label within the " + object + " project.", 2));
                for (final List<?> customField : customFields) {
                    if (customField.get(0).equals(object)) {
                        searchElements.add(getSearchElement(schemaElement.getFullXMLName(), schemaElement.getSQLName().toUpperCase() + "_FIELD_MAP=" + customField.get(1).toString().toLowerCase(), customField.get(1).toString(), "Custom Field: " + customField.get(1), customField.get(2).toString(), false, "Custom Field: " + customField.get(1), 1));
                    }
                }
            }
        }
        return searchElements;
    }

    private List<XnatSearchElement> getXnatSearchElements(final SchemaElement schemaElement, final List<DisplayField> displayFields) {
        return displayFields.stream().filter(DisplayField::isSearchable).map(displayField -> getSearchElement(schemaElement.getFullXMLName(), displayField)).collect(Collectors.toList());
    }

    private XnatSearchElement getSearchElement(final String elementName, final DisplayField displayField) {
        return getSearchElement(elementName, displayField.getId(), displayField.getHeader(), displayField.getSummary(), displayField.getDataType(), displayField instanceof SQLQueryField, StringUtils.getIfBlank(displayField.getDescription(), () -> StringUtils.getIfBlank(displayField.getHeader(), displayField::getId)), 0);
    }

    private XnatSearchElement getSearchElement(String elementName, String fieldId, String header, String summary, String dataType, boolean requiredValues, String description, int src) {
        return XnatSearchElement.builder()
                                .fieldId(StringUtils.defaultIfBlank(fieldId, ""))
                                .header(StringUtils.defaultIfBlank(header, ""))
                                .summary(StringUtils.defaultIfBlank(summary, ""))
                                .type(StringUtils.defaultIfBlank(dataType, ""))
                                .requiresValue(requiredValues)
                                .description(StringUtils.defaultIfBlank(description, ""))
                                .elementName(StringUtils.defaultIfBlank(elementName, ""))
                                .source(src).build();
    }

    private String getSavedSearchQuery(UserI user, String usernameToGetListFor, String getAllBundles, String includeTagged) throws UserNotFoundException, UserInitException, DataFormatException {
        UserI   userToGetListFor;
        boolean userIsAdmin = Groups.isSiteAdmin(user);
        if (userIsAdmin && !StringUtils.isBlank(usernameToGetListFor)) {
            userToGetListFor = new XDATUser(usernameToGetListFor);
        } else {
            userToGetListFor = user;
        }
        final StringBuilder query = new StringBuilder("SELECT DISTINCT xssouter.*, array_agg(xssauouter.login) AS users FROM ( SELECT DISTINCT xss.id FROM xdat_stored_search xss LEFT JOIN xdat_stored_search_allowed_user xssau ON xss.id = xssau.xdat_stored_search_id LEFT JOIN xdat_stored_search_groupid xssag ON xss.id = xssag.allowed_groups_groupid_xdat_sto_id");
        if (!userIsAdmin || StringUtils.isBlank(getAllBundles) || !StringUtils.equalsIgnoreCase(getAllBundles, "true")) {
            query.append(" LEFT JOIN xdat_user_groupid ON xssag.groupid=xdat_user_groupid.groupid WHERE (xss.secure=0 OR xssau.login='").append(userToGetListFor.getLogin()).append("' OR groups_groupid_xdat_user_xdat_user_id=").append(userToGetListFor.getID()).append(")");
            if (includeTagged != null) {
                if (includeTagged.equals("true")) {
                    query.append(" AND xss.tag IS NOT NULL");
                } else {
                    if (!Permissions.getAllProjectIds(XDAT.getContextService().getBean(JdbcTemplate.class)).contains(includeTagged)) {
                        log.error("", new Exception("Unknown tag: " + includeTagged));
                        throw new DataFormatException("");
                    }
                    query.append(" AND xss.tag='").append(includeTagged).append("'");
                }
            } else {
                query.append(" AND xss.tag IS NULL");
            }
        }
        return query.append(") AS ids LEFT JOIN xdat_stored_search xssouter ON xssouter.id = ids.id LEFT JOIN xdat_stored_search_allowed_user xssauouter ON xssouter.id = xssauouter.xdat_stored_search_id GROUP BY xssouter.id").toString();
    }

    private static class XdatStoredSearchRowMapper implements RowMapper<XdatStoredSearchI> {
        XdatStoredSearchRowMapper(final UserI user) {
            _user = user;
        }

        @Override
        public XdatStoredSearchI mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
            return XdatSearch.getXdatStoredSearchsById(resultSet.getString("id"), _user, false);
        }

        private final UserI _user;
    }

    /**
     * Returns a file containing search xmls which was stored on the file system.
     * This provides a way to standardize search xmls outside of the database, for
     * easy sharing across installations.
     *
     * @return The search XMLs stored on the file system.
     */
    private synchronized static File getFileSystemSearch(String name) {
        if (!name.contains("..")) {
            final File file = new File(new File(XFT.GetConfDir()).getParentFile().getParentFile(),
                                       "resources/searches/" + name);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

    private XdatStoredSearchI getXssDataAfterValidate(final UserI user, final String projectIds, final String searchId) throws NotFoundException {
        final XdatStoredSearchI storedSearch = loadStoredSearchFromFile(user, searchId).orElseThrow(() -> new NotFoundException(XdatStoredSearch.SCHEMA_ELEMENT_NAME, searchId));

        // if (mt.equals(MediaType.TEXT_XML) && (filepath == null ||
        // !filepath.startsWith("results")) && !this.hasQueryVariable("project")) {
        // return new FileRepresentation(searchXml, mt);
        // } else {
        if (StringUtils.isNotBlank(projectIds)) {
            final XdatCriteriaSet criteriaSet = new XdatCriteriaSet(user);
            criteriaSet.setMethod("OR");
            try {
                for (final String projectId : projectIds.split("[\\s]*,[\\s]*")) {
                    final XdatCriteria criteria1 = new XdatCriteria(user);
                    criteria1.setSchemaField(storedSearch.getRootElementName() + "/project");
                    criteria1.setComparisonType("=");
                    criteria1.setValue(projectId);
                    criteriaSet.setCriteria(criteria1);

                    final XdatCriteria criteria2 = new XdatCriteria(user);
                    criteria2.setSchemaField(storedSearch.getRootElementName() + "/sharing/share/project");
                    criteria2.setComparisonType("=");
                    criteria2.setValue(projectId);
                    criteriaSet.setCriteria(criteria2);
                }
                storedSearch.setSearchWhere(criteriaSet);
            } catch (Exception ignored) {
            }
        }
        return storedSearch;
    }

    private Optional<XdatStoredSearchI> loadStoredSearchFromFile(final UserI user, final String searchId) {
        // allow loading of saved searches from xml stored on the file system
        final File      searchXml = getFileSystemSearch(searchId);
        final SAXReader reader    = new SAXReader(user);
        try {
            final XFTItem item = reader.parse(searchXml);
            return Optional.of(new XdatStoredSearch(item));
        } catch (IOException e) {
            log.error("An error occurred trying to read the file {}", searchXml, e);
        } catch (SAXException e) {
            log.error("An error occurred trying to parse the file {}", searchXml, e);
        }
        return Optional.empty();
    }

    private void verifyXss(UserI user, final XdatStoredSearchI storedSearch) throws InsufficientPrivilegesException {
        if (!((XdatStoredSearch) storedSearch).hasAllowedUser(user.getUsername()) && !Permissions.canQuery(user, storedSearch.getRootElementName())) {
            throw new InsufficientPrivilegesException(user.getUsername());
        }
    }

    private XdatStoredSearchI getXssData(final UserI user, final String searchId, final String displayVersion) {
        if (!StringUtils.isNotBlank(searchId)) {
            return null;
        }
        if (!searchId.startsWith("@")) {
            return XdatStoredSearch.getXdatStoredSearchsById(searchId, user, true);
        }
        try {
            final DisplaySearch displaySearch = new DisplaySearch();
            displaySearch.setUser(user);
            displaySearch.setDisplay(StringUtils.defaultIfBlank(displayVersion, "listing"));
            displaySearch.setRootElement(searchId.substring(1));
            final XdatStoredSearch storedSearch = displaySearch.convertToStoredSearch(searchId);
            storedSearch.setId(searchId);
            return storedSearch;
        } catch (XFTInitException e) {
            log.error(XDAT.XFT_INIT_EXCEPTION_MESSAGE, e);
        } catch (ElementNotFoundException e) {
            log.error(XDAT.ELEMENT_NOT_FOUND_MESSAGE, e.ELEMENT, e);
        }
        return null;
    }

    private void deleteStoredSearch(XdatStoredSearchAllowedUserI mine, XdatStoredSearchI search, XdatStoredSearchGroupidI group, UserI user, XnatEventUtil event) {
        try {
            if (mine != null) {
                // TODOR2X: Need to add XdatStoredSearchI.getAllowedGroups_groupid() method
                if (search.getAllowedUser().size() > 1 || search.getAllowedGroups_groupid().size() > 0) {
                    SaveItemHelper.authorizedDelete(((ItemI) mine).getItem(), user, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, "Removed user from stored search", event));
                } else {
                    SaveItemHelper.authorizedDelete(((ItemI) search).getItem(), user, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, "Removed stored search", event));
                }
            } else if (group != null) {
                if (search.getAllowedUser().size() > 0 || search.getAllowedGroups_groupid().size() > 1) {
                    SaveItemHelper.authorizedDelete(((ItemI) group).getItem(), user, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, "Removed group from stored search", event));
                } else {
                    SaveItemHelper.authorizedDelete(((ItemI) search).getItem(), user, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, "Removed stored search", event));
                }
            } else if (Roles.isSiteAdmin(user)) {
                SaveItemHelper.authorizedDelete(((ItemI) search).getItem(), user, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, "Removed stored search", event));
            } else {
                throw new InsufficientPrivilegesException(user.getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private XdatStoredSearchGroupidI getXdatStoredSearchGroupid(XdatStoredSearchGroupidI group, XdatStoredSearchI search,
                                                                UserI user) {
        for (XdatStoredSearchGroupidI ag : ((XdatStoredSearch) search).getAllowedGroups_groupid()) {
            if (Groups.isMember(user, ag.getGroupid())) {
                group = ag;
                break;
            }
        }
        return group;
    }

    private XdatStoredSearchAllowedUserI getXdatStoredSearch(final XdatStoredSearchAllowedUserI mine, final XdatStoredSearchI search, final UserI user) {
        return GenericUtils.convertToTypedList(search.getAllowedUser(), XdatStoredSearchAllowedUserI.class).stream().filter(allowedUser -> StringUtils.equals(user.getUsername(), allowedUser.getLogin())).findFirst().orElse(mine);
    }

    private boolean getIsNew(final XdatStoredSearchI storedSearch, final boolean isNew, final UserI user) throws Exception {
        final boolean isPrimary = StringUtils.equals(storedSearch.getId(), StringUtils.defaultIfBlank(storedSearch.getTag(), "") + "_" + storedSearch.getRootElementName()) ||
                                  StringUtils.isNotBlank(storedSearch.getBriefDescription()) && StringUtils.equals(storedSearch.getBriefDescription(), DisplayManager.GetInstance().getPluralDisplayNameForElement(storedSearch.getRootElementName()));
        return (!isNew || !isPrimary || Permissions.can(user, "xnat:projectData/ID", storedSearch.getTag(), SecurityManager.DELETE)) && isNew;
    }

    private void verifyPermission(UserI user, XdatStoredSearchI xdatStoredSearch)
            throws InsufficientPrivilegesException {
        if (!Permissions.canQuery(user, xdatStoredSearch.getRootElementName())) {
            throw new InsufficientPrivilegesException(user.getUsername());
        }
    }

    private boolean getFoundWithSearchGroup(final XdatStoredSearchI search, UserI user) {
        return GenericUtils.convertToTypedList(search.getAllowedGroups_groupid(), XdatStoredSearchGroupidI.class).stream().anyMatch(group -> Groups.isMember(user, group.getGroupid()));
    }

    private boolean getFoundWithUser(final XdatStoredSearchI search, final UserI user) {
        return GenericUtils.convertToTypedList(search.getAllowedUser(), XdatStoredSearchAllowedUserI.class).stream().anyMatch(allowedUser -> StringUtils.equals(allowedUser.getLogin(), user.getUsername()));
    }

    private XdatStoredSearchI getXdatStoredSearchWithSaveAs(final XdatStoredSearchI search, final boolean saveAs) {
        if (saveAs) {
            while (!search.getAllowedGroups_groupid().isEmpty()) {
                search.removeAllowedGroups_groupid(0);
            }

            while (!search.getAllowedUser().isEmpty()) {
                search.removeAllowedUser(0);
            }
        }
        return search;
    }

    private XdatStoredSearchI getXdatStoredSearchWithNotIsNewAndNotFound(boolean isNew, boolean found,
                                                                         XdatStoredSearchI xdatStoredSearch, UserI user) throws InsufficientPrivilegesException {
        if (!found && !isNew) {
            if (xdatStoredSearch.getTag() != null && !xdatStoredSearch.getTag().equals("")) {
                try {
                    if (!Permissions.canEdit(user, "xnat:projectData/ID", xdatStoredSearch.getTag())) {
                        throw new InsufficientPrivilegesException(user.getUsername());
                    } else {
                        XdatStoredSearchAllowedUserI au = new XdatStoredSearchAllowedUser(user);
                        au.setLogin(user.getLogin());
                        xdatStoredSearch.setAllowedUser((ItemI) au);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                throw new InsufficientPrivilegesException(user.getUsername());
            }
        }
        return xdatStoredSearch;
    }

    private XdatStoredSearchI getXdatStoredSearchWithIsNewAndNotFound(boolean isNew, boolean found,
                                                                      XdatStoredSearchI xdatStoredSearch, UserI user) throws Exception {
        if (isNew && !found) {
            XdatStoredSearchAllowedUserI au = new XdatStoredSearchAllowedUser(user);
            au.setLogin(user.getLogin());
            xdatStoredSearch.setAllowedUser((ItemI) au);
        }
        return xdatStoredSearch;
    }

    @SuppressWarnings("CommentedOutCode")
    private void setBooleanProperty(XFTItem found, String field, boolean defaultValue) {
        try {
            // TODOR2X: This is where isQueryVariableFalse() and isQueryVariableTrue() are called and it doesn't make any sense here.
            //  Instead, just set the default boolean value for now.
            /*
            if (_default && !isQueryVariableFalse(field)) {
                found.setProperty(field, Boolean.TRUE);
            } else if (!_default && !isQueryVariableTrue(field)) {
                found.setProperty(field, Boolean.FALSE);
            } else if (_default) {
                found.setProperty(field, Boolean.FALSE);
            } else {
                found.setProperty(field, Boolean.TRUE);
            }
            */
            found.setProperty(field, defaultValue);
        } catch (XFTInitException e) {
            log.error(XDAT.XFT_INIT_EXCEPTION_MESSAGE, e);
        } catch (ElementNotFoundException e) {
            log.error(XDAT.ELEMENT_NOT_FOUND_MESSAGE, e.ELEMENT, e);
        } catch (FieldNotFoundException e) {
            log.error(XDAT.FIELD_NOT_FOUND_MESSAGE, e.FIELD, e);
        } catch (InvalidValueException e) {
            log.error("An invalid value was specified for the field {}", field, e);
        }
    }

    private void setAction(final XFTItem found, final int count, final String actionName, final String displayName, final String img, final String secureAccess, final String popup) {
        setProperty(found, "xdat:element_security.element_actions.element_action__" + count + ".element_action_name", actionName);
        setProperty(found, "xdat:element_security.element_actions.element_action__" + count + ".display_name", displayName);
        setProperty(found, "xdat:element_security.element_actions.element_action__" + count + ".sequence", count);
        if (StringUtils.isNotBlank(img)) {
            setProperty(found, "xdat:element_security.element_actions.element_action__" + count + ".image", img);
        }
        if (StringUtils.isNotBlank(secureAccess)) {
            setProperty(found, "xdat:element_security.element_actions.element_action__" + count + ".secureAccess", secureAccess);
        }
        if (StringUtils.isNotBlank(popup)) {
            setProperty(found, "xdat:element_security.element_actions.element_action__" + count + ".popup", popup);
        }
    }

    private void setProperty(final XFTItem item, final String xmlPath, final Object value) {
        try {
            item.setProperty(xmlPath, value);
        } catch (XFTInitException e) {
            log.error(XDAT.XFT_INIT_EXCEPTION_MESSAGE, e);
        } catch (ElementNotFoundException e) {
            log.error(XDAT.ELEMENT_NOT_FOUND_MESSAGE, e.ELEMENT, e);
        } catch (FieldNotFoundException e) {
            log.error(XDAT.FIELD_NOT_FOUND_MESSAGE, e.FIELD, e);
        } catch (InvalidValueException e) {
            log.error("An invalid value was specified for the field {}: {}", xmlPath, value, e);
        }
    }

    private void deleteSavedSearch(final XdatStoredSearchI search, UserI user, XdatStoredSearchAllowedUserI mine) throws JustificationAbsent, ActionNameAbsent {
        final PersistentWorkflowI workflow = PersistentWorkflowUtils.getOrCreateWorkflowData(null, user, ((ItemI) search).getItem(), EventUtils.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, EventUtils.TYPE.WEB_SERVICE, "Deleted Project stored search"));
        try {
            if (search.getAllowedUser().size() > 1 || search.getAllowedGroups_groupid().size() > 0) {
                SaveItemHelper.authorizedDelete(((ItemI) mine).getItem(), user, workflow.buildEvent());
            } else {
                SaveItemHelper.authorizedDelete(((ItemI) search).getItem(), user, workflow.buildEvent());
            }
            PersistentWorkflowUtils.complete(workflow, workflow.buildEvent());
        } catch (Exception e) {
            try {
                PersistentWorkflowUtils.fail(workflow, workflow.buildEvent());
            } catch (Exception e1) {
                log.error("An error occurred trying to save a workflow for search {}", search.getId(), e1);
            }
        }
    }

    private XdatStoredSearchAllowedUserI verifyUserLogin(XdatStoredSearchI search, UserI user) {
        return GenericUtils.convertToTypedList(search.getAllowedUser(), XdatStoredSearchAllowedUserI.class).stream().filter(allowedUser -> StringUtils.equals(allowedUser.getLogin(), user.getUsername())).findFirst().orElse(null);
    }

    private static final String                     QUERY_PROJECT_SEARCH_FIELDS = "SELECT DISTINCT ON (fdgf.name) " +
                                                                                  "    dtp.xnat_projectdata_id AS project, " +
                                                                                  "    fdgf.name, " +
                                                                                  "    fdgf.datatype           AS type " +
                                                                                  "FROM " +
                                                                                  "    xnat_abstractprotocol dtp " +
                                                                                  "        LEFT JOIN xnat_datatypeprotocol_fieldgroups dtp_fg ON dtp.xnat_abstractprotocol_id = dtp_fg.xnat_datatypeprotocol_xnat_abstractprotocol_id " +
                                                                                  "        LEFT JOIN xnat_fielddefinitiongroup fdg ON dtp_fg.xnat_fielddefinitiongroup_xnat_fielddefinitiongroup_id = fdg.xnat_fielddefinitiongroup_id " +
                                                                                  "        LEFT JOIN xnat_fielddefinitiongroup_field fdgf ON fdg.xnat_fielddefinitiongroup_id = fdgf.fields_field_xnat_fielddefiniti_xnat_fielddefinitiongroup_id " +
                                                                                  "WHERE " +
                                                                                  "    dtp.data_type ~* '^%s$' AND " +
                                                                                  "    fdgf.type = 'custom'";
    private final        NamedParameterJdbcTemplate _template;
}
