package org.nrg.xnat.services.search.impl;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.display.DisplayManager;
import org.nrg.xdat.om.XdatCriteria;
import org.nrg.xdat.om.XdatCriteriaSet;
import org.nrg.xdat.om.XdatSearch;
import org.nrg.xdat.om.XdatStoredSearch;
import org.nrg.xdat.om.XdatStoredSearchAllowedUser;
import org.nrg.xdat.om.XdatStoredSearchGroupid;
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
import org.nrg.xft.XFT;
import org.nrg.xft.XFTItem;
import org.nrg.xft.collections.ItemCollection;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.FieldNotFoundException;
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xft.schema.Wrappers.XMLWrapper.SAXReader;
import org.nrg.xft.search.ItemSearch;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.services.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;



import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class SearchServiceImpl implements SearchService{
	
	@Autowired
	public SearchServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}

	@Override
	public List<XdatSearch> findAllSearch(UserI user) {
		return XdatSearch.getAllXdatSearchs(user, false);
	}

	@Override
	public List<XdatSearch> findAllSearchElements(UserI user, String secured, String readable, String used) throws Exception  {
		final Map<String, ElementSecurity> allES    = new HashMap<>(ElementSecurity.GetElementSecurities());
		allES.keySet().removeAll(
				allES.entrySet().stream().filter(a->{
					try {
						return a.getValue().getElementName().startsWith("xdat:");
					} catch (XFTInitException | ElementNotFoundException | FieldNotFoundException e1) {
						e1.printStackTrace();
					}
					return false;
				}).map(e -> e.getKey()).collect(Collectors.toList()));
		
		if (secured != null) {
			allES.keySet().removeAll(allES.entrySet().stream().filter(a -> !a.getValue().isSecure())
					.map(e -> e.getKey()).collect(Collectors.toList()));
		}
		
		final Map<String, Long> counts = readable != null ? UserHelper.getUserHelperService(user).getReadableCounts() : XDAT.getTotalCounts();
		
		if (used != null) {
			allES.keySet().removeAll(allES.entrySet().stream().filter(a -> {
				try {
					return !counts.containsKey(a.getValue().getElementName());
				} catch (XFTInitException | ElementNotFoundException | FieldNotFoundException e1) {
					e1.printStackTrace();
				}
				return false;
			}).map(e -> e.getKey()).collect(Collectors.toList()));
		}
		
		allES.entrySet().forEach(t->{
			try {
				log.debug("allES filter Values "+t.getValue().getElementName());
			} catch (XFTInitException | ElementNotFoundException | FieldNotFoundException e1) {
				e1.printStackTrace();
			}
		});
		return null;
	}

	@Override
	public XdatSearch findSearchByElement(UserI user, String element) {
		return null;
	}

	@Override
	public List<XdatStoredSearch> findAllSavedSearch(UserI user) throws UserNotFoundException, UserInitException, DataFormatException {
		String query = getSavedSearchQuery(user);
		return _template.query(query, new MapSqlParameterSource(),new XdatStoredSearchRowMapper(user));
	}

	private String getSavedSearchQuery(UserI user) throws UserNotFoundException, UserInitException, DataFormatException {
		  String    usernameToGetListFor = getQueryVariable("user");
	        String    getAllBundles = getQueryVariable("all");
	        UserI     userToGetListFor;
	            boolean userIsAdmin = Groups.isSiteAdmin(user);
	            if (userIsAdmin && !StringUtils.isBlank(usernameToGetListFor)) {
	                userToGetListFor = new XDATUser(usernameToGetListFor);
	            } else {
	                userToGetListFor = user;
	            }
	            String query         = "SELECT DISTINCT xssouter.*, array_agg(xssauouter.login) AS users FROM ( SELECT DISTINCT xss.id FROM xdat_stored_search xss LEFT JOIN xdat_stored_search_allowed_user xssau ON xss.id = xssau.xdat_stored_search_id LEFT JOIN xdat_stored_search_groupid xssag ON xss.id = xssag.allowed_groups_groupid_xdat_sto_id";
	            if(!userIsAdmin || StringUtils.isBlank(getAllBundles) || !StringUtils.equalsIgnoreCase(getAllBundles,"true")){
	                query+=" LEFT JOIN xdat_user_groupid ON xssag.groupid=xdat_user_groupid.groupid WHERE (xss.secure=0 OR xssau.login='" + userToGetListFor.getLogin() + "' OR groups_groupid_xdat_user_xdat_user_id=" + userToGetListFor.getID() + ")";
	                String includeTagged = getQueryVariable("includeTag");
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
	            return query+=") AS ids LEFT JOIN xdat_stored_search xssouter ON xssouter.id = ids.id LEFT JOIN xdat_stored_search_allowed_user xssauouter ON xssouter.id = xssauouter.xdat_stored_search_id GROUP BY xssouter.id";
	}

	
	private static class XdatStoredSearchRowMapper implements RowMapper<XdatStoredSearch> {
		XdatStoredSearchRowMapper(final UserI user) {
	        _user = user;
	    }
	    @Override
	    public XdatStoredSearch mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
	        final String  searchId = resultSet.getString("id");
	        XdatStoredSearch xdatStoredSearch= XdatSearch.getXdatStoredSearchsById(searchId, _user, false);
	        return xdatStoredSearch;
	    }
	    private final UserI _user;
	}
	
	private String getQueryVariable(String string) {
		return null;
	}

	@Override
	public XdatStoredSearch findSavedSearchBySearchId(UserI user, String searchId) throws Exception {
		 XdatStoredSearch xss            = null;
		 String           sID            = searchId;
		 boolean          loadedFromFile = false;
		 
		 xss = getXssData(xss , sID, user);
		 if (xss != null) 
			 verifyXss(xss, user);
		 else xss = getXssDataAfterValidate(xss, sID, loadedFromFile, user );
		 
		 if (xss != null) {
			 getXnatStoredSearchData();
		 }
		 
		return xss;
	}
	
	private void getXnatStoredSearchData() {
		
	}

	/**
     * Returns a file containing search xmls which was stored on the file system.  This provides a way to standardize search xmls outside of the database, for easy sharing across installations.
     *
     * @return The search XMLs stored on the file system.
     */
    private synchronized static File getFileSystemSearch(String name) {
        if (!name.contains("..")) {
            final File file = new File(new File(XFT.GetConfDir()).getParentFile().getParentFile(), "resources/searches/" + name);
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

	
	private XdatStoredSearch getXssDataAfterValidate(XdatStoredSearch xss, String sID, boolean loadedFromFile, UserI user) throws Exception {

        //allow loading of saved searches from xml stored on hte file system
        final File searchXml = getFileSystemSearch(sID);

        if (searchXml != null) {
           // if (mt.equals(MediaType.TEXT_XML) && (filepath == null || !filepath.startsWith("results")) && !this.hasQueryVariable("project")) {
              //  return new FileRepresentation(searchXml, mt);
           // } else {
                    SAXReader reader = new SAXReader(user);
                    XFTItem item = reader.parse(searchXml);
                    xss = new XdatStoredSearch(item);

                    loadedFromFile = true;

                    if (this.getQueryVariable("project") != null) {
                        final XdatCriteriaSet cs = new XdatCriteriaSet(user);
                        cs.setMethod("OR");

                        for (final String p :  org.springframework.util.StringUtils.commaDelimitedListToSet(getQueryVariable("project"))) {
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

                        xss.setSearchWhere(cs);
                    }
                }
           // }
		return xss;
	}

	private void verifyXss(XdatStoredSearch xss, UserI user) throws InsufficientPrivilegesException {
		if (!xss.hasAllowedUser(user.getLogin()) && !Permissions.canQuery(user, xss.getRootElementName())) {
           throw new InsufficientPrivilegesException(user.getUsername());
        }
		
	}

	private XdatStoredSearch getXssData(XdatStoredSearch xss, String sID, UserI user) {
		if (xss == null && sID != null) {
            if (sID.startsWith("@")) {
                try {
                    String dv = this.getQueryVariable("dv");
                    if (dv == null) {
                        dv = "listing";
                    }
                    DisplaySearch ds = new DisplaySearch();
                    ds.setUser(user);
                    ds.setDisplay(dv);
                    ds.setRootElement(sID.substring(1));
                    xss = ds.convertToStoredSearch(sID);
                    xss.setId(sID);
                } catch (XFTInitException | ElementNotFoundException e) {
                    log.error("", e);
                }
            } else {
                xss = XdatStoredSearch.getXdatStoredSearchsById(sID, user, true);
            }
        }
		return xss;
	}
	
	@Override
	public void deleteSavedSearchBySearchId(UserI user, String searchId) throws SQLException, Exception {
		if (Objects.nonNull(searchId)) {

			XdatStoredSearch search = XdatStoredSearch.getXdatStoredSearchsById(searchId, user, false);

			if (search != null) {
				XdatStoredSearchAllowedUser mine = null;
				XdatStoredSearchGroupid group = null;

				mine = getXdatStoredSearch(mine, search, user);

				group = getXdatStoredSearchGroupid(group, search, user);

				deleteStoredSearch(mine, search, group, user);
			}
		}
	}

	private void deleteStoredSearch(XdatStoredSearchAllowedUser mine, XdatStoredSearch search, XdatStoredSearchGroupid group, UserI user) throws SQLException, Exception {
		if (mine != null) {
            if (search.getAllowedUser().size() > 1 || search.getAllowedGroups_groupid().size() > 0) {
                SaveItemHelper.authorizedDelete(mine.getItem(), user, newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, "Removed user from stored search"));
            } else {
                SaveItemHelper.authorizedDelete(search.getItem(), user, newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, "Removed stored search"));
            }
        } else if (group != null) {
            if (search.getAllowedUser().size() > 0 || search.getAllowedGroups_groupid().size() > 1) {
                SaveItemHelper.authorizedDelete(group.getItem(), user, newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, "Removed group from stored search"));
            } else {
                SaveItemHelper.authorizedDelete(search.getItem(), user, newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, "Removed stored search"));
            }
        } else if (Roles.isSiteAdmin(user)) {
            SaveItemHelper.authorizedDelete(search.getItem(), user, newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, "Removed stored search"));
        } else {
        	throw new InsufficientPrivilegesException(user.getUsername());
        }
	}

	private XdatStoredSearchGroupid getXdatStoredSearchGroupid(XdatStoredSearchGroupid group, XdatStoredSearch search, UserI user) {
		 for (XdatStoredSearchGroupid ag : search.getAllowedGroups_groupid()) {
             if (Groups.isMember(user, ag.getGroupid())) {
                 group = ag;
                 break;
             }
         }
		return group;
	}

	private XdatStoredSearchAllowedUser getXdatStoredSearch(XdatStoredSearchAllowedUser mine, XdatStoredSearch search, UserI user) {
		 for (XdatStoredSearchAllowedUser au : search.getAllowedUser()) {
             if (au.getLogin().equals(user.getLogin())) {
                 mine = au;
                 break;
             }
         }
		return mine;
	}

	private EventDetails newEventInstance(EventUtils.CATEGORY cat, String action) {
		 return EventUtils.newEventInstance(cat, getEventType(), (getAction() != null) ? getAction() : action, getReason(), getComment());
	}
	
	public EventUtils.TYPE getEventType() {
		final String id = getQueryVariable(EventUtils.EVENT_TYPE);
		if (id != null) {
			return EventUtils.getType(id, EventUtils.TYPE.WEB_SERVICE);
		} else {
			return EventUtils.TYPE.WEB_SERVICE;
		}
	}

	public String getAction() {
		return getQueryVariable(EventUtils.EVENT_ACTION);
	}

	public String getReason() {
		return getQueryVariable(EventUtils.EVENT_REASON);
	}

	public String getComment() {
		return getQueryVariable(EventUtils.EVENT_COMMENT);
	}

	private final NamedParameterJdbcTemplate _template;

	@Override
	public XdatStoredSearch updateStoredSearch(UserI user,XdatStoredSearch xdatStoredSearch, String searchId, Boolean saveAs) throws Exception {
		 boolean isNew = false;

         if (xdatStoredSearch.getId() == null || !xdatStoredSearch.getId().equals(searchId)) {
        	 xdatStoredSearch.setId(searchId);
             isNew = true;
         } else {
             XFTItem xss = xdatStoredSearch.getCurrentDBVersion(false);
             if (xss == null) {
                 isNew = true;
             } else if (saveAs) {
                 while (xss != null) {
                	 xdatStoredSearch.setId(xdatStoredSearch.getId() + "_1");
                     xss = xdatStoredSearch.getCurrentDBVersion(false);
                 }
                 isNew = true;
             }
         }
         
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

         xdatStoredSearch= getXdatStoredSearchWithSaveAs(xdatStoredSearch, saveAs);
         
         boolean found = false;
         
         found = getFoundWithUser(xdatStoredSearch, found, user);
         
         found = getFoundWithSearchGroup(xdatStoredSearch, found, user);

         xdatStoredSearch= getXdatStoredSearchWithNotIsNewAndNotFound(isNew, found, xdatStoredSearch, user);

         xdatStoredSearch= getXdatStoredSearchWithIsNewAndNotFound(isNew, found, xdatStoredSearch, user);
         
         try {
             SaveItemHelper.unauthorizedSave(xdatStoredSearch, user, false, true, this.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, (isNew) ? "Creating new stored search" : "Modified existing stored search"));
         } catch (Exception e) {
             log.error("", e);
           throw new InitializationException("Something went worng");
         }
		return XdatStoredSearch.getXdatStoredSearchsById(searchId, user, false);
	}

	private boolean getIsNew(XdatStoredSearch xdatStoredSearch, boolean isNew, UserI user) throws Exception {
		final boolean isPrimary = (xdatStoredSearch.getTag() != null && (xdatStoredSearch.getId().equals(xdatStoredSearch.getTag() + "_" + xdatStoredSearch.getRootElementName()))) ||
                (org.apache.commons.lang3.StringUtils.isNotBlank(xdatStoredSearch.getBriefDescription()) && xdatStoredSearch.getBriefDescription().equals(DisplayManager.GetInstance().getPluralDisplayNameForElement(xdatStoredSearch.getRootElementName())));

		if (isNew && isPrimary) {
			if (!Permissions.can(user, "xnat:projectData/ID", xdatStoredSearch.getTag(), SecurityManager.DELETE)) {
				isNew = false;
			}
		}
		return isNew;
	}

	private void verfiyPermission(UserI user, XdatStoredSearch xdatStoredSearch) throws InsufficientPrivilegesException {
		if (!Permissions.canQuery(user, xdatStoredSearch.getRootElementName())) {
			throw new InsufficientPrivilegesException(user.getUsername());
		}
	}

	private boolean getFoundWithSearchGroup(XdatStoredSearch xdatStoredSearch, boolean found, UserI user) {
		for (XdatStoredSearchGroupid ag : xdatStoredSearch.getAllowedGroups_groupid()) {
            if (Groups.isMember(user, ag.getGroupid())) {
                found = true;
            }
        }
		return found;
	}

	private boolean getFoundWithUser(XdatStoredSearch xdatStoredSearch, boolean found, UserI user) {
		for (XdatStoredSearchAllowedUser au : xdatStoredSearch.getAllowedUser()) {
            if (au.getLogin().equals(user.getLogin())) {
                found = true;
            }
        }
		return found;
	}

	private XdatStoredSearch getXdatStoredSearchWithSaveAs(XdatStoredSearch xdatStoredSearch, Boolean saveAs) {
		if (saveAs) {
            while (xdatStoredSearch.getAllowedGroups_groupid().size() > 0) {
           	 xdatStoredSearch.removeAllowedGroups_groupid(0);
            }

            while (xdatStoredSearch.getAllowedUser().size() > 0) {
           	 xdatStoredSearch.removeAllowedUser(0);
            }
        }
		return xdatStoredSearch;
	}

	private XdatStoredSearch getXdatStoredSearchWithNotIsNewAndNotFound(boolean isNew, boolean found, XdatStoredSearch xdatStoredSearch, UserI user) throws Exception {
		 if (!found && !isNew) {
             if (xdatStoredSearch.getTag() != null && !xdatStoredSearch.getTag().equals("")) {
                 if (!Permissions.canEdit(user, "xnat:projectData/ID", xdatStoredSearch.getTag())) {
                	 throw new InsufficientPrivilegesException(user.getUsername());
                 } else {
                     XdatStoredSearchAllowedUser au = new XdatStoredSearchAllowedUser(user);
                     au.setLogin(user.getLogin());
                     xdatStoredSearch.setAllowedUser(au);
                 }
             } else {
            	 throw new InsufficientPrivilegesException(user.getUsername());
             }
         }
		return xdatStoredSearch;
	}

	private XdatStoredSearch getXdatStoredSearchWithIsNewAndNotFound(boolean isNew, boolean found, XdatStoredSearch xdatStoredSearch, UserI user) throws Exception {
		if (isNew && !found) {
            XdatStoredSearchAllowedUser au = new XdatStoredSearchAllowedUser(user);
            au.setLogin(user.getLogin());
            xdatStoredSearch.setAllowedUser(au);
        }
		return xdatStoredSearch;
	}

}
