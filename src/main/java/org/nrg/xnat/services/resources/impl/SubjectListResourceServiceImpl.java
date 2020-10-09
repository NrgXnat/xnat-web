package org.nrg.xnat.services.resources.impl;

import static org.nrg.xdat.security.helpers.Permissions.getUserProjectAccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xft.XFTTable;
import org.nrg.xft.db.ViewManager;
import org.nrg.xft.presentation.FlattenedItemA;
import org.nrg.xft.presentation.ItemJSONBuilder;
import org.nrg.xft.search.QueryOrganizer;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.xmlpath.XMLPathShortcuts;
import org.nrg.xnat.services.resources.SubjectListResourceService;
import org.nrg.xnat.services.resources.util.JSONObjectRepresentationUtil;
import org.nrg.xnat.services.resources.util.JSONTableRepresentationUtil;
import org.nrg.xnat.services.resources.util.XNATCatalogTemplateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubjectListResourceServiceImpl extends XNATCatalogTemplateUtil implements SubjectListResourceService {

	public Map<String, String> fieldMapping = new HashMap<>();

	@Autowired
	public SubjectListResourceServiceImpl() {
		fieldMapping.putAll(XMLPathShortcuts.getInstance().getShortcuts(XMLPathShortcuts.SUBJECT_DATA, false));
	}

	@Override
	public String getSubjectResource(UserI user, String subjectId) throws IOException, Exception {
		if (Objects.isNull(subjectId) || subjectId.isEmpty())
			return getSubjectListData(user);
		else if (Objects.nonNull(subjectId))
			return getSubjectBySubjectId(user, subjectId);
		return "invalid input";

	}

	private String getSubjectListData(UserI user) throws IOException {
		XFTTable table;
		try {
			List<String> readableProjects = Permissions.getReadableProjects(user);
			List<String> protectedProjects = Permissions.getAllProtectedProjects(XDAT.getJdbcTemplate());
			Collection<String> readableExcludingProtected = CollectionUtils.subtract(readableProjects, protectedProjects);
			if (readableExcludingProtected.size() <= 0) {// Projects user can see, excluding those that they might only
															// be seeing because they are protected
				boolean hasExplicitAccessToAtLeastOneProtectedProject = false;
				for (String protectedProject : protectedProjects) {
					if (StringUtils.isNotBlank(getUserProjectAccess(user, protectedProject))) {
						hasExplicitAccessToAtLeastOneProtectedProject = true;
					}
				}
				if (!hasExplicitAccessToAtLeastOneProtectedProject) {
					throw new IllegalAccessException("The user is trying to search for data, but does not have access to any projects.");
				}
			}

			QueryOrganizer qo = new QueryOrganizer("xnat:subjectData", user, ViewManager.ALL);
			qo.addField("xnat:subjectData/project");
			qo.addField("xnat:subjectData/label");
			qo.addField("xnat:subjectData/meta/insert_date");
			qo.addField("xnat:subjectData/meta/insert_user/login");

			String query = qo.buildQuery();

			table = XFTTable.Execute(query, user.getDBName(), user.getUsername());

			table = formatHeaders(table, qo, "xnat:subjectData/ID", "/data/subjects/");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		Hashtable<String, Object> params = new Hashtable<>();
		if (table != null)
			params.put("totalRecords", table.size());
		return new JSONTableRepresentationUtil(table, null, params).getText();
	}

	private String getSubjectBySubjectId(UserI user, String subjectId) throws IOException, Exception {
		if (sub == null && subjectId != null) {
			sub = XnatSubjectdata.getXnatSubjectdatasById(subjectId, user, false);

			if (sub == null && proj != null) {
				sub = XnatSubjectdata.GetSubjectByProjectIdentifier(proj.getId(), subjectId, user, false);
			}
		}
		if (sub != null) {
			FlattenedItemA.HistoryConfigI history = (false) ? FlattenedItemA.GET_ALL : new FlattenedItemA.HistoryConfigI() {
						@Override
						public boolean getIncludeHistory() {
							return false;
						}
					};
			return new JSONObjectRepresentationUtil((new ItemJSONBuilder()).call(sub.getItem(), history, false)).getText();
		} else {
			final StringBuilder message = new StringBuilder("Unable to find the specified subject. ");
			if (proj == null) {
				message.append(
						"When searching by subject ID only, you must specify the accession number and not the subject label, which is not unique across the XNAT system. ");
				message.append(subjectId).append(" is not a known subject accession ID.");
			} else {
				message.append("The project ").append(proj.getId())
						.append(" does not contain a subject identifiable by the ID or label ").append(subjectId)
						.append(".");
			}
			return null;
		}
	}

	public XFTTable formatHeaders(XFTTable table, QueryOrganizer qo, String idpath, String URIpath) {
		final ArrayList<String> newColumns = new ArrayList<>();
		for (String column : table.getColumns()) {
			String xPath = qo.getXPATHforAlias(column.toLowerCase());
			if (xPath == null) {
				newColumns.add(column);
			} else {
				String key = this.getLabelForFieldMapping(xPath);
				if (key == null) {
					newColumns.add(xPath);
				} else {
					newColumns.add(key);
				}
			}
		}

		int idIndex = table.getColumnIndex(qo.getFieldAlias(idpath));

		if (URIpath != null)
			newColumns.add("URI");

		XFTTable clone = new XFTTable();
		clone.initTable(newColumns);
		for (Object[] row : table.rows()) {
			Object[] newRow;
			if (URIpath != null)
				newRow = new Object[row.length + 1];
			else
				newRow = new Object[row.length];

			System.arraycopy(row, 0, newRow, 0, row.length);

			String id = (String) row[idIndex];

			if (URIpath != null)
				newRow[row.length] = URIpath + id;

			clone.insertRow(newRow);
		}

		return clone;
	}

	public String getLabelForFieldMapping(String xPath) {
		for (Map.Entry<String, String> entry : fieldMapping.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(xPath)) {
				return entry.getKey();
			}
		}
		return null;
	}
}
