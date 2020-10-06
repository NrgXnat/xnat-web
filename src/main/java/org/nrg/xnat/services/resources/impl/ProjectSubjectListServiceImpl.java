/**
 * 
 */
package org.nrg.xnat.services.resources.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Objects;

import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectassessordata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.XFTTable;
import org.nrg.xft.db.ViewManager;
import org.nrg.xft.presentation.FlattenedItemA;
import org.nrg.xft.presentation.ItemJSONBuilder;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.search.QueryOrganizer;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.XftStringUtils;
import org.nrg.xnat.helpers.xmlpath.XMLPathShortcuts;
import org.nrg.xnat.services.resources.ProjectSubjectListService;
import org.nrg.xnat.services.resources.util.JSONObjectRepresentationUtil;
import org.nrg.xnat.services.resources.util.JSONTableRepresentationUtil;
import org.nrg.xnat.services.resources.util.ResourceXapiUtil;
import org.nrg.xnat.utils.CatalogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProjectSubjectListServiceImpl extends ResourceXapiUtil implements ProjectSubjectListService {
	private final static Logger logger = LoggerFactory.getLogger(ProjectSubjectListServiceImpl.class);

	private XnatProjectdata proj = null;
	public String userName = null;
	private XnatSubjectdata sub = null;
   // XnatSubjectassessordata expt = null;
    XnatSubjectassessordata existing;
    ArrayList<XnatExperimentdata> experiments =null;

	@Autowired
	public ProjectSubjectListServiceImpl() {
		fieldMapping.putAll(XMLPathShortcuts.getInstance().getShortcuts(XMLPathShortcuts.SUBJECT_DATA, true));
	}

	@Override
	public String getProjectSubjectResource(String projectId, String subjectId, String experimentId) throws Exception {
		
		if (projectId != null && subjectId != null && experimentId != null) {
			return getProjectSubjectExperimentById(projectId, subjectId,experimentId);
		}else if (projectId != null && subjectId != null) {
			return getProjectSubjectById(projectId, subjectId);
		} else if (projectId != null) {
			return getProjectSubjectById(projectId);
		} 
		return "invalid resource";
	}

	private String getProjectSubjectExperimentById(String projectId, String subjectId, String experimentId) throws Exception {
		 XnatSubjectassessordata experiment = null;
		 setProjectSubjectOrExisting(projectId,subjectId,experimentId);
		 if (Objects.isNull(experiment) && Objects.nonNull(experimentId)) {
			 experiment = (XnatSubjectassessordata) XnatExperimentdata.getXnatExperimentdatasById(experimentId, getUser(), false);
			 if (Objects.isNull(experiment) && Objects.nonNull(proj)) {
				 experiment = (XnatSubjectassessordata) XnatExperimentdata.GetExptByProjectIdentifier(proj.getId(), experimentId, getUser(), false);
	            }
	        }
		 FlattenedItemA.HistoryConfigI history = (isQueryVariableTrue("includeHistory")) ? FlattenedItemA.GET_ALL : new FlattenedItemA.HistoryConfigI() {
             @Override
             public boolean getIncludeHistory() {
                 return false;
             }
         };
		 return new JSONObjectRepresentationUtil((new ItemJSONBuilder()).call(sub.getItem(), history, isQueryVariableTrue("includeHeaders"))).getText();
	}

	private void setProjectSubjectOrExisting(String projectId, String subjectId, String experimentId) {
		if(Objects.nonNull(projectId))
			getProjectData(projectId);
		if(Objects.nonNull(subjectId))
			getSubjectData(subjectId);
		if(Objects.nonNull(experimentId) && Objects.nonNull(proj) && Objects.isNull(existing))
			getExistingData(experimentId);
	 fieldMapping.putAll(XMLPathShortcuts.getInstance().getShortcuts(XMLPathShortcuts.EXPERIMENT_DATA, false));
	}

	private void getExistingData(String experimentId) {
		existing = (XnatSubjectassessordata) XnatExperimentdata.GetExptByProjectIdentifier(proj.getId(), experimentId, getUser(), false);
		  if (Objects.isNull(existing)) {
            existing = (XnatSubjectassessordata) XnatExperimentdata.getXnatExperimentdatasById(experimentId, getUser(), false);
            if (Objects.nonNull(existing) && (Objects.nonNull(proj) && !existing.hasProject(proj.getId()))) 
            	existing = null;
            }
		  }

	private void getSubjectData(String subjectId) {
		sub = XnatSubjectdata.GetSubjectByProjectIdentifier(proj.getId(), subjectId, getUser(), false);
		if (Objects.isNull(sub)) {
			sub = XnatSubjectdata.getXnatSubjectdatasById(subjectId, getUser(), false);
			if (Objects.nonNull(sub) && (Objects.nonNull(proj) && !sub.hasProject(proj.getId()))) {
				sub = null;
			}
		}
	}

	private void getProjectData(String projectId) {
		proj = XnatProjectdata.getProjectByIDorAlias(projectId, getUser(), false);
	}

	public String getProjectSubjectById(String projectId) throws IOException {
		XFTTable table = null;
		getProjectData(projectId);
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
		
		getProjectData(projectId);
		
		getSubjectData(subjectId);

		if (sub != null) {
			try {
                FlattenedItemA.HistoryConfigI history = (isQueryVariableTrue("includeHistory")) ? FlattenedItemA.GET_ALL : new FlattenedItemA.HistoryConfigI() {
                    @Override
                    public boolean getIncludeHistory() {
                        return false;
                    }
                };
			 return new JSONObjectRepresentationUtil((new ItemJSONBuilder()).call(sub.getItem(), history, isQueryVariableTrue("includeHeaders"))).getText();
		} catch (Exception e) {
			log.error("Inernal server error : --");
            return null;
		}
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

	private boolean isQueryVariableTrue(String value) {
		if(value.equals("includeHistory"))
			return true;
		else if(value.equals("includeHeaders"))
			return false;
		return false;
	}

	@Override
	public String getProjectSubjectExperimentResource(String projectId, String subjectId, String experimentId, String resourceId) throws IOException {
		if (projectId != null && subjectId != null && experimentId != null && resourceId != null) {
			return getProjectSubjectExperimentResourceByResourceId(projectId, subjectId,experimentId,resourceId );
		}else	if (projectId != null && subjectId != null && experimentId != null) {
			return getProjectSubjectExperimentResourceById(projectId, subjectId,experimentId);
		}
		return "invalid resource";
	}

	private String getProjectSubjectExperimentResourceById(String projectId, String subjectId, String experimentId) throws IOException {

		getProjectData(projectId);

		getSubjectData(subjectId);
		
		getExperimentsData(experimentId);
		
		XFTTable table = null;
		if (experiments.size() > 0 || sub != null || proj != null) {
			try {
				table = loadCatalogs(null, false, isQueryVariableTrue("all"));
			} catch (Exception e) {
				// logger.error("", e);
			}
		}
		final boolean fileStats = false; // isQueryVariableTrue("file_stats");
		final boolean cacheFileStats = false;// isQueryVariableTrue("cache_file_stats");
		final Hashtable<String, Object> params = new Hashtable<>();
		params.put("title", "Resources");

		if (table != null) {
			table = CatalogUtils.populateTable(table, getUser(), null, cacheFileStats);

			// If table.rows() is null, set recordCount to 0
			final ArrayList<Object[]> records = table.rows();
			final int recordCount = (records != null) ? records.size() : 0;

			if (logger.isDebugEnabled()) {
				logger.debug("Found a total of " + recordCount + " records");
			}
			params.put("totalRecords", recordCount);
		}
		return new JSONTableRepresentationUtil(table, null, params).getText();
	}

	private void  getExperimentsData(String experimentId) {
        if (experimentId != null) {
            for (String s : XftStringUtils.CommaDelimitedStringToArrayList(experimentId)) {
                XnatExperimentdata expt = XnatExperimentdata.getXnatExperimentdatasById(s, getUser(), false);

                if (proj != null) {
                	expt = XnatExperimentdata.GetExptByProjectIdentifier(proj.getId(), s, getUser(), false);
                }
                if (expt != null) {
                    try {
                        if (expt.canRead(getUser())) {
                        	experiments.add(expt);
                        }
                    } catch (Exception ignored) {
                    }
                } 
            }
        }
	}

	private String getProjectSubjectExperimentResourceByResourceId(String projectId, String subjectId,String experimentId, String resourceId) {
		return null;
	}

}