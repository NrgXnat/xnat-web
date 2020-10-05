package org.nrg.xnat.services.resources.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.schema.SchemaElement;
import org.nrg.xdat.security.ElementSecurity;
import org.nrg.xdat.security.SecurityValues;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xft.XFTTable;
import org.nrg.xft.db.ViewManager;
import org.nrg.xft.presentation.FlattenedItemA;
import org.nrg.xft.presentation.ItemJSONBuilder;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.search.QueryOrganizer;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.xmlpath.XMLPathShortcuts;
import org.nrg.xnat.services.resources.ProjSubExptListService;
import org.nrg.xnat.services.resources.util.JSONObjectRepresentationUtil;
import org.nrg.xnat.services.resources.util.JSONTableRepresentationUtil;
import org.nrg.xnat.services.resources.util.ResourceXapiUtil;
import org.springframework.stereotype.Service;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class ProjSubExptListServiceImpl extends ResourceXapiUtil implements ProjSubExptListService {

	XnatProjectdata proj=null;
	XnatSubjectdata subject=null;
	XnatExperimentdata _existing= null;
	XnatExperimentdata _experiment = null;
	
	public Map<String, String> fieldMapping = new HashMap<>();
	@Override
	public String getProjectExperiments(UserI user, String projectId, String experimentId) throws Exception {
		fieldMapping.putAll(XMLPathShortcuts.getInstance().getShortcuts(XMLPathShortcuts.EXPERIMENT_DATA, true));
		if (projectId != null && experimentId != null) {
			return getProjectExperimentByExperimentId(projectId, experimentId, user);
		} else if (projectId != null) {
			return getProjectExperimentByProjectId(projectId, user);
		}
		return "invalid resource";
	}
	private String getProjectExperimentByProjectId(String projectId, UserI user) throws IOException {
		proj = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
		XFTTable table = null;
//		try {
//			final SecurityValues values = new SecurityValues();
//			values.put("xnat:subjectData/project", proj.getId());
//			values.put("xnat:subjectData/sharing/share/project", proj.getId());
//			final SchemaElement se= SchemaElement.GetElement(XnatSubjectdata.SCHEMA_ELEMENT_NAME);
//			
//		} catch (Exception e1) {
//			//log.error("", e1);
//			return null;
//		}
		
		try {
			final QueryOrganizer qo = new QueryOrganizer("xnat:subjectAssessorData", user, ViewManager.ALL);
			qo.addField("xnat:subjectAssessorData/ID");
			
			CriteriaCollection where=new CriteriaCollection("AND");

			CriteriaCollection cc= new CriteriaCollection("OR");
			cc.addClause("xnat:subjectAssessorData"+"/project", proj.getId());
			cc.addClause("xnat:subjectAssessorData"+"/sharing/share/project", proj.getId());
			where.addClause(cc);
			
			 if(!ElementSecurity.IsSecureElement("xnat:subjectAssessorData")){
	                qo.addField("xnat:experimentData/extension_item/element_name");
	                qo.addField("xnat:experimentData/project");
	            }
			 qo.setWhere(where);

				String query=qo.buildQuery();

				table=XFTTable.Execute(query, user.getDBName(), user.getUsername());
				
				if(table.size()>0){
					 if(!ElementSecurity.IsSecureElement("xnat:subjectAssessorData")){
		                    List<Object[]> remove= new ArrayList<>();
		                    Hashtable<String, Boolean> checked = new Hashtable<>();

		                    String enS=qo.getFieldAlias("xnat:experimentData/extension_item/element_name");
		                    if(enS==null) {
		                       // logger.error("Couldn't find property xnat:experimentData/extension_item/element_name for search",new Exception());
		                        //this.getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
		                        return null;
		                    }

		                    Integer en=table.getColumnIndex(enS.toLowerCase());
		                    
		                    for(Object[] row : table.rows()) {
		                        String element_name=(String)row[en];
		                        try{
		                            if(element_name==null){
		                                remove.add(row);
		                            }else{

		                                if(!checked.containsKey(element_name)){
		                                    if(Permissions.canRead(user, element_name + "/project",proj.getId())){
		                                        checked.put(element_name, Boolean.TRUE);
		                                    }else{
		                                        checked.put(element_name, Boolean.FALSE);
		                                    }
		                                }

		                                if(!checked.get(element_name)){
		                                    remove.add(row);
		                                }
		                            }
		                        } catch (Throwable e) {
		                            //logger.debug("Problem occurred iterating secure elements", e);
		                            remove.add(row);
		                        }
		                    }

		                    table.rows().removeAll(remove);
		                }
					
					table=formatHeaders(table,qo,"xnat:subjectAssessorData"+"/ID","/data/experiments/");

					final Integer labelI   = table.getColumnIndex("label");
					final Integer idI      = table.getColumnIndex("xnat:subjectAssessorData" + "/ID");
					final Integer subjectI = table.getColumnIndex("subject_label");
					final Integer projectI = table.getColumnIndex("project");
					if (labelI != null && idI != null) {
						final XFTTable  sharedExperiments = XFTTable.Execute("SELECT\n"
																			 + "  expt.sharing_share_xnat_experimentda_id AS id,\n"
																			 + "  expt.label                              AS experiment,\n"
																			 + "  part.label                              AS subject,\n"
																			 + "  expt.project                            AS project\n"
																			 + "FROM xnat_experimentdata_share expt\n"
																			 + "  LEFT JOIN xnat_subjectassessordata assessor ON assessor.id = expt.sharing_share_xnat_experimentda_id\n"
																			 + "  LEFT JOIN xnat_projectparticipant part ON part.subject_id = assessor.subject_id AND part.project = expt.project\n"
																			 + "WHERE expt.project = '" + proj.getId() + "'", user.getDBName(), user.getUsername());
						final Hashtable sharedLabels      = sharedExperiments.toHashtable("id", "experiment");
						final Hashtable sharedSubjects    = subjectI != null ? sharedExperiments.toHashtable("id", "subject") : null;
						final Hashtable sharedProjects    = projectI != null ? sharedExperiments.toHashtable("id", "project") : null;
						for (final Object[] row : table.rows()) {
							final String id = (String) row[idI];
							if (sharedLabels.containsKey(id)) {
								final String sharedLabel = (String) sharedLabels.get(id);
								if (null != sharedLabel && !sharedLabel.equals("")) {
									row[labelI] = sharedLabel;
								}
								if (subjectI != null) {
									final String sharedSubject = (String) sharedSubjects.get(id);
									if (null != sharedSubject && !sharedSubject.equals("")) {
	                                    row[subjectI] = sharedSubject;
	                                }
								}
								if (projectI != null) {
									final String sharedProject = (String) sharedProjects.get(id);
									if (null != sharedProject && !sharedProject.equals("")) {
	                                    row[projectI] = sharedProject;
	                                }
								}
							}
						}
					}
				}
		}catch (Exception e) {
			//logger.error("",e);
		}
	Hashtable<String,Object> params= new Hashtable<>();
	if (table != null)
		params.put("totalRecords", table.size());
	return new JSONTableRepresentationUtil(table, null, params).getText();
	}
	private String getProjectExperimentByExperimentId(String projectId, String experimentId, UserI user) throws  Exception {
		proj = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
		_existing = XnatExperimentdata.GetExptByProjectIdentifier(projectId, experimentId, user, false);
		
		 if (_experiment == null && StringUtils.isNotBlank(experimentId)) {
	            _experiment = XnatExperimentdata.getXnatExperimentdatasById(experimentId, user, false);

	            if (proj != null) {
	                if (_experiment == null) {
	                    _experiment = XnatExperimentdata.GetExptByProjectIdentifier(proj.getId(), experimentId, user, false);
	                }
	            }
	        }

	        if (_experiment == null) {
	            return null;
	        }

//	         if (StringUtils.equals(filepath, "history")) {
//	            try {
//	                return buildChangesets(_experiment.getItem(), _experiment.getStringProperty("ID"));
//	            } catch (Exception e) {
//	                //logger.error("", e);
//	                return null;
//	            }
//	        } else if (StringUtils.startsWith(filepath, "projects")) {
//	            return representProjectsForArchivableItem(_experiment.getLabel(), _experiment.getPrimaryProject(false), _experiment.getProjectDatas());
//	        } else {
	            FlattenedItemA.HistoryConfigI history = (isQueryVariableTrue("includeHistory")) ? FlattenedItemA.GET_ALL : new FlattenedItemA.HistoryConfigI() {
                    @Override
                    public boolean getIncludeHistory() {
                        return false;
                    }
                };
			 return new JSONObjectRepresentationUtil((new ItemJSONBuilder()).call(_experiment.getItem(), history, isQueryVariableTrue("includeHeaders"))).getText();
	            
	        //}
	}
	
	
	
	
	private boolean isQueryVariableTrue(String string) {
		return false;
	}
	@Override
	public boolean allowPost() {
		return false;
	}

	@Override
	public void handlePost() {
		
	}

}