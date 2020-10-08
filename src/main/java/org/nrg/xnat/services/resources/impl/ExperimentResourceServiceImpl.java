/**
 * 
 */
package org.nrg.xnat.services.resources.impl;

import static org.nrg.xnat.utils.CatalogUtils.ABSOLUTE_PATH;
import static org.nrg.xnat.utils.CatalogUtils.LOCATOR;
import static org.nrg.xnat.utils.CatalogUtils.PROJECT_PATH;
import static org.nrg.xnat.utils.CatalogUtils.URI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.bean.CatCatalogBean;
import org.nrg.xdat.bean.CatEntryBean;
import org.nrg.xdat.model.CatEntryI;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xft.XFTTable;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xnat.restlet.representations.ZipRepresentation;
import org.nrg.xnat.restlet.resources.SecureResource;
import org.nrg.xnat.services.resources.ExperimentResourceService;
import org.nrg.xnat.services.resources.util.BeanRepresentationUtil;
import org.nrg.xnat.services.resources.util.ItemXMLRepresentationUtil;
import org.nrg.xnat.services.resources.util.JSONTableRepresentationUtil;
import org.nrg.xnat.services.resources.util.ResourceXapiUtil;
import org.nrg.xnat.services.resources.util.XNATCatalogTemplateUtil;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.nrg.xnat.utils.CatalogUtils;
import org.nrg.xnat.utils.CatalogUtils.CatEntryFilterI;
import org.nrg.xnat.utils.CatalogUtils.CatalogData;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author afour
 *
 */
@Service
@Slf4j
public class ExperimentResourceServiceImpl extends XNATCatalogTemplateUtil implements ExperimentResourceService {
	
	private final static Logger logger = LoggerFactory.getLogger(ExperimentResourceServiceImpl.class);

	private XFTTable catalogs = null;
	//private XnatProjectdata proj = null;

	

	@Override
	public String getExperimentResource(String experimentId, String resourceId) throws IOException {
		if (experimentId != null && resourceId != null) {
			return getExperimentResourceByIds(experimentId, resourceId);
		} else if (experimentId != null) {
			return getExperimentById(experimentId);
		}
		return "invalid resource";
	}

	private String getExperimentById(String experimentId) throws IOException {
	
		setProjSubAssessExperScanAndReconData(null, null,  null, experimentId,  null,  null);
	
		XFTTable table = null;
		if (experimentId != null) {
			expt = XnatExperimentdata.getXnatExperimentdatasById(experimentId, getUser(), false);
		}
		if (expt != null) {
			try {
				table = loadCatalogs(null, false, true);// isQueryVariableTrue("all") -> true
			} catch (Exception e) {
				logger.error("", e);
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

	private String getExperimentResourceByIds(String experimentId, String resourceId) throws IOException {
		setProjSubAssessExperScanAndReconData(null, null,  null, experimentId,  null,  null);
		List<String> resourceIds = new ArrayList<>();
		List<XnatAbstractresource> resources = new ArrayList<>();
		XFTTable table = null;
		resourceIds.add(resourceId);
		getAllMatches(resourceIds, resources);

		if (experimentId != null) {
			expt = XnatExperimentdata.getXnatExperimentdatasById(experimentId, getUser(), false);
		}
		if (expt != null) {
			try {
				table = loadCatalogs(resourceIds, false, true);// isQueryVariableTrue("all") -> true
			} catch (Exception e) {
				logger.error("", e);
			}
		}

		if (resources.size() == 1) {
			final XnatAbstractresource resource = resources.get(0);
			try {
				if (proj == null) {
					if (parent.getItem().instanceOf("xnat:experimentData")) {
						proj = ((XnatExperimentdata) parent).getPrimaryProject(false);
					}
				}

				if (resource.getItem().instanceOf("xnat:resourceCatalog")) {
					try {
						CatalogUtils.CatalogData catalogData = CatalogUtils.CatalogData.getOrCreateAndClean(
								proj.getRootArchivePath(), ((XnatResourcecatalog) resource), false, proj.getId()); // isQueryVariableTrue("includeRootPath")
						return new BeanRepresentationUtil(catalogData.catBean).getText();
					} catch (ServerException e) {
						log.error("Unable to find catalog file: ", e.getMessage());

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
	public String getExperimentResourceFiles(String experimentId, String resourceId) throws ClientException, IOException {
		
		setProjSubAssessExperScanAndReconData(null, null,  null, experimentId,  null,  null);
		
		setResourceXNATCatalogTemplateUtil(true);
		
		try {
            if (proj == null) {
                //setting project as primary project, or shared project
                //this only works because the absolute paths are stored in the database for each resource, so the actual project path isn't used.
                if (parent != null && parent.getItem().instanceOf("xnat:experimentData")) {
                    proj = ((XnatExperimentdata) parent).getPrimaryProject(false);
                    // Per FogBugz 4746, prevent NPE when user doesn't have access to resource (MRH)
                    // Check access through shared project when user doesn't have access to primary project
                    if (proj == null) {
                        proj = (XnatProjectdata) ((XnatExperimentdata) parent).getFirstProject();
                    }
                } else if (security != null && security.getItem().instanceOf("xnat:experimentData")) {
                    proj = ((XnatExperimentdata) security).getPrimaryProject(false);
                    // Per FogBugz 4746, ....
                    if (proj == null) {
                        proj = (XnatProjectdata) ((XnatExperimentdata) security).getFirstProject();
                    }
                } else if (security != null && security.getItem().instanceOf("xnat:subjectData")) {
                    proj = ((XnatSubjectdata) security).getPrimaryProject(false);
                    // Per FogBugz 4746, ....
                    if (proj == null) {
                        proj = (XnatProjectdata) ((XnatSubjectdata) security).getFirstProject();
                    }
                } else if (security != null && security.getItem().instanceOf("xnat:projectData")) {
                    proj = (XnatProjectdata) security;
                }
            }
            List<XnatAbstractresource> resources = new ArrayList<>();
            //final List<XnatAbstractresource> resources = getResources();
            try {
                // Check project access before iterating through all of the resources.
                if (proj == null || Permissions.canReadProject(getUser(), proj.getId())) {
                    //all catalogs
                    getCatalogs().resetRowCursor();
                    for (Hashtable<String, Object> rowHash : getCatalogs().rowHashs()) {
                        final XnatAbstractresource resource = XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(rowHash.get("xnat_abstractresource_id"), getUser(), false);
                        if (rowHash.containsKey("resource_path")) {
                            resource.setBaseURI((String) rowHash.get("resource_path"));
                        }
                        resources.add(resource);
                    }
                }
            } catch (Exception e) {
                log.error("Exception checking whether user has project access.", e);
            }

           return handleMultipleCatalogs(resources);
        } catch (ElementNotFoundException e) {
            return "";
        }
	}

	private String handleMultipleCatalogs(List<XnatAbstractresource> resources) throws IOException, ElementNotFoundException {
		final boolean           isZip = false;
	        final Map<String, File> fileList = new HashMap<>();
	        final XFTTable          table    = new XFTTable();
	        final String queryVariable=null;
	        final String[] headers = CatalogUtils.FILE_HEADERS.clone();
	        String locator = null;
	        if (isZip || !StringUtils.equalsAnyIgnoreCase(queryVariable, ABSOLUTE_PATH, PROJECT_PATH)) {
	            locator = URI;
	        } 
	        table.initTable(headers);

	        final String  baseURI     = getBaseURI();
	        final CatEntryFilterI entryFilter = buildFilter();
	        File file  = null;

	        final String projectId = proj.getId();
	        final String rootArchivePath = proj.getRootArchivePath();
	        for (final XnatAbstractresource temp : resources) {
	            if (temp.getItem().instanceOf("xnat:resourceCatalog")) {
	                final boolean             includeRoot = isQueryVariableTrue("includeRootPath");
	                final XnatResourcecatalog catResource = (XnatResourcecatalog) temp;
	                final CatalogData         catalogData;
	                try {
	                    catalogData = CatalogData.getOrCreateAndClean(rootArchivePath, catResource, includeRoot, projectId);
	                } catch (ServerException e) {
	                    throw new ElementNotFoundException("xnat:resourceCatalog " + catResource.getUri());
	                }
	                final CatCatalogBean      cat         = catalogData.catBean;
	                final String              parentPath  = catalogData.catPath;

	                table.insertRows(CatalogUtils.getEntryDetails(cat, parentPath, (catResource.getBaseURI() != null) ? catResource.getBaseURI() + "/files" : baseURI + "/resources/" + catResource.getXnatAbstractresourceId() + "/files", catResource, isZip , entryFilter, proj, locator));
	            } 
	        }
	            final Pair<Hashtable<String, Object>, Map<String, Map<String, String>>> parametersAndProperties = getDefaultParametersAndColumnProperties();
	            parametersAndProperties.getRight().get(URI).put("serverRoot", "");
	            return representTable(table, parametersAndProperties.getLeft(), parametersAndProperties.getRight(), getSessionMaps());
	}
	
	 private CatEntryFilterI buildFilter() {
        final String[] contents    = getQueryVariables("file_content");
        final String[] formats     = getQueryVariables("file_format");
        final boolean  hasContents = !ArrayUtils.isEmpty(contents);
        final boolean  hasFormats  = !ArrayUtils.isEmpty(formats);
        if (!hasContents && !hasFormats) {
            return null;
        }
        return new CatEntryFilterI() {
            public boolean accept(final CatEntryI entry) {
                if (hasFormats && ((entry.getFormat() == null && !ArrayUtils.contains(formats, "NULL")) || !ArrayUtils.contains(formats, entry.getFormat()))) {
                    return false;
                }
                if (hasContents) {
                    return entry.getContent() == null ? ArrayUtils.contains(contents, "NULL") : ArrayUtils.contains(contents, entry.getContent());
                }
                return true;
            }
        };
    }

	private String[] getQueryVariables(String string) {
		return null;
	}

	private String representTable(XFTTable table,final Hashtable<String, Object> parameters ,Map<String, Map<String, String>> columnProperties, Map<String, String> sessionMaps) throws IOException {
		return new JSONTableRepresentationUtil(table, columnProperties, parameters).getText();
	}


	private Pair<Hashtable<String, Object>, Map<String, Map<String, String>>> getDefaultParametersAndColumnProperties() {
        final Hashtable<String, Object> parameters = new Hashtable<>();
        parameters.put("title", "Files");
        final Map<String, Map<String, String>> columnProperties = new Hashtable<>();
        columnProperties.put(URI, new Hashtable<String, String>());
        return ImmutablePair.of(parameters, columnProperties);
    }
	
    private Map<String, String> getSessionMaps() {
        final Map<String, String> sessionIds = new Hashtable<>();
        // Check if the session is an assessor to an "assessed" session
        if (!assesseds.isEmpty()) {
            // Check if the session containing the assessor has an "ASSESSORS" directory.
            // This signifies that the directory structure is based on a "modern" version of XNAT.
            if (!expts.isEmpty() && new File(assesseds.get(0).getSessionDir(), "ASSESSORS").isDirectory()) {
                for (final XnatExperimentdata session : expts) {
                    sessionIds.put(session.getId(), session.getArchiveDirectoryName());
                }
                return sessionIds;
            }
            //IOWA customization: to include project and subject in path
            final boolean projectIncludedInPath = isQueryVariableTrue("projectIncludedInPath");
            final boolean subjectIncludedInPath = isQueryVariableTrue("subjectIncludedInPath");
            for (final XnatExperimentdata session : assesseds) {
                final StringBuilder sessionUri = new StringBuilder();
                if (projectIncludedInPath) {
                    sessionUri.append(session.getProject()).append("/");
                }
                if (subjectIncludedInPath) {
                    if (session instanceof XnatImagesessiondata) {
                        final XnatSubjectdata subject = XnatSubjectdata.getXnatSubjectdatasById(((XnatImagesessiondata) session).getSubjectId(), getUser(), false);
                        sessionUri.append(subject.getLabel()).append("/");
                    }
                }
                sessionUri.append(session.getArchiveDirectoryName());
                sessionIds.put(session.getId(), sessionUri.toString());
            }
            return sessionIds;
        }
        if (!expts.isEmpty()) {
            for (final XnatExperimentdata session : expts) {
                sessionIds.put(session.getId(), session.getArchiveDirectoryName());
            }
            return sessionIds;
        }
        if (sub != null) {
            sessionIds.put(sub.getId(), sub.getArchiveDirectoryName());
            return sessionIds;
        }
        if (proj != null) {
            sessionIds.put(proj.getId(), proj.getId());
            return sessionIds;
        }

        return sessionIds;
    }
}
