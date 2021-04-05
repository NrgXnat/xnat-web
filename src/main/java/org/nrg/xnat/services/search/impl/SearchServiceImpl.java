package org.nrg.xnat.services.search.impl;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XdatCriteria;
import org.nrg.xdat.om.XdatCriteriaSet;
import org.nrg.xdat.om.XdatSearch;
import org.nrg.xdat.om.XdatStoredSearch;
import org.nrg.xdat.om.XdatStoredSearchAllowedUser;
import org.nrg.xdat.om.XdatStoredSearchGroupid;
import org.nrg.xdat.search.DisplaySearch;
import org.nrg.xdat.security.XDATUser;
import org.nrg.xdat.security.helpers.Groups;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.XFT;
import org.nrg.xft.XFTItem;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.EventUtils.CATEGORY;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xft.schema.Wrappers.XMLWrapper.SAXReader;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.services.search.SearchService;
import org.restlet.data.Status;
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
	public List<XdatSearch> findAllSearchElements(UserI user) {
		return null;
	}

	@Override
	public XdatSearch findSearchByElement(UserI user, String element) {
		return null;
	}

	@Override
	public List<XdatStoredSearch> findAllSavedSearch(UserI user) throws UserNotFoundException, UserInitException {
		String query = getSavedSearchQuery(user);
		return _template.query(query, new MapSqlParameterSource(),new XdatStoredSearchRowMapper(user));
	}

	private String getSavedSearchQuery(UserI user) throws UserNotFoundException, UserInitException {
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
	                            //logger.error("", new Exception("Unknown tag: " + includeTagged));
	                           // getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
	                            return null;
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

	private final NamedParameterJdbcTemplate _template;

	
}
