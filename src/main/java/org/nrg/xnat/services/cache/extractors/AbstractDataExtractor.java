package org.nrg.xnat.services.cache.extractors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xdat.services.cache.XnatCache;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter(AccessLevel.PROTECTED)
@Accessors(prefix = "_")
public abstract class AbstractDataExtractor<C extends XnatCache, P, T> implements DataExtractor<P, T> {
    private static final String QUERY_ACCESSIBLE_DATA_PROJECTS = "SELECT  " +
                                                                 "  project  " +
                                                                 "FROM  " +
                                                                 "  (SELECT DISTINCT f.field_value AS project  " +
                                                                 "   FROM  " +
                                                                 "     xdat_user u  " +
                                                                 "     LEFT JOIN xdat_user_groupid map ON u.xdat_user_id = map.groups_groupid_xdat_user_xdat_user_id  " +
                                                                 "     LEFT JOIN xdat_usergroup g ON map.groupid = g.id  " +
                                                                 "     LEFT JOIN xdat_element_access a ON (g.xdat_usergroup_id = a.xdat_usergroup_xdat_usergroup_id OR u.xdat_user_id = a.xdat_user_xdat_user_id)  " +
                                                                 "     LEFT JOIN xdat_field_mapping_set s ON a.xdat_element_access_id = s.permissions_allow_set_xdat_elem_xdat_element_access_id  " +
                                                                 "     LEFT JOIN xdat_field_mapping f ON s.xdat_field_mapping_set_id = f.xdat_field_mapping_set_xdat_field_mapping_set_id  " +
                                                                 "   WHERE  " +
                                                                 "     f.field_value != '*' AND  " +
                                                                 "     a.element_name = 'xnat:subjectData' AND  " +
                                                                 "     f.%s = 1 AND  " +
                                                                 "     u.login IN ('guest', :" + PARAM_USERNAME + ")) projects";
    private static final String QUERY_OWNED_PROJECTS           = String.format(QUERY_ACCESSIBLE_DATA_PROJECTS, "delete_element");
    private static final String QUERY_EDITABLE_PROJECTS        = String.format(QUERY_ACCESSIBLE_DATA_PROJECTS, "edit_element");
    private static final String QUERY_READABLE_PROJECTS        = String.format(QUERY_ACCESSIBLE_DATA_PROJECTS, "read_element");
    private static final String QUERY_HAS_ALL_DATA_PRIVILEGES  = "SELECT  " +
                                                                 "  EXISTS(SELECT TRUE  " +
                                                                 "         FROM  " +
                                                                 "           xdat_user u  " +
                                                                 "           LEFT JOIN xdat_user_groupid map ON u.xdat_user_id = map.groups_groupid_xdat_user_xdat_user_id  " +
                                                                 "           LEFT JOIN xdat_usergroup ug ON map.groupid = ug.id  " +
                                                                 "           LEFT JOIN xdat_element_access ea ON (ug.xdat_usergroup_id = ea.xdat_usergroup_xdat_usergroup_id OR u.xdat_user_id = ea.xdat_user_xdat_user_id)  " +
                                                                 "           LEFT JOIN xdat_field_mapping_set fms ON ea.xdat_element_access_id = fms.permissions_allow_set_xdat_elem_xdat_element_access_id  " +
                                                                 "           LEFT JOIN xdat_field_mapping fm ON fms.xdat_field_mapping_set_id = fm.xdat_field_mapping_set_xdat_field_mapping_set_id  " +
                                                                 "         WHERE  " +
                                                                 "           fm.field_value = '*' AND  " +
                                                                 "           ea.element_name = 'xnat:projectData' AND  " +
                                                                 "           fm.%s = 1 AND  " +
                                                                 "           u.login = :" + PARAM_USERNAME + ")";
    private static final String QUERY_HAS_ALL_DATA_ACCESS      = String.format(QUERY_HAS_ALL_DATA_PRIVILEGES, "read_element");
    private static final String QUERY_HAS_ALL_DATA_ADMIN       = String.format(QUERY_HAS_ALL_DATA_PRIVILEGES, "edit_element");
    private static final String QUERY_ALL_DATA_ACCESS_PROJECTS = "SELECT id AS project FROM xnat_projectdata ORDER BY project";

    private final C                          _cache;
    private final NamedParameterJdbcTemplate _template;
    private final Map<String, Boolean>       _userChecks;

    protected AbstractDataExtractor(final C cache, final NamedParameterJdbcTemplate template) {
        _cache      = cache;
        _template   = template;
        _userChecks = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String getCacheGroup();

    /**
     * Checks whether the user exists. If not, this throws the {@link UserNotFoundException}. Otherwise, it returns
     * a parameter source containing the username that can be used in subsequent queries.
     *
     * @param username The user to test.
     *
     * @return A parameter source containing the username parameter.
     *
     * @throws UserNotFoundException If the user doesn't exist.
     */
    protected MapSqlParameterSource checkUser(final String username) throws UserNotFoundException {
        final MapSqlParameterSource parameters = new MapSqlParameterSource(PARAM_USERNAME, username);

        // If the user isn't in the check map OR the user is in the check map but is set as not existing...
        if (!_userChecks.containsKey(username) || !_userChecks.get(username)) {
            // See if the user exists now. The non-existent user existing should be updated with the add user event,
            // but we don't have a clearly defined handler for that yet.
            _userChecks.put(username, _template.queryForObject(UserManagementServiceI.QUERY_CHECK_USER_EXISTS, parameters, Boolean.class));
        }
        if (!_userChecks.get(username)) {
            throw new UserNotFoundException(username);
        }
        return parameters;
    }

    /**
     * Retrieves a list of projects where the specified user has read access. This differs from {@link #getUserProjects(String)} in that it
     * includes protected and public projects and projects to which the user has read access due to all data access privileges.
     *
     * @param username The username to retrieve projects for.
     *
     * @return A list of projects to which the specified user has read access.
     */
    protected List<String> getUserReadableProjects(final String username) {
        return getProjectsByAccessQuery(username, QUERY_READABLE_PROJECTS, false);
    }

    /**
     * Retrieves a list of projects where the specified user has edit access.
     *
     * @param username The username to retrieve projects for.
     *
     * @return A list of projects to which the specified user has edit access.
     */
    protected List<String> getUserEditableProjects(final String username) {
        return getProjectsByAccessQuery(username, QUERY_EDITABLE_PROJECTS, true);
    }

    /**
     * Retrieves a list of projects where the specified user is an owner (i.e. has delete access).
     *
     * @param username The username to retrieve projects for.
     *
     * @return A list of projects to which the specified user has delete access.
     */
    protected List<String> getUserOwnedProjects(final String username) {
        return getProjectsByAccessQuery(username, QUERY_OWNED_PROJECTS, true);
    }

    private List<String> getProjectsByAccessQuery(final String username, final String query, final boolean requireAdminAccess) {
        final MapSqlParameterSource parameters = new MapSqlParameterSource("username", username);
        return hasAllDataAdmin(username) || (hasAllDataAccess(username) && !requireAdminAccess)
               ? _template.queryForList(QUERY_ALL_DATA_ACCESS_PROJECTS, parameters, String.class)
               : _template.queryForList(query, parameters, String.class);
    }

    /**
     * Cheap test to see if the user is in a group that has data read access on all projects.
     *
     * @param username The username to be checked.
     *
     * @return Returns true if the user has all-data-access, false otherwise.
     */
    private boolean hasAllDataAccess(final String username) {
        return _template.queryForObject(QUERY_HAS_ALL_DATA_ACCESS, new MapSqlParameterSource(PARAM_USERNAME, username), Boolean.class);
    }

    /**
     * Cheap test to see if the user is in a group that has data admin access on all projects.
     *
     * @param username The username to be checked.
     *
     * @return Returns true if the user has all-data-admin, false otherwise.
     */
    private boolean hasAllDataAdmin(final String username) {
        return _template.queryForObject(QUERY_HAS_ALL_DATA_ADMIN, new MapSqlParameterSource(PARAM_USERNAME, username), Boolean.class);
    }
}
