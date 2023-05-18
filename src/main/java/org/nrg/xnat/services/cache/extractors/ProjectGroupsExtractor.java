package org.nrg.xnat.services.cache.extractors;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.services.cache.GroupsAndPermissionsCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

@CacheDefinition(value = "projectGroups", valueType = List.class)
@Slf4j
public class ProjectGroupsExtractor extends AbstractGroupsAndPermissionsCacheDataExtractor<String, List<String>> {
    private static final String QUERY_PROJECT_GROUPS = "SELECT id AS groups FROM xdat_usergroup WHERE tag = :" + PARAM_PROJECT_ID;

    @Autowired
    public ProjectGroupsExtractor(final GroupsAndPermissionsCache cache, final NamedParameterJdbcTemplate template) {
        super(cache, template);
    }

    @Override
    public List<String> extract(final String projectId) {
        return getTemplate().queryForList(QUERY_PROJECT_GROUPS, new MapSqlParameterSource(PARAM_PROJECT_ID, projectId), String.class);
    }
}
