package org.nrg.xnat.services.cache.extractors;

import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.exceptions.NrgServiceRuntimeException;
import org.nrg.xdat.security.UserGroup;
import org.nrg.xdat.services.cache.GroupsAndPermissionsCache;
import org.nrg.xft.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@CacheDefinition(value = "projectGroups", valueType = UserGroup.class)
@Slf4j
public class GroupsExtractor extends AbstractGroupsAndPermissionsCacheDataExtractor<String, UserGroup> {
    @Autowired
    public GroupsExtractor(final GroupsAndPermissionsCache cache, final NamedParameterJdbcTemplate template) {
        super(cache, template);
    }

    @Override
    public UserGroup extract(final String groupId) {
        try {
            return new UserGroup(groupId, getTemplate());
        } catch (ItemNotFoundException e) {
            throw new NrgServiceRuntimeException("An error occurred trying to get user group with ID " + groupId + ": it doesn't seem to exist", e);
        }
    }
}
