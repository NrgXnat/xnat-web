package org.nrg.xnat.services.cache.extractors;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.security.UserGroup;
import org.nrg.xdat.services.cache.GroupsAndPermissionsCache;
import org.nrg.xft.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.nrg.xnat.services.cache.DefaultGroupsAndPermissionsCache.CACHE_GROUPS;

@Component
@Slf4j
public class GroupsExtractor extends AbstractGroupsAndPermissionsCacheDataExtractor<String, UserGroup> {
    private static final String QUERY_ALL_GROUPS = "SELECT id FROM xdat_usergroup";

    @Autowired
    public GroupsExtractor(final @Lazy GroupsAndPermissionsCache cache, final NamedParameterJdbcTemplate template) {
        super(cache, CACHE_GROUPS, template);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserGroup extract(final Object... parameters) {
        if (parameters.length == 0) {
            return null;
        }

        final String groupId = (String) parameters[0];
        log.debug("Extracting group with ID {}", groupId);

        try {
            return new UserGroup(groupId, getTemplate());
        } catch (ItemNotFoundException e) {
            log.info("Asked to get user group {}, but it doesn't seem to exist, returning null", groupId);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getKeys() {
        return getTemplate().queryForList(QUERY_ALL_GROUPS, EmptySqlParameterSource.INSTANCE, String.class);
    }
}
