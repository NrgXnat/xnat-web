package org.nrg.xnat.services.cache.extractors;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.services.cache.GroupsAndPermissionsCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

import static org.nrg.xnat.services.cache.DefaultGroupsAndPermissionsCache.CACHE_USER_LAST_UPDATED;

@Component
@Slf4j
public class UserLastUpdateExtractor extends AbstractGroupsAndPermissionsCacheDataExtractor<String, Date> {
    @Autowired
    public UserLastUpdateExtractor(final GroupsAndPermissionsCache cache, final NamedParameterJdbcTemplate template) {
        super(cache, CACHE_USER_LAST_UPDATED, template);
    }

    @Override
    public Date extract(final Object... parameters) {
        return new Date();
    }
}
