package org.nrg.xnat.services.cache.extractors;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.services.cache.GroupsAndPermissionsCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

@CacheDefinition(value = "projectMembers", valueType = List.class)
@Slf4j
public class ProjectMembersExtractor extends AbstractGroupsAndPermissionsCacheDataExtractor<String, List<String>> {
    private static final String QUERY_PROJECT_USERS = "SELECT DISTINCT login " +
                                                      "FROM xdat_user u " +
                                                      "  LEFT JOIN xdat_user_groupid gid ON u.xdat_user_id = gid.groups_groupid_xdat_user_xdat_user_id " +
                                                      "  LEFT JOIN xdat_usergroup g ON gid.groupid = g.id " +
                                                      "  LEFT JOIN xdat_element_access xea ON g.xdat_usergroup_id = xea.xdat_usergroup_xdat_usergroup_id " +
                                                      "  LEFT JOIN xdat_field_mapping_set xfms ON xea.xdat_element_access_id = xfms.permissions_allow_set_xdat_elem_xdat_element_access_id " +
                                                      "  LEFT JOIN xdat_field_mapping xfm ON xfms.xdat_field_mapping_set_id = xfm.xdat_field_mapping_set_xdat_field_mapping_set_id " +
                                                      "WHERE tag = :" + PARAM_PROJECT_ID + " OR tag IS NULL AND field_value = '*'";

    @Autowired
    public ProjectMembersExtractor(final GroupsAndPermissionsCache cache, final NamedParameterJdbcTemplate template) {
        super(cache, template);
    }

    @Override
    public List<String> extract(final String projectId) {
        return getTemplate().queryForList(QUERY_PROJECT_USERS, new MapSqlParameterSource(PARAM_PROJECT_ID, projectId), String.class);
    }
}
