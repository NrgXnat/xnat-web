package org.nrg.xnat.services.search.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XdatSearch;
import org.nrg.xdat.om.XdatStoredSearch;
import org.nrg.xdat.security.XDATUser;
import org.nrg.xdat.security.helpers.Groups;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

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
	public List<XdatSearch> findSavedSearchBySearchId(UserI user, String searchId) {
		return null;
	}
	
	private final NamedParameterJdbcTemplate _template;

}
