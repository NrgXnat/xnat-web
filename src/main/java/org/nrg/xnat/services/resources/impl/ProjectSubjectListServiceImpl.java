/**
 * 
 */
package org.nrg.xnat.services.resources.impl;

import java.io.IOException;
import java.util.Hashtable;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.XFTTable;
import org.nrg.xft.db.ViewManager;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.search.QueryOrganizer;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.xmlpath.XMLPathShortcuts;
import org.nrg.xnat.services.resources.ProjectSubjectListService;
import org.nrg.xnat.services.resources.util.JSONTableRepresentationUtil;
import org.nrg.xnat.services.resources.util.RepresentItemUtil;
import org.nrg.xnat.services.resources.util.ResourceXapiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProjectSubjectListServiceImpl extends ResourceXapiUtil implements ProjectSubjectListService {

	private XnatProjectdata proj = null;
	public String userName = null;
	private XnatSubjectdata sub = null;

	@Autowired
	public ProjectSubjectListServiceImpl() {
		fieldMapping.putAll(XMLPathShortcuts.getInstance().getShortcuts(XMLPathShortcuts.SUBJECT_DATA, true));
	}

	@Override
	public String getProjectSubjectResource(String projectId, String subjectId) throws IOException {
		if (projectId != null && subjectId != null) {
			return getProjectSubjectById(projectId, subjectId);
		} else if (projectId != null) {
			return getProjectSubjectById(projectId);
		}
		return "invalid resource";
	}

	public String getProjectSubjectById(String projectId) throws IOException {
		XFTTable table = null;
		proj = XnatProjectdata.getProjectByIDorAlias(projectId, getUser(), false);
		if (proj != null) {
			try {
				final UserI user = getUser();
				final QueryOrganizer qo = new QueryOrganizer("xnat:subjectData", user, ViewManager.ALL);
				qo.addField("xnat:subjectData/ID");
				qo.addField("xnat:subjectData/project");
				qo.addField("xnat:subjectData/label");
				qo.addField("xnat:subjectData/meta/insert_date");
				qo.addField("xnat:subjectData/meta/insert_user/login");

				final CriteriaCollection cc = new CriteriaCollection("OR");
				cc.addClause("xnat:subjectData/project", proj.getId());
				cc.addClause("xnat:subjectData/sharing/share/project", proj.getId());
				qo.setWhere(cc);

				final String query = qo.buildQuery();
				userName = user.getUsername();
				table = XFTTable.Execute(query, user.getDBName(), userName);
				table = formatHeaders(table, qo, "xnat:subjectData/ID", "/data/subjects/");

				final Integer labelI = table.getColumnIndex("label");
				final Integer idI = table.getColumnIndex("ID");
				if (labelI != null && idI != null) {
					final XFTTable t = XFTTable.Execute(
							"SELECT subject_id,label FROM xnat_projectParticipant WHERE project='" + proj.getId() + "'",
							user.getDBName(), user.getUsername());
					final Hashtable lbls = t.toHashtable("subject_id", "label");
					for (Object[] row : table.rows()) {
						final String id = (String) row[idI];
						if (lbls.containsKey(id)) {
							final String lbl = (String) lbls.get(id);
							if (null != lbl && !lbl.equals("")) {
								row[labelI] = lbl;
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			final Hashtable<String, Object> params = new Hashtable<String, Object>();
			if (table != null)
				params.put("totalRecords", table.size());

			return new JSONTableRepresentationUtil(table, null, params).getText();
		}
		final Hashtable<String, Object> params = new Hashtable<String, Object>();
		params.put("title", "Project Subjects");
		if (table != null)
			params.put("totalRecords", table.size());
		return new JSONTableRepresentationUtil(table, null, params).getText();
	}

	public String getProjectSubjectById(String projectId, String subjectId) throws IOException {
		final UserI user = getUser();
		proj = XnatProjectdata.getProjectByIDorAlias(projectId, getUser(), false);
		if (sub == null && subjectId != null) {
			sub = XnatSubjectdata.getXnatSubjectdatasById(subjectId, user, false);

			if (sub == null && proj != null) {
				sub = XnatSubjectdata.GetSubjectByProjectIdentifier(proj.getId(), subjectId, user, false);
			}
		}

		if (sub != null) {
			RepresentItemUtil representItemUtil = null;
			return representItemUtil.representItem(sub.getItem(), proj);
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
			return message.toString();
		}
	}

//	private Representation representItem(XFTItem item, MediaType mt) {
//		Representation representation = null;
//		try {
//			FlattenedItemA.HistoryConfigI history = new FlattenedItemA.HistoryConfigI() {
//				@Override
//				public boolean getIncludeHistory() {
//					return false;
//				}
//			};
//			representation = new JSONObjectRepresentation(MediaType.APPLICATION_JSON,
//					(new ItemJSONBuilder()).call(item, history, false));
//		} catch (Exception e) {
//			// getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
//			return null;
//		}
//		if (representation != null && proj != null && representation instanceof TurbineScreenRepresentation
//				&& StringUtils.isNotBlank(proj.getId())) {
//			((TurbineScreenRepresentation) representation).setRunDataParameter("project", proj.getId());
//		}
//		return representation;
//	}

}