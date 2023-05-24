package org.nrg.xnat.services.cache.extractors;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.om.WrkWorkflowdata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.services.cache.GroupsAndPermissionsCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.nrg.xnat.services.cache.DefaultGroupsAndPermissionsCache.CACHE_READABLE_COUNTS;

@Component
@Slf4j
public class ReadableCountsExtractor extends AbstractGroupsAndPermissionsCacheDataExtractor<String, Map<String, Long>> {
    private static final String QUERY_USER_READABLE_WORKFLOW_COUNT   = "SELECT reltuples::bigint AS COUNT FROM pg_class WHERE oid = 'public.wrk_workflowdata'::regclass";
    private static final String QUERY_USER_READABLE_SUBJECT_COUNT    = "SELECT SUM(subjs.COUNT) AS ELEMENT_COUNT "
                                                                       + "FROM xdat_element_access xea  "
                                                                       + "LEFT JOIN xdat_usergroup grp ON xea.xdat_usergroup_xdat_usergroup_id=grp.xdat_usergroup_id "
                                                                       + "LEFT JOIN xdat_user_groupid gid ON grp.id=gid.groupid "
                                                                       + "LEFT JOIN xdat_field_mapping_set fms ON xea.xdat_element_access_id=fms.permissions_allow_set_xdat_elem_xdat_element_access_id "
                                                                       + "LEFT JOIN xdat_field_mapping xfm ON fms.xdat_field_mapping_set_id=xfm.xdat_field_mapping_set_xdat_field_mapping_set_id AND xfm.read_element=1 AND 'xnat:subjectData/project'=xfm.field "
                                                                       + "JOIN ( "
                                                                       + "\tSELECT project, COUNT(id) FROM xnat_subjectData GROUP BY project UNION SELECT project, COUNT(subject_id) FROM xnat_projectParticipant GROUP BY project "
                                                                       + ") subjs ON xfm.field_value=subjs.project "
                                                                       + "WHERE gid.groups_groupid_xdat_user_xdat_user_id IN (:" + PARAM_USER_IDS + ") OR xea.xdat_user_xdat_user_id IN (:" + PARAM_USER_IDS + ")";
    private static final String QUERY_USER_READABLE_EXPERIMENT_COUNT = "SELECT xea.element_name, SUM(expts.SUM) AS ELEMENT_COUNT "
                                                                       + "FROM xdat_user_groupid gid "
                                                                       + "LEFT JOIN xdat_usergroup grp ON gid.groupid=grp.id "
                                                                       + "LEFT JOIN xdat_element_access xea ON grp.xdat_usergroup_id=xea.xdat_usergroup_xdat_usergroup_id "
                                                                       + "LEFT JOIN xdat_field_mapping_set fms ON xea.xdat_element_access_id=fms.permissions_allow_set_xdat_elem_xdat_element_access_id "
                                                                       + "LEFT JOIN xdat_field_mapping xfm ON fms.xdat_field_mapping_set_id=xfm.xdat_field_mapping_set_xdat_field_mapping_set_id AND xfm.read_element=1 AND xea.element_name || '/project'=xfm.field "
                                                                       + "JOIN ( "
                                                                       + "\tSELECT project, element_name, SUM(COUNT) FROM ( "
                                                                       + "\t\tSELECT project, element_name, COUNT(id) FROM xnat_experimentData LEFT JOIN xdat_meta_element xme ON xnat_experimentData.extension=xme.xdat_meta_element_id GROUP BY project, element_name "
                                                                       + "  \t\tUNION  "
                                                                       + "  \t\tSELECT shr.project, element_name, COUNT(expt.id) FROM xnat_experimentData_share shr LEFT JOIN xnat_experimentData expt ON shr.sharing_share_xnat_experimentda_id=expt.id LEFT JOIN xdat_meta_element xme ON expt.extension=xme.xdat_meta_element_id GROUP BY shr.project, element_name "
                                                                       + "  \t) SRCH GROUP BY project,element_name "
                                                                       + ") expts ON xfm.field_value=expts.project AND xea.element_name=expts.element_name "
                                                                       + "WHERE gid.groups_groupid_xdat_user_xdat_user_id IN (:" + PARAM_USER_IDS + ") "
                                                                       + "GROUP BY xea.element_name";
    private static final String QUERY_USER_READABLE_SCAN_COUNT       = "SELECT " +
                                                                       "  element_name, " +
                                                                       "  COUNT(*) AS element_count " +
                                                                       "FROM " +
                                                                       "  (SELECT xnat_imageScanData.xnat_imagescandata_id" +
                                                                       "   FROM " +
                                                                       "     (SELECT SEARCH.* " +
                                                                       "      FROM " +
                                                                       "        (SELECT DISTINCT ON (xnat_imagescandata_id) * " +
                                                                       "         FROM " +
                                                                       "           (SELECT xnat_imageScanData.xnat_imagescandata_id, xnat_imageScanData.project AS scanProject, sharedScans.project AS scanSharedProject " +
                                                                       "            FROM " +
                                                                       "              xnat_imageScanData xnat_imageScanData " +
                                                                       "              LEFT JOIN xnat_imageScanData_share sharedScans ON xnat_imageScanData.xnat_imagescandata_id = sharedScans.sharing_share_xnat_imagescandat_xnat_imagescandata_id) SECURITY " +
                                                                       "         WHERE " +
                                                                       "           scanProject IN (:" + PARAM_PROJECT_IDS + ") OR " +
                                                                       "           scanSharedProject IN (:" + PARAM_PROJECT_IDS + ")) SECURITY " +
                                                                       "        LEFT JOIN xnat_imageScanData SEARCH ON SECURITY.xnat_imagescandata_id = SEARCH.xnat_imagescandata_id) xnat_imageScanData) SEARCH " +
                                                                       "  LEFT JOIN xnat_imageScanData scan ON search.xnat_imagescandata_id = scan.xnat_imagescandata_id " +
                                                                       "  LEFT JOIN xdat_meta_element xme ON scan.extension = xme.xdat_meta_element_id " +
                                                                       "GROUP BY " +
                                                                       "  element_name";

    private static final ResultSetExtractor<Map<String, Long>> ELEMENT_COUNT_EXTRACTOR = results -> {
        final Map<String, Long> elementCounts = new HashMap<>();
        while (results.next()) {
            final String elementName  = results.getString("element_name");
            final long   elementCount = results.getLong("element_count");
            elementCounts.put(elementName, elementCount);
        }
        return elementCounts;
    };

    @Autowired
    public ReadableCountsExtractor(final @Lazy GroupsAndPermissionsCache cache, final NamedParameterJdbcTemplate template) {
        super(cache, CACHE_READABLE_COUNTS, template);
    }

    @Override
    public Map<String, Long> extract(final Object... parameters) {
        if (parameters.length == 0) {
            return Collections.emptyMap();
        }

        final String username = (String) parameters[0];
        log.info("Extracting readable counts for user '{}'", username);

        try {
            final Map<String, Long> readableCounts   = new HashMap<>();
            final List<String>      readableProjects = getUserReadableProjects(username);
            readableCounts.put(XnatProjectdata.SCHEMA_ELEMENT_NAME, (long) readableProjects.size());
            readableCounts.put(WrkWorkflowdata.SCHEMA_ELEMENT_NAME, getUserReadableWorkflowCount(username));
            readableCounts.putAll(getUserReadableSubjectsAndExperiments(readableProjects, Arrays.asList(Users.getUserId(username), Users.getUserId("guest"))));

            if (log.isDebugEnabled()) {
                log.debug("Caching the following readable element counts for user '{}': '{}'", username, getDisplayForReadableCounts(readableCounts));
            }
            return ImmutableMap.copyOf(readableCounts);
        } catch (DataAccessException e) {
            log.error("An error occurred in the SQL for retrieving readable counts for the  user {}", username, e);
            return Collections.emptyMap();
        }
    }

    private Long getUserReadableWorkflowCount(final String username) {
        return getTemplate().queryForObject(QUERY_USER_READABLE_WORKFLOW_COUNT, new MapSqlParameterSource(PARAM_USERNAME, username), Long.class);
    }

    private Map<String, Long> getUserReadableSubjectsAndExperiments(final List<String> readableProjectIds, final List<Integer> userIds) {
        if (readableProjectIds.isEmpty()) {
            return Collections.emptyMap();
        }

        final MapSqlParameterSource userIdParameters         = new MapSqlParameterSource("userIds", userIds);
        final Map<String, Long>     readableExperimentCounts = getTemplate().query(QUERY_USER_READABLE_EXPERIMENT_COUNT, userIdParameters, ELEMENT_COUNT_EXTRACTOR);


        final MapSqlParameterSource projectParameters  = new MapSqlParameterSource("projectIds", readableProjectIds);
        final Map<String, Long>     readableScanCounts = getTemplate().query(QUERY_USER_READABLE_SCAN_COUNT, projectParameters, ELEMENT_COUNT_EXTRACTOR);


        readableExperimentCounts.putAll(readableScanCounts);
        final Long readableSubjectCount = getTemplate().queryForObject(QUERY_USER_READABLE_SUBJECT_COUNT, userIdParameters, Long.class);

        readableExperimentCounts.put(XnatSubjectdata.SCHEMA_ELEMENT_NAME, readableSubjectCount);
        return readableExperimentCounts;
    }

    private static String getDisplayForReadableCounts(final Map<String, Long> readableCounts) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(readableCounts.get(XnatProjectdata.SCHEMA_ELEMENT_NAME)).append(" projects, ");
        buffer.append(readableCounts.get(WrkWorkflowdata.SCHEMA_ELEMENT_NAME)).append(" workflows, ");
        buffer.append(readableCounts.get(XnatSubjectdata.SCHEMA_ELEMENT_NAME)).append(" subjects");
        if (readableCounts.size() > 3) {
            for (final String type : readableCounts.keySet()) {
                if (!StringUtils.equalsAny(type, XnatProjectdata.SCHEMA_ELEMENT_NAME, WrkWorkflowdata.SCHEMA_ELEMENT_NAME, XnatSubjectdata.SCHEMA_ELEMENT_NAME)) {
                    buffer.append(", ").append(readableCounts.get(type)).append(" ").append(type);
                }
            }
        }
        return buffer.toString();
    }
}
