package org.nrg.xnat.services.resources.files.impl;

import org.nrg.xnat.restlet.representations.JSONTableRepresentation;
import org.nrg.xnat.services.archive.CatalogService;
import org.nrg.xnat.services.resources.files.FileListService;
import org.nrg.xnat.utils.CatalogUtils;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatReconstructedimagedata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.turbine.utils.TurbineUtils;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFTTable;
import org.nrg.xft.db.PoolDBUtils;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.XftStringUtils;

@Service
public class FileListServiceImpl implements FileListService {
	
	XnatProjectdata proj = null;
	XnatSubjectdata sub  = null;
	ArrayList<XnatExperimentdata> expts = new ArrayList<>();
	ArrayList<XnatImagescandata> scans = new ArrayList<>();
	ArrayList<XnatReconstructedimagedata> recons = new ArrayList<>();
	ArrayList<XnatExperimentdata> assesseds = new ArrayList<>();
	//private CatalogService _catalogService;
	String type = null;
    ItemI parent = null;
    ItemI security = null;
    String xmlPath = null;
    protected boolean completeDocument = false;
	
	@Override
	public String getResourceFiles(UserI sessionUser, String assessedId, String scanId) {
		return null;
	}

	@Override
	public String getResources(UserI user, String assessedId, String scanId) throws IOException {
			getProjSubAcceScanAndExperData(user,assessedId,scanId);
			XFTTable table = null;
	        if (recons.size() > 0 || scans.size() > 0 || expts.size() > 0 || sub != null || proj != null) {
	            try {
	                table = loadCatalogs(null, false, true, user);
	            } catch (Exception e) {
	            	 //logger.error("", e);
	            }
	        }
	        final boolean fileStats      = false;
	        final boolean cacheFileStats = false;
	        if (fileStats) {
	            try {
	                if (proj == null) {
	                    if (parent.getItem().instanceOf("xnat:experimentData")) {
	                        proj = ((XnatExperimentdata) parent).getPrimaryProject(false);
	                        // Per FogBugz 4746, prevent NPE when user doesn't have access to resource (MRH)
	                        // Check access through shared project when user doesn't have access to primary project
	                        if (proj == null) {
	                            proj = (XnatProjectdata) ((XnatExperimentdata) parent).getFirstProject();
	                        }
	                    } else if (security.getItem().instanceOf("xnat:experimentData")) {
	                        proj = ((XnatExperimentdata) security).getPrimaryProject(false);
	                        // Per FogBugz 4746, ....
	                        if (proj == null) {
	                            proj = (XnatProjectdata) ((XnatExperimentdata) security).getFirstProject();
	                        }
	                    } else if (security.getItem().instanceOf("xnat:subjectData")) {
	                        proj = ((XnatSubjectdata) security).getPrimaryProject(false);
	                        // Per FogBugz 4746, ....
	                        if (proj == null) {
	                            proj = (XnatProjectdata) ((XnatSubjectdata) security).getFirstProject();
	                        }
	                    } else if (security.getItem().instanceOf("xnat:projectData")) {
	                        proj = (XnatProjectdata) security;
	                    }
	                }

	            } catch (ElementNotFoundException e) {
	            	//logger.error("", e);
	            }
	        }


	        final Hashtable<String, Object> params = new Hashtable<>();
	        params.put("title", "Resources");

	        if (table != null) {
	            table = CatalogUtils.populateTable(table, user, proj, cacheFileStats);

	            // If table.rows() is null, set recordCount to 0
	            final ArrayList<Object[]> records     = table.rows();
	            final int                 recordCount = (records != null) ? records.size() : 0;

	            //if (logger.isDebugEnabled()) {
//	                logger.debug("Found a total of " + recordCount + " records");
//	            }
	            params.put("totalRecords", recordCount);
	        }

	        return new JSONTableRepresentation(table, null, params, MediaType.APPLICATION_JSON).getText();
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
	  
	  protected String getResourceIdsWhereClause(final List<String> resourceIds) {
	        return getResourceIdsWhereClause(resourceIds, "map.xnat_abstractresource_xnat_abstractresource_id", "abst.label");
	    }

	    protected String getResourceIdsWhereClause(final List<String> resourceIds, final String idKey) {
	        return getResourceIdsWhereClause(resourceIds, idKey, "abst.label");
	    }
	
	    protected String getResourceIdsWhereClause(final List<String> resourceIds, final String idKey, final String labelKey) {
	        // Numeric resource IDs are those that contain only digits.
	        final List<String> numericIds = Lists.newArrayList(Iterables.filter(resourceIds, new Predicate<String>() {
	            @Override
	            public boolean apply(@Nullable final String resourceId) {
	                return StringUtils.isNumeric(resourceId);
	            }
	        }));
	        // Text resource IDs are those that are not the literal value "NULL". This includes the numeric resource IDs.
	        final List<String> textIds = Lists.newArrayList(Iterables.filter(resourceIds, new Predicate<String>() {
	            @Override
	            public boolean apply(@Nullable final String resourceId) {
	                return !StringUtils.equalsIgnoreCase("NULL", resourceId);
	            }
	        }));
	        // We can detect the literal value "NULL" implicitly, because it would have been filtered out of the text resource IDs.
	        final boolean hasNull = resourceIds.size() > textIds.size();
	        final boolean hasNumerics = !numericIds.isEmpty();
	        final boolean hasTexts = !textIds.isEmpty();

	        final StringBuilder whereClause = new StringBuilder();
	        if (hasNumerics) {
	            whereClause.append(idKey).append(" IN (").append(StringUtils.join(numericIds, ", ")).append(")");
	        }
	        if (hasNumerics && hasTexts) {
	            whereClause.append(" OR ");
	        }
	        if (hasTexts) {
	            whereClause.append(labelKey).append(" IN ('").append(StringUtils.join(textIds, "', '")).append("')");
	        }
	        if ((hasNumerics || hasTexts) && hasNull) {
	            whereClause.append(" OR ");
	        }
	        if (hasNull) {
	            whereClause.append(labelKey).append(" IS NULL");
	        }
	        return whereClause.toString();
	    }
	public XFTTable loadCatalogs(final List<String> resourceIds, final boolean includeURI, final boolean allowAll, UserI user) throws Exception {
        checkResourceIDs(resourceIds);

        final StringBuilder query = new StringBuilder();
        final boolean hasResourceIds = resourceIds != null && !resourceIds.isEmpty();
        final boolean isInResource = StringUtils.equalsIgnoreCase(type, "in");

        if (!recons.isEmpty()) {
            security = assesseds.get(0);
            parent = recons.get(0);
            final List<Integer> reconIds = Lists.transform(recons, new Function<XnatReconstructedimagedata, Integer>() {
                @Override
                public Integer apply(final XnatReconstructedimagedata recon) {
                    return recon.getXnatReconstructedimagedataId();
                }
            });
            if (isInResource) {
                xmlPath = "xnat:reconstructedImageData/in/file";
                query.append(STARTER_FIELDS);
                query.append(", 'reconstructions'::TEXT AS category, recon.id::TEXT AS cat_id, recon.type::TEXT AS cat_desc");
                if (includeURI) {
                    query.append(",'/experiments/' || recon.image_session_id || '/reconstructions/' || recon.id || '/in' || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
                }
                query.append(" FROM recon_in_resource map LEFT JOIN xnat_reconstructedimagedata recon ON map.xnat_reconstructedimagedata_xnat_reconstructedimagedata_id=recon.xnat_reconstructedimagedata_id LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id WHERE xnat_reconstructedimagedata_xnat_reconstructedimagedata_id IN ('");
                query.append(StringUtils.join(reconIds, "', '"));
                query.append("') ");
                if (hasResourceIds) {
                    query.append(" AND (").append(getResourceIdsWhereClause(resourceIds)).append(")");
                }
            } else {
                xmlPath = "xnat:reconstructedImageData/out/file";
                query.append(STARTER_FIELDS);
                query.append(", 'reconstructions'::TEXT AS category, recon.id::TEXT AS cat_id, recon.type::TEXT AS cat_desc");
                if (includeURI) {
                    query.append(",'/experiments/' || recon.image_session_id || '/reconstructions/' || recon.id || '/out' || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
                }
                query.append(" FROM recon_out_resource map LEFT JOIN xnat_reconstructedimagedata recon ON map.xnat_reconstructedimagedata_xnat_reconstructedimagedata_id=recon.xnat_reconstructedimagedata_id LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id WHERE xnat_reconstructedimagedata_xnat_reconstructedimagedata_id IN ('");
                query.append(StringUtils.join(reconIds, "', '"));
                query.append("') ");
                if (hasResourceIds) {
                    query.append(" AND (").append(getResourceIdsWhereClause(resourceIds)).append(")");
                }
            }
        } else if (!scans.isEmpty()) {
            security = assesseds.get(0);
            parent = scans.get(0);
            final List<Integer> scanIds = Lists.transform(scans, new Function<XnatImagescandata, Integer>() {
                @Override
                public Integer apply(final XnatImagescandata scan) {
                    return scan.getXnatImagescandataId();
                }
            });
            xmlPath = "xnat:imageScanData/file";
            query.append(STARTER_FIELDS);
            query.append(", 'scans'::TEXT AS category, scan.id::TEXT AS cat_id, scan.type::TEXT AS cat_desc");
            if (includeURI) {
                query.append(",'/experiments/' || scan.image_session_id || '/scans/' || scan.id || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
            }
            query.append(" FROM xnat_abstractresource abst LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id LEFT JOIN xnat_imagescandata scan ON abst.xnat_imagescandata_xnat_imagescandata_id=scan.xnat_imagescandata_id WHERE xnat_imagescandata_xnat_imagescandata_id IN ('");
            query.append(StringUtils.join(scanIds, "', '"));
            query.append("') ");
            if (hasResourceIds) {
                query.append(" AND (").append(getResourceIdsWhereClause(resourceIds, "abst.xnat_abstractresource_id")).append(")");
            }
        } else if (!expts.isEmpty()) {
            security = expts.get(0);
            parent = expts.get(0);
            final List<String> experimentIds = Lists.transform(expts, new Function<XnatExperimentdata, String>() {
                @Override
                public String apply(final XnatExperimentdata experiment) {
                    return experiment.getId();
                }
            });
            if (!assesseds.isEmpty()) {
                security = assesseds.get(0);
                if (isInResource) {
                    xmlPath = "xnat:imageAssessorData/in/file";
                    query.append(STARTER_FIELDS);
                    query.append(", 'assessors'::TEXT AS category, expt.id::TEXT AS cat_id, COALESCE(xes.singular,xmeexpt.element_name)::TEXT AS cat_desc");
                    if (includeURI) {
                        query.append(",'/experiments/' || xiad.imagesession_id || '/assessors/' || expt.id || '/in' || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
                    }
                    query.append(" FROM img_assessor_in_resource map LEFT JOIN xnat_experimentdata expt ON map.xnat_imageassessordata_id=expt.id ");
                    if (includeURI) {
                        query.append(" LEFT JOIN xnat_imageassessordata xiad ON expt.id=xiad.id ");
                    }
                    query.append(" LEFT JOIN xdat_meta_element xmeexpt ON expt.extension=xmeexpt.xdat_meta_element_id LEFT JOIN xdat_element_security xes ON xmeexpt.element_name=xes.element_name LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id WHERE map.xnat_imageassessordata_id IN ('");
                    query.append(StringUtils.join(experimentIds, "', '"));
                    query.append("') ");
                    if (hasResourceIds) {
                        query.append(" AND (").append(getResourceIdsWhereClause(resourceIds)).append(")");
                    }
                } else {
                    xmlPath = "xnat:imageAssessorData/out/file";
                    query.append(STARTER_FIELDS);
                    query.append(", 'assessors'::TEXT AS category, expt.id::TEXT AS cat_id, COALESCE(xes.singular,xmeexpt.element_name)::TEXT AS cat_desc");
                    if (includeURI) {
                        query.append(",'/experiments/' || xiad.imagesession_id || '/assessors/' || expt.id || '/out' || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
                    }
                    query.append(" FROM img_assessor_out_resource map LEFT JOIN xnat_experimentdata expt ON map.xnat_imageassessordata_id=expt.id ");
                    if (includeURI) {
                        query.append(" LEFT JOIN xnat_imageassessordata xiad ON expt.id=xiad.id ");
                    }
                    query.append(" LEFT JOIN xdat_meta_element xmeexpt ON expt.extension=xmeexpt.xdat_meta_element_id LEFT JOIN xdat_element_security xes ON xmeexpt.element_name=xes.element_name LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id WHERE map.xnat_imageassessordata_id IN ('");
                    query.append(StringUtils.join(experimentIds, "', '"));
                    query.append("') ");
                    if (hasResourceIds) {
                        query.append(" AND (").append(getResourceIdsWhereClause(resourceIds)).append(")");
                    }
                }
            } else if (allowAll && (isQueryVariableTrue("all") || resourceIds != null)) {
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
            } else {
                xmlPath = "xnat:experimentData/resources/resource";
                // resources
                query.append(STARTER_FIELDS);
                query.append(", 'resources'::TEXT AS category, expt.id::TEXT AS cat_id, ' '::TEXT AS cat_desc");
                if (includeURI) {
                    query.append(",'/experiments/' || map.xnat_experimentdata_id || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
                }
                query.append(" FROM xnat_experimentdata_resource map LEFT JOIN xnat_experimentdata expt ON map.xnat_experimentdata_id=expt.id LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id WHERE xnat_experimentdata_id IN ('");
                query.append(StringUtils.join(experimentIds, "', '"));
                query.append("') ");
                if (hasResourceIds) {
                    query.append(" AND (").append(getResourceIdsWhereClause(resourceIds)).append(")");
                }
            }
        } else if (sub != null) {
            security = sub;
            parent = sub;
            xmlPath = "xnat:subjectData/resources/resource";
            // resources
            query.append(STARTER_FIELDS);
            query.append(", 'resources'::TEXT AS category, NULL::TEXT AS cat_id, ' '::TEXT AS cat_desc");
            if (includeURI) {
                query.append(",'/projects/' || sub.project || '/subjects/' || sub.id || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
            }
            query.append(" FROM xnat_subjectdata_resource map LEFT JOIN xnat_subjectdata sub ON map.xnat_subjectdata_id=sub.id LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id WHERE xnat_subjectdata_id='");
            query.append(sub.getId());
            query.append("'");
            if (hasResourceIds) {
                query.append(" AND (").append(getResourceIdsWhereClause(resourceIds)).append(")");
            }
        } else if (proj != null) {
            security = proj;
            parent = proj;
            xmlPath = "xnat:projectData/resources/resource";
            // resources
            query.append(STARTER_FIELDS);
            query.append(", 'resources'::TEXT AS category, NULL::TEXT AS cat_id, ' '::TEXT AS cat_desc");
            if (includeURI) {
                query.append(",'/projects/' || map.xnat_projectdata_id || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
            }
            query.append(" FROM xnat_projectdata_resource map LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id WHERE xnat_projectdata_id='");
            query.append(proj.getId());
            query.append("'");
            if (hasResourceIds) {
                query.append(" AND (").append(getResourceIdsWhereClause(resourceIds)).append(")");
            }
        } else {
            query.append(STARTER_FIELDS);
            query.append(", 'resources'::TEXT AS category, NULL::TEXT AS cat_id, ' '::TEXT AS cat_desc FROM xnat_abstractresource abst LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id WHERE xnat_abstractresource_id IS NULL");
        }

        final String completedQuery = query.toString();
      //log.debug("Loading catalog for user '{}' using query: {}", user.getUsername(), completedQuery);
        return XFTTable.Execute(completedQuery, user.getDBName(), user.getUsername());
    }
	
	
	
	public boolean isQueryVariableTrue(String key) {
        return isQueryVariableTrueHelper(getQueryVariable(key));
    }

    protected static boolean isQueryVariableTrue(String key, Request request) {
        return isQueryVariableTrueHelper(getQueryVariable(key, request));
    }

    protected static boolean isQueryVariableTrueHelper(final Object queryVariableObj) {
        if (queryVariableObj == null) {
            return false;
        }
        if (queryVariableObj instanceof String) {
            return !(StringUtils.equalsAnyIgnoreCase((String) queryVariableObj, "false", "0"));
        } else {
            return false;
        }
    }
    
    public String getQueryVariable(String key) {
    	 if(key.equals("columns"))
			 return null;
		 return key;
       // return getQueryVariable(key, new Request());
    }
    public static String getQueryVariable(String key, Request request) {
        Form f = getQueryVariableForm(request);
        if (f != null && f.getValuesMap().containsKey(key)) {
            return TurbineUtils.escapeParam(f.getFirstValue(key));
        }
        return null;
    }
    private static Form getQueryVariableForm(Request request) {
    	 Form form = new Form();
		 form.add("format", "json");
		 form.add("accessible","true");
		 form.add("x", "xhr7t78bbwt");
		 return form;
		//return request.getResourceRef().getQueryAsForm();
    }


	private void getProjSubAcceScanAndExperData(UserI user, String assessedId, String scanId) {
		//_catalogService = XDAT.getContextService().getBean(CatalogService.class);
	        if (assessedId != null) {
	            for (String s : XftStringUtils.CommaDelimitedStringToArrayList(assessedId)) {
	                XnatExperimentdata assessed = XnatImagesessiondata.getXnatImagesessiondatasById(s, user, false);

	                if (assessed != null && (proj != null && !assessed.hasProject(proj.getId()))) {
	                    assessed = null;
	                }

	                if (assessed == null && proj != null) {
	                    assessed = XnatImagesessiondata.GetExptByProjectIdentifier(proj.getId(), s,user, false);
	                }

	                if (assessed != null) {
	                    try {
	                        if (assessed.canRead(user)) {
	                            assesseds.add(assessed);
	                        }
	                    } catch (Exception ignored) {
	                    }
	                }
	            }
	        }

	       
	        if (scanId != null && this.assesseds.size() > 0) {

	        	scanId = scanId.replace("[SLASH]", "/");//this is such an ugly hack.  If a slash is included in the scan type and thus in the URL, it breaks the GET command.  Even if it is properly escaped.  So, I'm adding this alternative encoding of slash to allow us to work around the issue.  Hopefully Spring MVC will eliminate it.

	            CriteriaCollection cc = new CriteriaCollection("OR");
	            for (XnatExperimentdata assessed : this.assesseds) {
	                CriteriaCollection subcc = new CriteriaCollection("AND");
	                subcc.addClause("xnat:imageScanData/image_session_ID", assessed
	                        .getId());
	                if (!(scanId.equals("*") || scanId.equals("ALL"))) {
	                    if (!scanId.contains(",")) {
	                        subcc.addClause("xnat:imageScanData/ID", scanId);
	                    } else {
	                        CriteriaCollection subsubcc = new CriteriaCollection("OR");
	                        for (String s : XftStringUtils.CommaDelimitedStringToArrayList(scanId, true)) {
	                            subsubcc.addClause("xnat:imageScanData/ID", s);
	                        }
	                        subcc.add(subsubcc);
	                    }
	                }
	                cc.add(subcc);

	                subcc = new CriteriaCollection("AND");
	                subcc.addClause("xnat:imageScanData/image_session_ID", assessed
	                        .getId());
	                if (!(scanId.equals("*") || scanId.equals("ALL"))) {
	                    if (!scanId.contains(",")) {
	                        if (scanId.equals("NULL")) {
	                            CriteriaCollection subsubcc = new CriteriaCollection("OR");
	                            subsubcc.addClause("xnat:imageScanData/type", "", " IS NULL ", true);
	                            subsubcc.addClause("xnat:imageScanData/type", "");
	                            subcc.add(subsubcc);
	                        } else {
	                            subcc.addClause("xnat:imageScanData/type", scanId.replace("[COMMA]", ","));
	                        }
	                    } else {
	                        CriteriaCollection subsubcc = new CriteriaCollection("OR");
	                        for (String s : XftStringUtils.CommaDelimitedStringToArrayList(scanId, true)) {
	                            if (s.equals("NULL")) {
	                                subsubcc.addClause("xnat:imageScanData/type", "", " IS NULL ", true);
	                                subsubcc.addClause("xnat:imageScanData/type", "");
	                            } else {
	                                subsubcc.addClause("xnat:imageScanData/type", s.replace("[COMMA]", ","));
	                            }
	                        }
	                        subcc.add(subsubcc);
	                    }
	                }
	                cc.add(subcc);
	            }
	            scans = XnatImagescandata.getXnatImagescandatasByField(cc, user,completeDocument);
	        }
	}
	
	private static final String STARTER_FIELDS = "SELECT xnat_abstractresource_id, abst.label, xme.element_name ";
    public static final  String            USER_ACCESSIBLE_ASSESSOR_IDS = "( SELECT * FROM xnat_imageassessordata WHERE id IN (SELECT id " +
                                                              " FROM   (SELECT xea.element_name, " +
                                                              "                xfm.field, " +
                                                              "                xfm.field_value " +
                                                              "         FROM   xdat_user u " +
                                                              "                JOIN xdat_user_groupid map " +
                                                              "                  ON u.xdat_user_id = map.groups_groupid_xdat_user_xdat_user_id " +
                                                              "                JOIN xdat_usergroup gp " +
                                                              "                  ON map.groupid = gp.id " +
                                                              "                JOIN xdat_element_access xea " +
                                                              "                  ON gp.xdat_usergroup_id = xea.xdat_usergroup_xdat_usergroup_id " +
                                                              "                JOIN xdat_field_mapping_set xfms " +
                                                              "                  ON " +
                                                              " xea.xdat_element_access_id = xfms.permissions_allow_set_xdat_elem_xdat_element_access_id " +
                                                              " JOIN xdat_field_mapping xfm " +
                                                              "   ON " +
                                                              " xfms.xdat_field_mapping_set_id = xfm.xdat_field_mapping_set_xdat_field_mapping_set_id " +
                                                              " AND read_element = 1 " +
                                                              " AND field_value != '' " +
                                                              " AND field != '' " +
                                                              " WHERE  u.login = 'guest' " +
                                                              "  UNION " +
                                                              "  SELECT xea.element_name, " +
                                                              "         xfm.field, " +
                                                              "         xfm.field_value " +
                                                              "  FROM   xdat_user_groupid map " +
                                                              "         JOIN xdat_user u ON map.groups_groupid_xdat_user_xdat_user_id = u.xdat_user_id " +
                                                              "         JOIN xdat_usergroup gp " +
                                                              "           ON map.groupid = gp.id " +
                                                              "         JOIN xdat_element_access xea " +
                                                              "           ON gp.xdat_usergroup_id = xea.xdat_usergroup_xdat_usergroup_id " +
                                                              "         JOIN xdat_field_mapping_set xfms " +
                                                              "           ON " +
                                                              " xea.xdat_element_access_id = xfms.permissions_allow_set_xdat_elem_xdat_element_access_id " +
                                                              " JOIN xdat_field_mapping xfm " +
                                                              "   ON " +
                                                              " xfms.xdat_field_mapping_set_id = xfm.xdat_field_mapping_set_xdat_field_mapping_set_id " +
                                                              " AND read_element = 1 " +
                                                              " AND field_value != '' " +
                                                              " AND field != '' " +
                                                              " WHERE u.login = '${username}' " +
                                                              " OR xfm.field_value IN (SELECT proj.id " +
                                                              "         FROM   xnat_projectdata proj " +
                                                              "         JOIN (SELECT field_value, " +
                                                              "                        read_element AS project_read " +
                                                              "                                FROM   xdat_element_access " +
                                                              "                                ea " +
                                                              "                                LEFT JOIN xdat_field_mapping_set fms " +
                                                              "                                ON ea.xdat_element_access_id = " +
                                                              "                                fms.permissions_allow_set_xdat_elem_xdat_element_access_id " +
                                                              "                                LEFT JOIN xdat_user u " +
                                                              "                                ON ea.xdat_user_xdat_user_id = u.xdat_user_id " +
                                                              "                                LEFT JOIN xdat_field_mapping fm " +
                                                              "                                ON fms.xdat_field_mapping_set_id = " +
                                                              "                                fm.xdat_field_mapping_set_xdat_field_mapping_set_id " +
                                                              "                                WHERE  login = 'guest' " +
                                                              "                                AND read_element = 1 " +
                                                              "                                AND element_name = 'xnat:projectData')project_read " +
                                                              " ON proj.id = project_read.field_value " +
                                                              " JOIN (SELECT field_value, " +
                                                              "       read_element AS subject_read " +
                                                              "               FROM   xdat_element_access ea " +
                                                              "               LEFT JOIN xdat_field_mapping_set fms " +
                                                              "               ON ea.xdat_element_access_id = " +
                                                              "               fms.permissions_allow_set_xdat_elem_xdat_element_access_id " +
                                                              "               LEFT JOIN xdat_user u " +
                                                              "               ON ea.xdat_user_xdat_user_id = u.xdat_user_id " +
                                                              "               LEFT JOIN xdat_field_mapping fm " +
                                                              "               ON fms.xdat_field_mapping_set_id = " +
                                                              "               fm.xdat_field_mapping_set_xdat_field_mapping_set_id " +
                                                              "               WHERE  login = 'guest' " +
                                                              "               AND read_element = 1 " +
                                                              "               AND field = 'xnat:subjectData/project')subject_read " +
                                                              " ON proj.id = subject_read.field_value)) perms " +
                                                              " INNER JOIN (SELECT iad.id, " +
                                                              "                    element_name " +
                                                              "                    || '/project' AS field, " +
                                                              "                    expt.project, " +
                                                              "                    expt.label " +
                                                              "             FROM   xnat_imageassessordata iad " +
                                                              "                    LEFT JOIN xnat_experimentdata expt " +
                                                              "                           ON iad.id = expt.id " +
                                                              "                    LEFT JOIN xdat_meta_element xme " +
                                                              "                           ON expt.extension = xme.xdat_meta_element_id " +
                                                              "             WHERE  iad.imagesession_id IN ('${sessionIds}') " +
                                                              "             UNION " +
                                                              "             SELECT expt.id, " +
                                                              "                    xme.element_name " +
                                                              "                    || '/sharing/share/project', " +
                                                              "                    shr.project, " +
                                                              "                    shr.label " +
                                                              "             FROM   xnat_experimentdata_share shr " +
                                                              "                    LEFT JOIN xnat_experimentdata expt " +
                                                              "                           ON expt.id = shr.sharing_share_xnat_experimentda_id " +
                                                              "                    LEFT JOIN xdat_meta_element xme " +
                                                              "                           ON expt.extension = xme.xdat_meta_element_id) expts " +
                                                              "         ON perms.field = expts.field " +
                                                              "            AND perms.field_value IN (expts.project, '*') " +
                                                              " ORDER  BY element_name) )";

}
