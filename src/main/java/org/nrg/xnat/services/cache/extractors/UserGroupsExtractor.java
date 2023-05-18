package org.nrg.xnat.services.cache.extractors;

import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.exceptions.NrgServiceRuntimeException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xdat.services.cache.GroupsAndPermissionsCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

@CacheDefinition(value = "projectGroups", valueType = List.class)
@Slf4j
public class UserGroupsExtractor extends AbstractGroupsAndPermissionsCacheDataExtractor<String, List<String>> {
    private static final String QUERY_GET_GROUPS_FOR_USER = "SELECT " +
                                                            "    g.groupid " +
                                                            "FROM " +
                                                            "    xdat_user_groupid g " +
                                                            "    LEFT JOIN xdat_user u ON g.groups_groupid_xdat_user_xdat_user_id = u.xdat_user_id " +
                                                            "WHERE " +
                                                            "    g.groupid IS NOT NULL AND " +
                                                            "    u.login = :username " +
                                                            "ORDER BY " +
                                                            "    g.groupid";

    @Autowired
    public UserGroupsExtractor(final GroupsAndPermissionsCache cache, final NamedParameterJdbcTemplate template) {
        super(cache, template);
    }

    @Override
    public List<String> extract(final String username) {
        log.info("Initializing user group IDs cache entry for user '{}'", username);
        final List<String> groupIds;
        try {
            groupIds = getTemplate().queryForList(QUERY_GET_GROUPS_FOR_USER, checkUser(username), String.class);
        } catch (UserNotFoundException e) {
            throw new NrgServiceRuntimeException("The user " + username + " does not exist", e);
        }
        log.debug("Found {} user group IDs cache entry for user '{}'", groupIds.size(), username);
        return groupIds;
    }
}
