/**
 * 
 */
package org.nrg.xnat.services.resources.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.nrg.action.ServerException;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatReconstructedimagedata;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.om.XnatSubjectassessordata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFTTable;
import org.nrg.xft.db.PoolDBUtils;
import org.nrg.xft.db.ViewManager;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.presentation.FlattenedItemA;
import org.nrg.xft.presentation.ItemJSONBuilder;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.search.QueryOrganizer;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.XftStringUtils;
import org.nrg.xnat.helpers.xmlpath.XMLPathShortcuts;
import org.nrg.xnat.restlet.representations.BeanRepresentation;
import org.nrg.xnat.restlet.representations.ItemXMLRepresentation;
import org.nrg.xnat.services.resources.ProjectSubjectListService;
import org.nrg.xnat.services.resources.util.BeanRepresentationUtil;
import org.nrg.xnat.services.resources.util.ItemXMLRepresentationUtil;
import org.nrg.xnat.services.resources.util.JSONObjectRepresentationUtil;
import org.nrg.xnat.services.resources.util.JSONTableRepresentationUtil;
import org.nrg.xnat.services.resources.util.ResourceXapiUtil;
import org.nrg.xnat.utils.CatalogUtils;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProjectSubjectListServiceImpl extends ResourceXapiUtil implements ProjectSubjectListService {
	private final static Logger logger = LoggerFactory.getLogger(ProjectSubjectListServiceImpl.class);

	private XnatProjectdata proj = null;
	public String userName = null;
	private XnatSubjectdata sub = null;
	ItemI security = null;
	String xmlPath = null;
	String type = null;
    ItemI parent = null;
    private XFTTable catalogs = null;
	 //ArrayList<XnatExperimentdata> experiments = new ArrayList<>();
   // XnatSubjectassessordata expt = null;
    XnatSubjectassessordata existing;

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
		else if(value.equals("all"))
			return true;
		return false;
	}

	@Override
	public String getProjectSubjectExperimentResource(String projectId, String subjectId, String experimentId) throws IOException {
		if (projectId != null && subjectId != null && experimentId != null) {
			return getProjectSubjectExperimentResourceById(projectId, subjectId,experimentId);
		}
		return "invalid resource";
	}

	private String getProjectSubjectExperimentResourceById(String projectId, String subjectId, String experimentId) throws IOException {
		ArrayList<XnatExperimentdata> experiments = new ArrayList<>();
		getProjectData(projectId);
		
		getSubjectData(subjectId);
		
		experiments = getExperimentsData(experimentId);
		
		XFTTable table = null;
		if (experiments.size() > 0 || sub != null || proj != null) {
			try {
				table = loadCatalogs(null, false, isQueryVariableTrue("all"),experiments);
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

	private ArrayList<XnatExperimentdata>  getExperimentsData(String experimentId) {
		ArrayList<XnatExperimentdata> experiments = new ArrayList<>();
        if (experimentId != null) {
            for (String s : XftStringUtils.CommaDelimitedStringToArrayList(experimentId)) {
                XnatExperimentdata expt = XnatExperimentdata.getXnatExperimentdatasById(s, getUser(), false);

                if (expt == null && proj != null) {
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
		return experiments;
	}
	
	private static final Predicate<String> CONTAINS_QUOTE = new Predicate<String>() {
        @Override
        public boolean apply(final String resourceId) {
            return StringUtils.contains(resourceId, "'");
        }
    };

    private static final Predicate<String> HACK_CHECK = new Predicate<String>() {
        @Override
        public boolean apply(final String resourceId) {
            return PoolDBUtils.HackCheck(resourceId);
        }
    };
	 public void checkResourceIDs(final List<String> resourceIds) throws Exception {
	        if (resourceIds == null || resourceIds.isEmpty()) {
	            return;
	        }
	        if (Iterables.any(resourceIds, CONTAINS_QUOTE)) {
	            throw new Exception("Possible SQL Injection attempt. The \"'\" character is not allowed in resource labels: " + StringUtils.join(Iterables.filter(resourceIds, CONTAINS_QUOTE), ", "));
	        }
	        if (Iterables.any(resourceIds, HACK_CHECK)) {
	            throw new Exception("Possible SQL Injection attempt: " + StringUtils.join(Iterables.filter(resourceIds, CONTAINS_QUOTE), ", "));
	        }
	    }
	
	 public XFTTable loadCatalogs(final List<String> resourceIds, final boolean includeURI, final boolean allowAll, ArrayList<XnatExperimentdata> experiments) throws Exception {
	        checkResourceIDs(resourceIds);

	        final StringBuilder query = new StringBuilder();
	        final boolean hasResourceIds = resourceIds != null && !resourceIds.isEmpty();
	        final boolean isInResource = StringUtils.equalsIgnoreCase(type, "in");
	        
	        final UserI user = getUser();
	        if (!experiments.isEmpty()) {
	            security = experiments.get(0);
	            parent = experiments.get(0);
	            final List<String> experimentIds = Lists.transform(experiments, new Function<XnatExperimentdata, String>() {
	                @Override
	                public String apply(final XnatExperimentdata experiment) {
	                    return experiment.getId();
	                }
	            });
	            if (allowAll && (isQueryVariableTrue("all") || resourceIds != null)) {
	                xmlPath = "xnat:experimentData/resources/resource";
	                final Map<String, String> variables = new HashMap<>();
	                variables.put("username", user.getUsername());
	                variables.put("sessionIds", StringUtils.join(experimentIds, "', '"));
	                final String userAccessibleAccessorIds = StringSubstitutor.replace(USER_ACCESSIBLE_ASSESSOR_IDS, variables);
	                // resources

	                query.append("SELECT * FROM (").append(STARTER_FIELDS).append(", 'resources'::TEXT AS category, NULL::TEXT AS cat_id,''::TEXT AS cat_desc");
	                if (includeURI) {
	                    query.append(",'/experiments/' || res_map.xnat_experimentdata_id || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
	                }
	                query.append(" FROM xnat_experimentdata_resource res_map JOIN xnat_abstractresource abst ON res_map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id WHERE res_map.xnat_experimentdata_id IN ('");
	                query.append(StringUtils.join(experimentIds, "', '"));
	                query.append("') ");
	                query.append("  UNION ");
	                query.append(STARTER_FIELDS);
	                query.append(", 'scans'::TEXT,isd.id,isd.type");
	                if (includeURI) {
	                    query.append(",'/experiments/' || isd.image_session_id || '/scans/' || isd.id || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
	                }
	                query.append(" FROM xnat_imagescanData isd JOIN xnat_abstractresource abst ON isd.xnat_imagescandata_id=abst.xnat_imagescandata_xnat_imagescandata_id JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id WHERE isd.image_session_id IN ('");
	                query.append(StringUtils.join(experimentIds, "', '"));
	                query.append("') UNION ");
	                query.append(STARTER_FIELDS);
	                query.append(", 'reconstructions'::TEXT,recon.id,recon.type");
	                if (includeURI) {
	                    query.append(",'/experiments/' || recon.image_session_id || '/reconstructions/' || recon.id || '/out' || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
	                }
	                query.append(" FROM xnat_reconstructedimagedata recon JOIN recon_out_resource map ON recon.xnat_reconstructedimagedata_id=map.xnat_reconstructedimagedata_xnat_reconstructedimagedata_id JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id WHERE image_session_id IN ('");
	                query.append(StringUtils.join(experimentIds, "', '"));
	                query.append("') UNION ");
	                query.append(STARTER_FIELDS);
	                query.append(", 'assessors'::TEXT,iad.id,xes.singular");
	                if (includeURI) {
	                    query.append(",'/experiments/' || iad.imagesession_id || '/assessors/' || iad.id || '/out' || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
	                }
	                query.append(" FROM ").append(userAccessibleAccessorIds).append(" iad JOIN img_assessor_out_resource map ON iad.id=map.xnat_imageassessordata_id JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id LEFT JOIN xdat_element_security xes ON xme.element_name=xes.element_name WHERE iad.imagesession_id IN ('");
	                query.append(StringUtils.join(experimentIds, "', '"));
	                query.append("') UNION ");
	                query.append(STARTER_FIELDS);
	                query.append(", 'assessors'::TEXT,iad.id,xes.singular");
	                if (includeURI) {
	                    query.append(",'/experiments/' || iad.imagesession_id || '/assessors/' || iad.id || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
	                }
	                query.append(" FROM ").append(userAccessibleAccessorIds).append(" iad JOIN xnat_experimentdata_resource map ON iad.id=map.xnat_experimentdata_id JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id LEFT JOIN xdat_element_security xes ON xme.element_name=xes.element_name WHERE iad.imagesession_id IN ('");
	                query.append(StringUtils.join(experimentIds, "', '"));
	                query.append("')) all_resources");

	                if (hasResourceIds) {
	                    query.append(" WHERE (").append(getResourceIdsWhereClause(resourceIds, "xnat_abstractresource_id", "label")).append(")");
	                }
	            } 
	        }  

	        final String completedQuery = query.toString();
	        log.debug("Loading catalog for user '{}' using query: {}", user.getUsername(), completedQuery);
	        return XFTTable.Execute(completedQuery, user.getDBName(), userName);
	    }

	private String getProjectSubjectExperimentResourceByResourceId(String projectId, String subjectId,String experimentId, String resourceId) throws IOException {
		List<String> resourceIds = new ArrayList<>();
		List<XnatAbstractresource> resources = new ArrayList<>();
		//XFTTable table = null;
		resourceIds.add(resourceId);
		getAllMatches(resourceIds, resources);
//
//		if (experimentId != null) {
//			expt = XnatExperimentdata.getXnatExperimentdatasById(experimentId, getUser(), false);
//		}
//		if (expt != null) {
//			try {
//				table = loadCatalogs(resourceIds, false, true);// isQueryVariableTrue("all") -> true
//			} catch (Exception e) {
//				logger.error("", e);
//			}
//		}

		if (resources.size() == 1) {
			final XnatAbstractresource resource = resources.get(0);
			
				 try {
		                if (proj == null) {
		                    if (parent.getItem().instanceOf("xnat:experimentData")) {
		                        proj = ((XnatExperimentdata) parent).getPrimaryProject(false);
		                    } else if (security.getItem().instanceOf("xnat:experimentData")) {
		                        proj = ((XnatExperimentdata) security).getPrimaryProject(false);
		                    }
		                }

		                if (resource.getItem().instanceOf("xnat:resourceCatalog")) {
		                    try {
		                        CatalogUtils.CatalogData catalogData = CatalogUtils.CatalogData.getOrCreateAndClean(
		                                proj.getRootArchivePath(), ((XnatResourcecatalog) resource), isQueryVariableTrue("includeRootPath"), proj.getId());
		                        return new BeanRepresentationUtil(catalogData.catBean).getText();
		                    } catch (ServerException e) {
		                    }
		                } else {
		                	return new ItemXMLRepresentationUtil(resource.getItem()).getText();
		                }
		            } catch (ElementNotFoundException e) {
		                log.error("", e);
		            }
				 }

		return "input null";
	}
	
	private void getAllMatches(List<String> resourceIds, List<XnatAbstractresource> resources) {
		resources.clear();
		try {
			catalogs = loadCatalogs(resourceIds, false, true);
			// setCatalogs(loadCatalogs(resourceIds, false, true));
		} catch (Exception e) {
			log.error("An error occurred trying to load catalogs from the resource IDs: {}", resourceIds, e);
		}
		initializeResourcesFromIdsAndCatalogs(resourceIds, resources);
	}
	private void initializeResourcesFromIdsAndCatalogs(List<String> resourceIds, List<XnatAbstractresource> resources) {
		if (catalogs != null && catalogs.size() > 0) {
			for (final Object[] row : catalogs.rows()) {
				final String id = Integer.toString((Integer) row[0]);
				final String label = (String) row[1];
				resources.addAll(
						Lists.transform(Lists.newArrayList(Iterables.filter(resourceIds, new Predicate<String>() {
							@Override
							public boolean apply(@Nullable final String resourceId) {
								return StringUtils.equalsAny(resourceId, id, label);
							}
						})), new Function<String, XnatAbstractresource>() {
							@Override
							public XnatAbstractresource apply(final String resourceId) {
								return XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(row[0],
										getUser(), false);
							}
						}));
			}
		}
	}

	@Override
	public String getProjectSubjectExperimentResources(String projectId, String subjectId, String experimentId,String resourceId) throws IOException {
		if (projectId != null && subjectId != null && experimentId != null && resourceId != null)
			return getProjectSubjectExperimentResourceByResourceId(projectId, subjectId, experimentId, resourceId);
		return "invalid resource";
	}
}