package org.nrg.xnat.services.resources.impl;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.UserGroup;
import org.nrg.xdat.security.UserGroupI;
import org.nrg.xdat.security.helpers.Groups;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xft.XFTItem;
import org.nrg.xft.XFTTable;
import org.nrg.xft.presentation.FlattenedItemA;
import org.nrg.xft.presentation.ItemJSONBuilder;
import org.nrg.xnat.services.resources.ProjectGroupResourceService;
import org.nrg.xnat.services.resources.util.JSONObjectRepresentationUtil;
import org.nrg.xnat.services.resources.util.JSONTableRepresentationUtil;
import org.nrg.xnat.services.resources.util.ResourceXapiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProjectGroupResourceServiceImpl extends ResourceXapiUtil implements ProjectGroupResourceService {
	private static final Logger _log = LoggerFactory.getLogger(ProjectGroupResourceServiceImpl.class);
	private XnatProjectdata project = null;
	private UserGroupI group = null;

	@Autowired
	public ProjectGroupResourceServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}

	@Override
	public void handleDelete() {

	}

	@Override
	public void handlePost() {

	}

	@Override
	public void handlePut() {

	}

	@Override
	public String getProjectGroupResources(String projectId, String groupId) throws Exception {
		if (projectId != null && groupId != null) {
			return getProjectGroupResourceByIds(projectId, groupId);
		} else if (projectId != null) {
			return getProjectGroupResourceById(projectId);
		}
		return "invalid resource";
	}

	private String getProjectGroupResourceById(String projectId) throws IOException {
		project = XnatProjectdata.getProjectByIDorAlias(projectId, getUser(), false);
		if (!Permissions.canReadProject(getUser(), project.getId())) {
			// getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			_log.debug("User does not have permission..");
		}
		final ProjectGroupRowMapper mapper = new ProjectGroupRowMapper();
		_template.query(QUERY_PROJECT_GROUPS, new MapSqlParameterSource("projectId", project.getId()), mapper);
		
		final XFTTable table = new XFTTable();
        table.initTable(GROUP_LIST_HEADERS, mapper.getRows());

        final Hashtable<String, Object> params = new Hashtable<>();
        params.put("title", "Projects");
        params.put("totalRecords", table.size());
    //    return representTable(table, overrideVariant(variant), params);
		return new JSONTableRepresentationUtil(table, null, params).getText();
	}

	private String getProjectGroupResourceByIds(String projectId, String groupId) throws IOException, Exception {
		project = XnatProjectdata.getProjectByIDorAlias(projectId, getUser(), false);
		group = findGroupByNameAndDisplayName(groupId, null);
		XFTItem item = ((UserGroup) group).getUserGroupImpl().getItem();
		FlattenedItemA.HistoryConfigI history = (true) ? FlattenedItemA.GET_ALL : new FlattenedItemA.HistoryConfigI() {
            @Override
            public boolean getIncludeHistory() {
                return false;
            }
        };
        return new JSONObjectRepresentationUtil((new ItemJSONBuilder()).call(item, history, true)).getText();
	}

	private static class ProjectGroupRowMapper implements RowCallbackHandler {
		@Override
		public void processRow(final ResultSet row) throws SQLException {
			final List<Object> items = new ArrayList<>();
			for (final String header : GROUP_LIST_HEADERS) {
				items.add(row.getObject(header));
			}
			_rows.add(items.toArray(new Object[0]));
		}

		ArrayList<Object[]> getRows() {
			return _rows;
		}

		private final ArrayList<Object[]> _rows = new ArrayList<>();
	}
	
	private UserGroupI findGroupByNameAndDisplayName(final String groupName, final String displayName) {
        if (StringUtils.isAllBlank(groupName, displayName)) {
            return null;
        }
        if (StringUtils.isNotBlank(groupName)) {
            if (NumberUtils.isCreatable(groupName)) {
                final UserGroupI group = Groups.getGroupByPK(groupName);
                if (group != null) {
                    return group;
                }
            }
            final UserGroupI byName = Groups.getGroup(groupName);
            if (byName != null) {
                return byName;
            }
            final UserGroupI byProjectAndGroupName = Groups.getGroup(project.getId() + "_" + groupName);
            if (byProjectAndGroupName != null) {
                return byProjectAndGroupName;
            }
            final UserGroupI byTagAndName = Groups.getGroupByTagAndName(project.getId(), groupName);
            if (byTagAndName != null) {
                return byTagAndName;
            }
        }
        if (StringUtils.isNotBlank(displayName)) {
            final UserGroupI byTagAndName = Groups.getGroupByTagAndName(project.getId(), displayName);
            if (byTagAndName != null) {
                return byTagAndName;
            }
            return Groups.getGroup(project.getId() + "_" + displayName);
        }
        return null;
    }
	

	private static final ArrayList<String> GROUP_LIST_HEADERS = new ArrayList<>(
			Arrays.asList("id", "displayname", "tag", "xdat_usergroup_id", "users"));
	private static final List<String> PROTECTED_DISPLAY_NAMES = Arrays.asList("Owners", "Members", "Collaborators");
	private static final String QUERY_PROJECT_GROUPS = "SELECT ug.id, ug.displayname,ug.tag,ug.xdat_usergroup_id, COUNT(map.groups_groupid_xdat_user_xdat_user_id) AS users FROM xdat_userGroup ug LEFT JOIN xdat_user_groupid map ON ug.id=map.groupid WHERE tag = :projectId GROUP BY ug.id, ug.displayname,ug.tag,ug.xdat_usergroup_id  ORDER BY ug.displayname DESC";
	private final NamedParameterJdbcTemplate _template;
}
