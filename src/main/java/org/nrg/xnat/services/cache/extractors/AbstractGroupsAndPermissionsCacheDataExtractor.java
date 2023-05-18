package org.nrg.xnat.services.cache.extractors;

import org.nrg.xdat.services.cache.GroupsAndPermissionsCache;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public abstract class AbstractGroupsAndPermissionsCacheDataExtractor<P, T> extends AbstractDataExtractor<GroupsAndPermissionsCache, P, T> {
    protected AbstractGroupsAndPermissionsCacheDataExtractor(final GroupsAndPermissionsCache cache, final NamedParameterJdbcTemplate template) {
        super(cache, template);
    }

    public String getCacheGroup() {
        return GroupsAndPermissionsCache.CACHE_NAME;
    }
}
