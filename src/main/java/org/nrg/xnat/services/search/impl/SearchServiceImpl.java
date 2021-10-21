package org.nrg.xnat.services.search.impl;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.collections.DisplayFieldCollection.DisplayFieldNotFoundException;
import org.nrg.xdat.display.DisplayField;
import org.nrg.xdat.display.DisplayManager;
import org.nrg.xdat.display.ElementDisplay;
import org.nrg.xdat.display.SQLQueryField;
import org.nrg.xdat.model.*;
import org.nrg.xdat.model.XdatCriteriaSetI;
import org.nrg.xdat.model.XdatSearchI;
import org.nrg.xdat.model.XdatStoredSearchAllowedUserI;
import org.nrg.xdat.model.XdatStoredSearchGroupidI;
import org.nrg.xdat.model.XdatStoredSearchI;
import org.nrg.xdat.om.*;
import org.nrg.xdat.om.base.BaseXnatProjectdata;
import org.nrg.xdat.om.base.auto.AutoXdatStoredSearch;
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
import org.nrg.xft.exception.DBPoolException;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.FieldNotFoundException;
import org.nrg.xft.exception.InvalidValueException;
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperElement;
import org.nrg.xft.schema.Wrappers.XMLWrapper.SAXReader;
import org.nrg.xft.search.ItemSearch;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xft.utils.XftStringUtils;
import org.nrg.xapi.model.SearchElement;
import org.nrg.xapi.model.DisplayFieldReferenceI;
import org.nrg.xapi.model.DisplayVersion;
import org.nrg.xapi.model.Version;
import org.nrg.xapi.model.XnatSearchElement;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	public SearchServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}

	@Override
	public List<XdatSearch> findAllSearch(UserI user) throws NotFoundException {
		List<XdatSearch> searches = XdatSearch.getAllXdatSearchs(user, false);
		if (Objects.isNull(searches) || searches.isEmpty()) {
			throw new NotFoundException(XdatSearch.SCHEMA_ELEMENT_NAME);
		}
		return searches;
	}

	@Override
	public List<SearchElement> findAllSearchElements(UserI user, String secured, String readable, String used) throws NotFoundException {
		Map<String, ElementSecurity> elementSecurities = null;
		List<SearchElement> elementDtos = new ArrayList<>();
		try {
			elementSecurities = new HashMap<>(ElementSecurity.GetElementSecurities());
		} catch (Exception e) {
			log.error("User {} searched for a elementSecurities but that doesn't exist", user.getUsername());
			e.printStackTrace();
		}
		if (Objects.nonNull(elementSecurities)) {

			final Map<String, Long> counts = readable != null ? UserHelper.getUserHelperService(user).getReadableCounts() : XDAT.getTotalCounts();

			elementSecurities = filterElementSecurityWithUsed(filterElementSecurityWithSecured(filterElementSecurityWithXdat(elementSecurities), secured), used, counts);
			try {
				elementDtos = getXnatSearchElement(elementSecurities, counts);
			} catch (XFTInitException | ElementNotFoundException | FieldNotFoundException e) {
				log.error("User {} searched for a SearchElements but that doesn't exist", user.getUsername());
				e.printStackTrace();
			}
		} else {
			throw new NotFoundException(ElementSecurity.SCHEMA_ELEMENT_NAME);
		}

		if (Objects.isNull(elementDtos) || elementDtos.isEmpty())
			throw new NotFoundException(ElementSecurity.SCHEMA_ELEMENT_NAME);

		return elementDtos;
	}

	@Override
	public List<XnatSearchElement> findAllSearchElementsByElementName(UserI user, String elementName) {
		ArrayList<String> elementNames = XftStringUtils.CommaDelimitedStringToArrayList(elementName);
		List<XnatSearchElement> elementDtos = new ArrayList<XnatSearchElement>();
		for (String en : elementNames) {
			SchemaElement se = null;
			try {
				se = SchemaElement.GetElement(en);
			} catch (XFTInitException | ElementNotFoundException e) {
				e.printStackTrace();
			}
			ElementDisplay ed = se.getDisplay();
			ArrayList displays = ed.getSortedFields();
			Iterator iter = displays.iterator();

			try {
				elementDtos = getVersionElementData(ed.getVersions(), elementDtos);
			} catch (DisplayFieldNotFoundException e) {
				e.printStackTrace();
			}

			elementDtos = getXnatSearchElements(iter, elementDtos, se);

			List<List> custom_fields = null;
			try {
				custom_fields = UserHelper.getUserHelperService(user).getQueryResultsAsArrayList("SELECT DISTINCT ON (name) dtp.xnat_projectdata_id AS project, fdgf.name, fdgf.datatype AS type FROM xnat_abstractprotocol dtp LEFT JOIN xnat_datatypeprotocol_fieldgroups dtp_fg ON dtp.xnat_abstractprotocol_id=dtp_fg.xnat_datatypeprotocol_xnat_abstractprotocol_id LEFT JOIN xnat_fielddefinitiongroup fdg  ON dtp_fg.xnat_fielddefinitiongroup_xnat_fielddefinitiongroup_id=fdg.xnat_fielddefinitiongroup_id LEFT JOIN xnat_fielddefinitiongroup_field fdgf ON fdg.xnat_fielddefinitiongroup_id=fdgf.fields_field_xnat_fielddefiniti_xnat_fielddefinitiongroup_id WHERE dtp.data_type='"
								+ en + "' AND fdgf.type='custom'");
			} catch (SQLException | DBPoolException e) {
				e.printStackTrace();
			}

			DisplayField pi = ed.getProjectIdentifierField();

			try {
				elementDtos = getXnatSearchDataElements(se, user, elementDtos, custom_fields, pi);
			} catch (XFTInitException | ElementNotFoundException | FieldNotFoundException e) {
				e.printStackTrace();
			}
		}
		return elementDtos;
	}

	@Override
	public Optional<DisplayVersion> findSearchElementVersionByElementName(UserI user, String elementName) throws DisplayFieldNotFoundException, NotFoundException {
		SchemaElement se = null;
		try {
			se = SchemaElement.GetElement(elementName);
		} catch (XFTInitException | ElementNotFoundException e) {
		}
		ElementDisplay ed = se.getDisplay();
		DisplayVersion displayVersion = getDisplayVersions(ed.getVersions());
		if (Objects.isNull(displayVersion))
			throw new NotFoundException(ElementSecurity.SCHEMA_ELEMENT_NAME);
		return Optional.of(displayVersion);
	}

	@Override
	public List<XdatStoredSearchI> findAllSavedSearch(UserI user, String usernameToGetListFor, String getAllBundles, String includeTagged) throws NotFoundException {
		String query = null;
		try {
			query = getSavedSearchQuery(user, usernameToGetListFor, getAllBundles, includeTagged);
		} catch (UserNotFoundException | DataFormatException | UserInitException e) {
			e.printStackTrace();
		}
		List<XdatStoredSearchI> savedSearches = _template.query(query, new MapSqlParameterSource(),
				new XdatStoredSearchRowMapper(user));
		if (Objects.isNull(savedSearches) || savedSearches.isEmpty())
			throw new NotFoundException(XdatStoredSearch.SCHEMA_ELEMENT_NAME);
		return savedSearches;
	}

	@Override
	public Optional<XdatStoredSearchI> findSavedSearchBySearchId(UserI user, String searchId, String dv, String project) throws InsufficientPrivilegesException {
		XdatStoredSearchI xss = null;
		String sID = searchId;
		boolean loadedFromFile = false;

		xss = getXssData(xss, sID, user,dv);
		if (xss != null)
			verifyXss((XdatStoredSearchI) xss, user);
		else
			xss = (XdatStoredSearchI) getXssDataAfterValidate((XdatStoredSearchI) xss, sID, loadedFromFile, user,project);

		if (xss != null) {
			getXnatStoredSearchData();
		}

		return Optional.of(xss);
	}
	
	@Override
	public Optional<XdatStoredSearchI> findSavedSearchByProjectIdAndSearchId(UserI user, String projectId,
																			 String searchId) throws DataFormatException, NotFoundException {
		XdatStoredSearchI xdatStoredSearch = (XdatStoredSearchI) new XdatStoredSearch();
		XnatProjectdataI xnatProjectdata = new XnatProjectdata();

		if (Objects.isNull(projectId) || projectId.isEmpty())
			throw new DataFormatException("The requested projectId wasn't found");
		if (Objects.isNull(searchId) || searchId.isEmpty())
			throw new DataFormatException("The searchId projectId wasn't found");

		xnatProjectdata = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);

		if (Objects.isNull(xnatProjectdata))
			throw new NotFoundException(XdatStoredSearch.SCHEMA_ELEMENT_NAME, projectId);

		if (searchId.startsWith("@"))
			xdatStoredSearch = (XdatStoredSearchI) ((BaseXnatProjectdata)xnatProjectdata).getDefaultSearch(searchId.substring(1));
		else
			xdatStoredSearch = (XdatStoredSearchI) XdatStoredSearch.getXdatStoredSearchsById(xdatStoredSearch, user, true);

		if (Objects.isNull(xdatStoredSearch))
			throw new NotFoundException("No saved search with XdatStoredSearch was found {} " + searchId);

		return Optional.of(xdatStoredSearch);
	}

	@Override
	public void deleteSavedSearchByProjectIdAndSearchId(UserI user, String projectId, String searchId)
			throws JustificationAbsent, ActionNameAbsent {
		if (searchId != null) {
			XdatStoredSearchI search = (XdatStoredSearchI) XdatStoredSearch.getXdatStoredSearchsById(searchId, user, false);
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

			verfiyPermission(user, xdatStoredSearch);

			isNew = getIsNew(xdatStoredSearch, isNew, user);

			xdatStoredSearch =  getXdatStoredSearchWithSaveAs(xdatStoredSearch, saveAs);

			boolean found = false;

			found = getFoundWithUser(xdatStoredSearch, found, user);

			found = getFoundWithSearchGroup(xdatStoredSearch, found, user);

			xdatStoredSearch = getXdatStoredSearchWithNotIsNewAndNotFound(isNew, found, xdatStoredSearch, user);

			xdatStoredSearch = getXdatStoredSearchWithIsNewAndNotFound(isNew, found, xdatStoredSearch, user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			SaveItemHelper.unauthorizedSave((ItemI) xdatStoredSearch, user, false, true,
					XnatEventUtil.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, (isNew) ? "Creating new stored search" : "Modified existing stored search",event));
		} catch (Exception e) {
			log.error("", e);
			throw new InitializationException("Something went worng");
		}
		return (XdatStoredSearchI) XdatStoredSearch.getXdatStoredSearchsById(searchId, user, false);
	}
	
	@Override
	public XdatStoredSearchI create(UserI user, XdatStoredSearchI search) {
		return null;
	}

	@Override
	public void updateSearchElement(UserI user, XdatSearchI xdatSearch, String elementName, boolean secure,
			String singular, String plural, String code) {
		try {
			if (XFTTool.ValidateElementName(elementName)) {
				try {
					XFTItem found = XFTItem.NewItem(elementName, user);
					SchemaElement se = SchemaElement.GetElement(elementName);
					if ((!secure) && se.hasField(se.getFullXMLName() + "/project")
							&& se.hasField(se.getFullXMLName() + "/sharing/share/project")) {
						found.setProperty("secure", Boolean.TRUE);
						found.setProperty("primary_security_fields.primary_security_field__0",
								se.getFullXMLName() + "/project");
						found.setProperty("primary_security_fields.primary_security_field__1",
								se.getFullXMLName() + "/sharing/share/project");
					}

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

					if (singular != null)
						found.setProperty("singular", singular);
					if (plural != null)
						found.setProperty("plural", plural);
					if (code != null)
						found.setProperty("code", code);

					int count = 0;

					setAction(found, count++, "edit", "Edit", "e.gif", "edit", null);

					setAction(found, count++, "xml", "View XML", "r.gif", null, null);

					setAction(found, count++, "xml_file", "Download XML", "save.gif", null, null);

					setAction(found, count++, "email_report", "Email", "right2.gif", null, "always");

				} catch (ElementNotFoundException e) {
					log.error("", e);
				} catch (FieldNotFoundException e) {
					log.error("", e);
				} catch (InvalidValueException e) {
					log.error("", e);
				}
			} else {
				return;
			}
		} catch (XFTInitException e) {
			log.error("", e);
			return;
		}
	}

	@Override
	public void deleteSavedSearchBySearchId(UserI user, String searchId,  XnatEventUtil event) throws SQLException {
		if (Objects.nonNull(searchId)) {

			XdatStoredSearchI search = (XdatStoredSearchI) XdatStoredSearch.getXdatStoredSearchsById(searchId, user, false);

			if (search != null) {
				XdatStoredSearchAllowedUserI mine = null;
				XdatStoredSearchGroupidI group = null;

				mine = getXdatStoredSearch(mine, search, user);

				group = getXdatStoredSearchGroupid(group, search, user);

				deleteStoredSearch(mine, search, group, user,event);
			}
		}
	}

	//pending impl for confirmation 
	private void getXnatStoredSearchData() {

	}

	private List<SearchElement> getXnatSearchElement(Map<String, ElementSecurity> elementSecurities, Map<String, Long> counts) throws XFTInitException, ElementNotFoundException, FieldNotFoundException {
		List<SearchElement> elementDtos = new ArrayList<SearchElement>();
		for (ElementSecurity es : elementSecurities.values()) {
			elementDtos.add(SearchElement.builder()
					.singular(Objects.nonNull(es.getSingularDescription()) ? es.getSingularDescription() : es.getElementName())
					.plural(Objects.nonNull(es.getPluralDescription()) ? es.getPluralDescription() : es.getElementName())
					.secured(Objects.nonNull(es.isSecure()) ? true : false).elementName(es.getElementName())
					.count(Objects.nonNull(counts.get(es.getElementName())) ? counts.get(es.getElementName()) : 0L)
					.build());
		}
		return elementDtos;
	}

	private Map<String, ElementSecurity> filterElementSecurityWithUsed(Map<String, ElementSecurity> elementSecurities, String used, Map<String, Long> counts) {
		if (used != null) {
			elementSecurities.keySet().removeAll(elementSecurities.entrySet().stream().filter(a -> {
				try {
					return !counts.containsKey(a.getValue().getElementName());
				} catch (XFTInitException | ElementNotFoundException | FieldNotFoundException e1) {
					e1.printStackTrace();
				}
				return false;
			}).map(e -> e.getKey()).collect(Collectors.toList()));
		}
		return elementSecurities;
	}

	private Map<String, ElementSecurity> filterElementSecurityWithSecured(
			Map<String, ElementSecurity> elementSecurities, String secured) {
		if (secured != null) {
			elementSecurities.keySet().removeAll(elementSecurities.entrySet().stream() .filter(a -> !a.getValue().isSecure()).map(e -> e.getKey()).collect(Collectors.toList()));
		}
		return elementSecurities;
	}

	private Map<String, ElementSecurity> filterElementSecurityWithXdat(Map<String, ElementSecurity> elementSecurities) {
		elementSecurities.keySet().removeAll(elementSecurities.entrySet().stream().filter(a -> {
			try {
				return a.getValue().getElementName().startsWith("xdat:");
			} catch (XFTInitException | ElementNotFoundException | FieldNotFoundException e1) {
				e1.printStackTrace();
			}
			return false;
		}).map(e -> e.getKey()).collect(Collectors.toList()));
		return elementSecurities;
	}

	private List<XnatSearchElement> getVersionElementData(Hashtable<String, org.nrg.xdat.display.DisplayVersion> versions,
														  List<XnatSearchElement> elementDtos) throws DisplayFieldNotFoundException {
		DisplayVersion displayVersion = getDisplayVersions(versions);
		elementDtos.add(XnatSearchElement.builder().displayVersion(displayVersion).build());
		return elementDtos;
	}

	private DisplayVersion getDisplayVersions(Hashtable<String, org.nrg.xdat.display.DisplayVersion> versions)
			throws DisplayFieldNotFoundException {
		List<Version> versionDtos = new ArrayList<>();
		for (Entry<String, org.nrg.xdat.display.DisplayVersion> entry : versions.entrySet()) {
			List<DisplayFieldReferenceI> fields = new ArrayList<>();
			for (org.nrg.xdat.display.DisplayFieldReferenceI field : entry.getValue().getAllFields()) {
			fields.add(DisplayFieldReferenceI.builder().id(field.getId())
						.elementName(Objects.isNull(field.getElementName()) || !field.getElementName().equals("") ? field.getElementName(): null)
						.value(Objects.isNull(field.getValue()) || !field.getValue().equals("") ? field.getValue() : null)
						.visible(field.isVisible() ? true : false)
						.type(Objects.isNull(field.getType()) || !field.getType().equals("") ? field.getType() : null)
						.header(Objects.isNull(field.getHeader()) || !field.getHeader().equals("") ? field.getHeader(): null)
						.build());
			}
			versionDtos.add(Version.builder().name(entry.getKey())
					.lightColor(Objects.isNull(entry.getValue().getLightColor()) || !entry.getValue().getLightColor().equals("") ? entry.getValue().getLightColor() : null)
					.darkColor(Objects.isNull(entry.getValue().getDarkColor()) || !entry.getValue().getDarkColor().equals("") ? entry.getValue().getDarkColor() : null)
					.defaultSortOrder(Objects.isNull(entry.getValue().getDefaultSortOrder()) || !entry.getValue().getDefaultSortOrder().equals("") ? entry.getValue().getDefaultSortOrder() : null)
					.orderBy(Objects.isNull(entry.getValue().getDefaultOrderBy()) || !entry.getValue().getDefaultOrderBy().equals("") ? entry.getValue().getDefaultOrderBy() : null)
					.fields(Objects.nonNull(fields) || !fields.isEmpty() ? fields : new ArrayList<>()).build());
		}
		return DisplayVersion.builder().versions(versionDtos).build();

	}

	private List<XnatSearchElement> getXnatSearchDataElements(SchemaElement se, UserI user,
															  List<XnatSearchElement> elementDtos, List<List> custom_fields, DisplayField pi)
			throws XFTInitException, ElementNotFoundException, FieldNotFoundException {
		if (GenericWrapperElement.GetFieldForXMLPath(se.getFullXMLName() + "/project") != null) {
			List<Object> av = Permissions.getAllowedValues(user, se.getFullXMLName(), se.getFullXMLName() + "/project", "read");
			for (Object o : av) {
				XnatSearchElement elementDto = new XnatSearchElement();
				elementDto = getElementDto(pi.getId() + "=" + o, o.toString(), "Label within the " + o + " project.", "string", false, "Label within the " + o + " project.", se.getFullXMLName(), 2);
				elementDtos.add(elementDto);
				for (List cf : custom_fields) {
					if (cf.get(0).equals(o)) {
						XnatSearchElement element = new XnatSearchElement();
						elementDto = getElementDto( se.getSQLName().toUpperCase() + "_FIELD_MAP=" + cf.get(1).toString().toLowerCase(), cf.get(1).toString(), "Custom Field: " + cf.get(1), cf.get(2).toString(), false, "Custom Field: " + cf.get(1), se.getFullXMLName(), 1);
						elementDtos.add(element);
					}
				}
			}
		}
		return elementDtos;
	}

	private List<XnatSearchElement> getXnatSearchElements(Iterator iter, List<XnatSearchElement> elementDtos,
														  SchemaElement se) {
		while (iter.hasNext()) {
			XnatSearchElement elementDto = new XnatSearchElement();
			DisplayField df = (DisplayField) iter.next();
			if (df.isSearchable()) {
				String desciption = (df.getDescription() == null)? (df.getHeader() == null) ? df.getId() : df.getHeader() : df.getDescription();
				boolean requiredValues = (df instanceof SQLQueryField) ? true : false;
				elementDto = getElementDto(df.getId(), df.getHeader(), df.getSummary(), df.getDataType(), requiredValues, desciption, se.getFullXMLName(), 0);
				elementDtos.add(elementDto);
			}
		}
		return elementDtos;
	}

	private XnatSearchElement getElementDto(String fieldId, String header, String summary, String dataType,
											boolean requiredValues, String desciption, String elementName, int src) {
		return XnatSearchElement.builder().fieldId(Objects.nonNull(fieldId) ? fieldId : "")
				.header(Objects.nonNull(header) ? header : "").summary(Objects.nonNull(summary) ? summary : "")
				.type(Objects.nonNull(dataType) ? dataType : "").requiresValue(requiredValues)
				.description(Objects.nonNull(desciption) ? desciption : "")
				.elementName(Objects.nonNull(elementName) ? elementName : "").src(src).build();
	}

	private String getSavedSearchQuery(UserI user, String usernameToGetListFor, String getAllBundles, String includeTagged) throws UserNotFoundException, UserInitException, DataFormatException {
		UserI userToGetListFor;
		boolean userIsAdmin = Groups.isSiteAdmin(user);
		if (userIsAdmin && !StringUtils.isBlank(usernameToGetListFor)) {
			userToGetListFor = new XDATUser(usernameToGetListFor);
		} else {
			userToGetListFor = user;
		}
		String query = "SELECT DISTINCT xssouter.*, array_agg(xssauouter.login) AS users FROM ( SELECT DISTINCT xss.id FROM xdat_stored_search xss LEFT JOIN xdat_stored_search_allowed_user xssau ON xss.id = xssau.xdat_stored_search_id LEFT JOIN xdat_stored_search_groupid xssag ON xss.id = xssag.allowed_groups_groupid_xdat_sto_id";
		if (!userIsAdmin || StringUtils.isBlank(getAllBundles) || !StringUtils.equalsIgnoreCase(getAllBundles, "true")) {
			query += " LEFT JOIN xdat_user_groupid ON xssag.groupid=xdat_user_groupid.groupid WHERE (xss.secure=0 OR xssau.login='" + userToGetListFor.getLogin() + "' OR groups_groupid_xdat_user_xdat_user_id=" + userToGetListFor.getID() + ")";
			if (includeTagged != null) {
				if (includeTagged.equals("true")) {
					query += " AND xss.tag IS NOT NULL";
				} else {
					if (!Permissions.getAllProjectIds(XDAT.getContextService().getBean(JdbcTemplate.class)).contains(includeTagged)) {
						log.error("", new Exception("Unknown tag: " + includeTagged));
						throw new DataFormatException("");
					}
					query += " AND xss.tag='" + includeTagged + "'";
				}
			} else {
				query += " AND xss.tag IS NULL";
			}
		}
		return query += ") AS ids LEFT JOIN xdat_stored_search xssouter ON xssouter.id = ids.id LEFT JOIN xdat_stored_search_allowed_user xssauouter ON xssouter.id = xssauouter.xdat_stored_search_id GROUP BY xssouter.id";
	}

	private static class XdatStoredSearchRowMapper implements RowMapper<XdatStoredSearchI> {
		XdatStoredSearchRowMapper(final UserI user) {
			_user = user;
		}

		@Override
		public XdatStoredSearchI mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			final String searchId = resultSet.getString("id");
			XdatStoredSearchI xdatStoredSearch = (XdatStoredSearchI) XdatSearch.getXdatStoredSearchsById(searchId, _user, false);
			return xdatStoredSearch;
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

	private XdatStoredSearchI getXssDataAfterValidate(XdatStoredSearchI xss, String sID, boolean loadedFromFile, UserI user, String project) {

		// allow loading of saved searches from xml stored on hte file system
		final File searchXml = getFileSystemSearch(sID);

		if (searchXml != null) {
			// if (mt.equals(MediaType.TEXT_XML) && (filepath == null ||
			// !filepath.startsWith("results")) && !this.hasQueryVariable("project")) {
			// return new FileRepresentation(searchXml, mt);
			// } else {
			SAXReader reader = new SAXReader(user);
			XFTItem item = null;
			try {
				item = reader.parse(searchXml);
			} catch (IOException | SAXException e) {
				e.printStackTrace();
			}
			xss = (XdatStoredSearchI) new XdatStoredSearch(item);

			loadedFromFile = true;

			if (project != null) {
				final XdatCriteriaSet cs =  new XdatCriteriaSet(user);
				cs.setMethod("OR");
				try {
					for (final String p : org.springframework.util.StringUtils.commaDelimitedListToSet(project)) {
						XdatCriteria c = new XdatCriteria(user);
						c.setSchemaField(xss.getRootElementName() + "/project");
						c.setComparisonType("=");
						c.setValue(p);
						cs.setCriteria(c);

						c = new XdatCriteria(user);
						c.setSchemaField(xss.getRootElementName() + "/sharing/share/project");
						c.setComparisonType("=");
						c.setValue(p);
						cs.setCriteria(c);
					}

					((XdatStoredSearch)xss).setSearchWhere((ItemI) cs);
				} catch (Exception e) {
				}
			}
		}
		return xss;
	}

	private void verifyXss(XdatStoredSearchI xss, UserI user) throws InsufficientPrivilegesException {
		if (!((XdatStoredSearch)xss).hasAllowedUser(user.getLogin()) && !Permissions.canQuery(user, xss.getRootElementName())) {
			throw new InsufficientPrivilegesException(user.getUsername());
		}

	}

	private XdatStoredSearchI getXssData(XdatStoredSearchI xss, String sID, UserI user, String dv) {
		if (xss == null && sID != null) {
			if (sID.startsWith("@")) {
				try {
					if (dv == null) {
						dv = "listing";
					}
					DisplaySearch ds = new DisplaySearch();
					ds.setUser(user);
					ds.setDisplay(dv);
					ds.setRootElement(sID.substring(1));
					xss = (XdatStoredSearchI) ds.convertToStoredSearch(sID);
					xss.setId(sID);
				} catch (XFTInitException | ElementNotFoundException e) {
					log.error("", e);
				}
			} else {
				xss = (XdatStoredSearchI) XdatStoredSearch.getXdatStoredSearchsById(sID, user, true);
			}
		}
		return xss;
	}

	private void deleteStoredSearch(XdatStoredSearchAllowedUserI mine, XdatStoredSearchI search,
			XdatStoredSearchGroupidI group, UserI user, XnatEventUtil event) throws SQLException {
		try {
		if (mine != null) {
			if (search.getAllowedUser().size() > 1 || search.getAllowedGroups_groupid().size() > 0) {
				SaveItemHelper.authorizedDelete(((ItemI)mine).getItem(), user, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, "Removed user from stored search",event));
			} else {
				SaveItemHelper.authorizedDelete(((ItemI)search).getItem(), user, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, "Removed stored search", event));
			}
		} else if (group != null) {
			if (search.getAllowedUser().size() > 0 || search.getAllowedGroups_groupid().size() > 1) {
				SaveItemHelper.authorizedDelete(((ItemI)group).getItem(), user, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, "Removed group from stored search", event));
			} else {
				SaveItemHelper.authorizedDelete(((ItemI)search).getItem(), user, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, "Removed stored search", event));
			}
		} else if (Roles.isSiteAdmin(user)) {
			SaveItemHelper.authorizedDelete(((ItemI)search).getItem(), user, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, "Removed stored search", event));
		} else {
			throw new InsufficientPrivilegesException(user.getUsername());
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private XdatStoredSearchGroupidI getXdatStoredSearchGroupid(XdatStoredSearchGroupidI group, XdatStoredSearchI search,
			UserI user) {
		for (XdatStoredSearchGroupidI ag : search.getAllowedGroups_groupid()) {
			if (Groups.isMember(user, ag.getGroupid())) {
				group = ag;
				break;
			}
		}
		return group;
	}

	private XdatStoredSearchAllowedUserI getXdatStoredSearch(XdatStoredSearchAllowedUserI mine, XdatStoredSearchI search,
			UserI user) {
		for (XdatStoredSearchAllowedUserI au : search.getAllowedUser()) {
			if (au.getLogin().equals(user.getLogin())) {
				mine = au;
				break;
			}
		}
		return mine;
	}

	private boolean getIsNew(XdatStoredSearchI xdatStoredSearch, boolean isNew, UserI user) throws Exception {
		final boolean isPrimary = (xdatStoredSearch.getTag() != null && (xdatStoredSearch.getId() .equals(xdatStoredSearch.getTag() + "_" + xdatStoredSearch.getRootElementName())))
				|| (org.apache.commons.lang3.StringUtils.isNotBlank(xdatStoredSearch.getBriefDescription()) && xdatStoredSearch.getBriefDescription().equals(DisplayManager.GetInstance()
								.getPluralDisplayNameForElement(xdatStoredSearch.getRootElementName())));

		if (isNew && isPrimary) {
			if (!Permissions.can(user, "xnat:projectData/ID", xdatStoredSearch.getTag(), SecurityManager.DELETE)) {
				isNew = false;
			}
		}
		return isNew;
	}

	private void verfiyPermission(UserI user, XdatStoredSearchI xdatStoredSearch)
			throws InsufficientPrivilegesException {
		if (!Permissions.canQuery(user, xdatStoredSearch.getRootElementName())) {
			throw new InsufficientPrivilegesException(user.getUsername());
		}
	}

	private boolean getFoundWithSearchGroup(XdatStoredSearchI xdatStoredSearch, boolean found, UserI user) {
		for (XdatStoredSearchGroupidI ag : xdatStoredSearch.getAllowedGroups_groupid()) {
			if (Groups.isMember(user, ag.getGroupid())) {
				found = true;
			}
		}
		return found;
	}

	private boolean getFoundWithUser(XdatStoredSearchI xdatStoredSearch, boolean found, UserI user) {
		for (XdatStoredSearchAllowedUserI au : xdatStoredSearch.getAllowedUser()) {
			if (au.getLogin().equals(user.getLogin())) {
				found = true;
			}
		}
		return found;
	}

	private XdatStoredSearchI getXdatStoredSearchWithSaveAs(XdatStoredSearchI xdatStoredSearch, Boolean saveAs) {
		if (saveAs) {
			while (xdatStoredSearch.getAllowedGroups_groupid().size() > 0) {
				((AutoXdatStoredSearch) xdatStoredSearch).removeAllowedGroups_groupid(0);
			}

			while (xdatStoredSearch.getAllowedUser().size() > 0) {
				((AutoXdatStoredSearch) xdatStoredSearch).removeAllowedUser(0);
			}
		}
		return xdatStoredSearch;
	}

	private XdatStoredSearchI getXdatStoredSearchWithNotIsNewAndNotFound(boolean isNew, boolean found,
																		XdatStoredSearchI xdatStoredSearch, UserI user) throws InsufficientPrivilegesException  {
		if (!found && !isNew) {
			if (xdatStoredSearch.getTag() != null && !xdatStoredSearch.getTag().equals("")) {
				try {
					if (!Permissions.canEdit(user, "xnat:projectData/ID", xdatStoredSearch.getTag())) {
						throw new InsufficientPrivilegesException(user.getUsername());
					} else {
						XdatStoredSearchAllowedUserI au = (XdatStoredSearchAllowedUserI) new XdatStoredSearchAllowedUser(user);
						au.setLogin(user.getLogin());
						((AutoXdatStoredSearch) xdatStoredSearch).setAllowedUser((ItemI) au);
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
			XdatStoredSearchAllowedUserI au = (XdatStoredSearchAllowedUserI) new XdatStoredSearchAllowedUser(user);
			au.setLogin(user.getLogin());
			((AutoXdatStoredSearch) xdatStoredSearch).setAllowedUser((ItemI) au);
		}
		return xdatStoredSearch;
	}

	

	private void setBooleanProperty(XFTItem found, String field, boolean _default) {
		try {
			if (_default && !isQueryVariableFalse(field)) {
				found.setProperty(field, Boolean.TRUE);
			} else if (!_default && !isQueryVariableTrue(field)) {
				found.setProperty(field, Boolean.FALSE);
			} else if (_default) {
				found.setProperty(field, Boolean.FALSE);
			} else
				found.setProperty(field, Boolean.TRUE);
		} catch (XFTInitException e) {
			log.error("", e);
		} catch (ElementNotFoundException e) {
			log.error("", e);
		} catch (FieldNotFoundException e) {
			log.error("", e);
		} catch (InvalidValueException e) {
			log.error("", e);
		}
	}

	private void setAction(XFTItem found, int count, String action_name, String display_name, String img,
			String secureAccess, String popup) {
		try {
			found.setProperty("xdat:element_security.element_actions.element_action__" + count + ".element_action_name",
					action_name);
			found.setProperty("xdat:element_security.element_actions.element_action__" + count + ".display_name",
					display_name);
			found.setProperty("xdat:element_security.element_actions.element_action__" + count + ".sequence",
					new Integer(count));
			if (img != null)
				found.setProperty("xdat:element_security.element_actions.element_action__" + count + ".image", img);
			if (secureAccess != null)
				found.setProperty("xdat:element_security.element_actions.element_action__" + count + ".secureAccess",
						secureAccess);
			if (popup != null)
				found.setProperty("xdat:element_security.element_actions.element_action__" + count + ".popup", popup);
		} catch (XFTInitException e) {
			log.error("", e);
		} catch (ElementNotFoundException e) {
			log.error("", e);
		} catch (FieldNotFoundException e) {
			log.error("", e);
		} catch (InvalidValueException e) {
			log.error("", e);
		}
	}

	private boolean isQueryVariableTrue(String field) {
		return false;
	}

	private boolean isQueryVariableFalse(String field) {
		return false;
	}

	

	private void deleteSavedSearch(XdatStoredSearchI search, UserI user, XdatStoredSearchAllowedUserI mine)
			throws JustificationAbsent, ActionNameAbsent {
		PersistentWorkflowI wrk = PersistentWorkflowUtils.getOrCreateWorkflowData(null, user, ((ItemI)search).getItem(),
				EventUtils.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, EventUtils.TYPE.WEB_SERVICE,
						"Deleted Project stored search"));
		try {
			if (search.getAllowedUser().size() > 1 || search.getAllowedGroups_groupid().size() > 0) {
				SaveItemHelper.authorizedDelete(((ItemI)mine).getItem(), user, wrk.buildEvent());
			} else {
				SaveItemHelper.authorizedDelete(((ItemI)search).getItem(), user, wrk.buildEvent());
			}
			PersistentWorkflowUtils.complete(wrk, wrk.buildEvent());
		} catch (Exception e) {
			try {
				PersistentWorkflowUtils.fail(wrk, wrk.buildEvent());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private XdatStoredSearchAllowedUserI verifyUserLogin(XdatStoredSearchI search, UserI user) {
		XdatStoredSearchAllowedUserI mine = null;
		for (XdatStoredSearchAllowedUserI au : search.getAllowedUser()) {
			if (au.getLogin().equals(user.getLogin())) {
				mine = au;
				break;
			}
		}
		return mine;

	}
	
	private final NamedParameterJdbcTemplate _template;
}
