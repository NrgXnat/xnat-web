package org.nrg.xnat.services.files.impl;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ClientException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.model.CatEntryI;
import org.nrg.xdat.om.WrkWorkflowdata;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImageassessordata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xft.XFTItem;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.XftItemEventI;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.ActionNameAbsent;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.IDAbsent;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.JustificationAbsent;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.FileResourceWrapper;
import org.nrg.xnat.helpers.resource.XnatResourceInfo;
import org.nrg.xnat.helpers.resource.direct.ResourceModifierA;
import org.nrg.xnat.helpers.resource.direct.ResourceModifierA.UpdateMeta;
import org.nrg.xnat.model.util.XNATCatalogTemplateUtil;
import org.nrg.xnat.presentation.ChangeSummaryBuilderA;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.nrg.xnat.services.cache.UserProjectCache;
import org.nrg.xnat.services.files.FileService;
import org.nrg.xnat.services.messaging.file.MoveStoredFileRequest;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.nrg.xnat.utils.CatalogUtils;
import org.nrg.xnat.utils.CatalogUtils.CatalogData;
import org.nrg.xnat.utils.WorkflowUtils;
import org.restlet.data.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileServiceImpl extends XNATCatalogTemplateUtil implements FileService {
	
	@Autowired
	public FileServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}
	
	@Override
	public List<XnatResourcecatalog> findByProject(UserI user, String projectId) throws DataFormatException {
		if(Objects.isNull(projectId))
			throw new DataFormatException("Projectid is missing");
		
		return _template.query(PROJECT_QUERY + BY_ID_WHERE_PROJECT, new MapSqlParameterSource("projectId", projectId), new FileRowMapper(user));
	}
	
	@Override
	public List<XnatResourcecatalog> findBySubject(UserI user, String subjectId) throws DataFormatException {
		if(Objects.isNull(subjectId))
			throw new DataFormatException("subjectId is missing");
		
		return _template.query(SUBJECT_QUERY + BY_WHERE + BY_ID_WHERE_SUBJECT, new MapSqlParameterSource("subjectId", subjectId), new FileRowMapper(user));
	}
	
	@Override
	public List<XnatResourcecatalog> findByProjectAndSubject(UserI user, String projectId, String subjectId) throws DataFormatException {
		if(Objects.isNull(projectId) && Objects.isNull(subjectId) )
			throw new DataFormatException("either projectId or subjectId is missing");
		
		return _template.query(SUBJECT_QUERY + BY_ID_WHERE_PROJ + AND_WHERE + BY_ID_WHERE_SUBJECT , new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId), new FileRowMapper(user));
	}
	
	@Override
	public List<XnatResourcecatalog> findByProjectAndResource(UserI user, String projectId, Integer resourceId) throws DataFormatException {
		if(Objects.isNull(projectId) && Objects.isNull(resourceId) )
			throw new DataFormatException("either projectId or resourceId is missing");
		return _template.query(PROJECT_QUERY + BY_ID_WHERE_PROJ_AND_RESOURCE, new MapSqlParameterSource("projectId", projectId).addValue("resourceId", resourceId), new FileRowMapper(user));
	}
	
	@Override
	public List<XnatResourcecatalog> findBySubjectAndResource(UserI user, String subjectId, Integer resourceId) throws DataFormatException {
		if(Objects.isNull(subjectId) && Objects.isNull(resourceId) )
			throw new DataFormatException("either subjectId or resourceId is missing");
		
		return _template.query(SUBJECT_RESOURCE_QUERY + BY_ID_WHERE_SUBJ_AND_RESOURCE, new MapSqlParameterSource("subjectId", subjectId).addValue("resourceId", resourceId), new FileRowMapper(user));
	}
	
	@Override
	public List<XnatResourcecatalog> findByExperimentAndAssessors(UserI user, String experimentId, String assessorId) throws DataFormatException {
		if(Objects.isNull(experimentId) && Objects.isNull(assessorId) )
			throw new DataFormatException("either experimentId or assessorId is missing");
		
		return _template.query(EXPERIMENT_ASSESSER_QUERY + BY_ID_WHERE_EXP_AND_ASSESSER, new MapSqlParameterSource("experimentId", experimentId).addValue("assessorId", assessorId), new FileRowMapper(user));
	}
	
	@Override
	public List<XnatResourcecatalog> findByIdAndProjectAndSubjectAndExperimentAndAssessors(UserI user,String projectId, String subjectId, String experimentId, String assessedId) throws DataFormatException {
		if(Objects.isNull(projectId) && Objects.isNull(subjectId)&& Objects.isNull(experimentId) && Objects.isNull(assessedId) )
			throw new DataFormatException("either projectId or subjectId or experimentId or assessedId  is missing");
		
		return _template.query(PRO_SUB_EXP_ASS_QUERY + BY_WHERE_PRO_SUB_EXP_ASS  , new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId).addValue("experimentId", experimentId).addValue("assessedId", assessedId), new FileRowMapper(user));
	}
	
	@Override
	public List<XnatResourcecatalog> findByExperiment(UserI user, String experimentId) throws DataFormatException {
		if(Objects.isNull(experimentId))
			throw new DataFormatException("experimentId is missing");
		
		return _template.query(EXP_FILE_QUERY, new MapSqlParameterSource("experimentId", experimentId), new FileRowMapper(user));
	}

	@Override
	public List<XnatResourcecatalog> findByExperimentAndResource(UserI user, String experimentId, Integer resourceId) throws DataFormatException {
		if(Objects.isNull(experimentId) && Objects.isNull(resourceId) )
			throw new DataFormatException("either experimentId or resourceId is missing");
		
		return _template.query(EXP_RESOURCE_QUERY, new MapSqlParameterSource("experimentId", experimentId).addValue("resourceId", resourceId), new FileRowMapper(user));
	}
	
	
	@Override
	public void deleteResourceFile(UserI user, String projectId,String subjectId, String experimentId, String assessorId, String scanId, String type,String resourceId) throws Exception {
		proj = null;
		sub = null;
		expts = new ArrayList<>();
		assesseds = new ArrayList<>();
		scans = new ArrayList<>();
		// step 1: get proj/sub/assesseds/expts/scans data
		if(Objects.nonNull(projectId))
			proj = getXnatProjectdata(projectId, user);
		if(Objects.nonNull(subjectId))
			sub = getXnatSubjectdata(subjectId, user, proj);
		if (Objects.nonNull(assessorId)) 
			assesseds = getXnatAssessordata(assessorId, user, proj);
		if(Objects.nonNull(experimentId)) 
			expts = getXnatExperimentData(experimentId, user,assesseds, type);
		if (Objects.nonNull(scanId)) 
			scans = getXnatImageScanData(scanId, user, assesseds);

		// step 2: set resource_ids
		_resourceIds = setResourcesIds(resourceId, user, false);

		// Step 3: get resource data
		XnatAbstractresource resource = null;
		
		resource= getResourceData(user, _resourceIds);

		// Step 4: validate resource data
		validateResource(user, resource);

		// Step 5: validate project data
		verifyProjIsNull();

		// Step 6: get catalogData
		final CatalogUtils.CatalogData catalogData = CatalogUtils.CatalogData.getOrCreate(proj.getRootArchivePath(),(XnatResourcecatalog) resource, proj.getId());

		// Step 7: get  cat Enttry
		final Collection<CatEntryI> entries = CatalogUtils.findCatEntriesWithinPath(filePath, catalogData);

		if (entries.isEmpty())
			throw new NotFoundException("Resource file not found");

		// Step 8: get or create workflow data
		PersistentWorkflowI work = WorkflowUtils.getOrCreateWorkflowData(getEventId(), user, security.getItem(),newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.REMOVE_FILE));

		// Step 9: delete resource file
		deleteResourceFiles(work, catalogData, entries, user);

	}
	
	private void deleteResourceFiles(PersistentWorkflowI work, CatalogData catalogData, Collection<CatEntryI> entries, UserI user) throws Exception {
		try {
            long catSize = catalogData.catRes.getFileSize() == null ? 0 : (Long) catalogData.catRes.getFileSize();
            Map<CatEntryI, File> historyMap = new HashMap<>();
            for (CatEntryI entry : entries) {
                CatalogUtils.CatalogEntryPathInfo info = new CatalogUtils.CatalogEntryPathInfo(entry,
                        catalogData.catPath);
                historyMap.put(entry, new File(info.entryPathDest));
                catSize -= CatalogUtils.getCatalogEntrySize(entry);
            }

            int nremoved = entries.size();
            int fileCount = (catalogData.catRes.getFileCount() == null) ? 0 :
                    catalogData.catRes.getFileCount() - nremoved;

            EventMetaI ci = work.buildEvent();
            Map<String, Map<String, Integer>> auditSummary = new HashMap<>();
            CatalogUtils.addAuditEntry(auditSummary, Integer.parseInt(ci.getEventId().toString()),
                    Calendar.getInstance().getTime(), ChangeSummaryBuilderA.REMOVED, nremoved);

            // Perform remove on the catalog bean
            catalogData.catBean.getEntries_entry().removeAll(entries);

            // Write updated bean to the catalog, maintain history if appropriate, and remove files if requested
            CatalogUtils.saveUpdatedCatalog(catalogData, auditSummary, catSize, fileCount, ci, user,
                    historyMap, !isQueryVariableFalse("removeFiles"));

            if (StringUtils.equals(XnatProjectdata.SCHEMA_ELEMENT_NAME, parent.getXSIType())) {
                XDAT.triggerXftItemEvent(XnatProjectdata.SCHEMA_ELEMENT_NAME, parent.getStringProperty("ID"),
                        XftItemEventI.DELETE);
            }
        } finally {
            WorkflowUtils.complete(work, work.buildEvent());
        }
		
	}

	private void validateResource(UserI user, XnatAbstractresource resource) throws Exception {
		if (resource == null || parent == null || security == null) {
            throw new ClientException(Status.CLIENT_ERROR_BAD_REQUEST,
                    "Unable to determine resource, parent, or security.");
        }

        if (!Permissions.canDelete(user,security)) {
            throw new ClientException(Status.CLIENT_ERROR_FORBIDDEN,
                    "User account doesn't have permission to modify this session.");
        }
        XFTItem item = resource.getItem();
        if (item.isLocked() || !item.isActive() && !item.isQuarantine()) {
            //cannot modify it if it isn't active
            throw new ClientException(Status.CLIENT_ERROR_FORBIDDEN,
                    "Item locked or is not active and not quarantined");
        }

        if (!(resource instanceof XnatResourcecatalog)) {
            throw new ClientException(Status.CLIENT_ERROR_BAD_REQUEST,
                    "File is not an instance of XnatResourcecatalog. Delete operation not supported.");
        }
		
	}

	private void verifyProjIsNull() throws ElementNotFoundException {
		 if (proj == null) {
             if (parent.getItem().instanceOf("xnat:experimentData")) {
                 proj = ((XnatExperimentdata) parent).getPrimaryProject(false);
             } else if (security.getItem().instanceOf("xnat:experimentData")) {
                 proj = ((XnatExperimentdata) security).getPrimaryProject(false);
             }
         }
	}
	
	@Override
	public Integer createResourceFile(UserI user, XnatResourceInfo xnatResourceInfo, String projectId, String resourceId) throws Exception{
		// step 1: get project data
		if (Objects.nonNull(projectId))
			proj = getXnatProjectdata(projectId, user);
		
		// step 2: set resource_ids
		 _resourceIds = setResourcesIds(resourceId, user, false);
		 
		// Step 3: get resource data
			XnatAbstractresource xnatAbstractresource = null;

			xnatAbstractresource = getResourceData(user, _resourceIds);
		
		// step 4:
			if (parent != null && security != null) {
				if (Permissions.canEdit(user, security)) {
					verifyProjectIsNull();
					
					final Object resourceIdentifier = verifyResourceIsNull(xnatAbstractresource);
					
					final boolean overwrite = true; // HC
					final boolean extract = true; // HC

					PersistentWorkflowI workflow = PersistentWorkflowUtils.getWorkflowByEventId(user, getEventId());

					workflow = verifyAndGetWorkflow(workflow, xnatAbstractresource, user);

					final boolean skipUpdateStats = false; // HC

					boolean isNew = false;

					workflow = getWorkflow(workflow, isNew, skipUpdateStats, user);

					final EventMetaI eventMeta = getEventMetaI(workflow, user);

					final UpdateMeta updateMeta = new UpdateMeta(eventMeta, !(skipUpdateStats));
					
					workflow = uploadFile(xnatResourceInfo,overwrite, updateMeta, user, projectId, workflow, resourceIdentifier, extract, isNew);
				
					if (StringUtils.isBlank(reference) && workflow != null && isNew)
						WorkflowUtils.complete(workflow, eventMeta);
				}
			}
			return null;
	}

	private PersistentWorkflowI uploadFile(XnatResourceInfo xnatResourceInfo, boolean overwrite, UpdateMeta updateMeta, UserI user, String projectId, PersistentWorkflowI workflow, Object resourceIdentifier, boolean extract, boolean isNew) {
		try {
			 final List<FileWriterWrapperI> writers = getFileWriters(xnatResourceInfo);
			 if (writers == null || writers.isEmpty()) {
                  if (xnatResourceInfo.getFileSize() == 0) {
                  	throw new DataFormatException("You tried to upload file " + xnatResourceInfo.getFileName() + " to this service, but didn't provide any data (found request entity size of 0). Please check the format of your service request.");
                  } else {
                  	throw new DataFormatException("You tried to upload file " + xnatResourceInfo.getFileName() + " a payload of " + CatalogUtils.formatSize(xnatResourceInfo.getFileSize()) + " to this service, but didn't provide any data. If you think you sent data to upload, you can try to upload file " + xnatResourceInfo.getFileName() + " with the query-string parameter inbody=true or use multipart/form-data encoding.");
                  }
              }

			final ResourceModifierA resourceModifier = buildResourceModifier(overwrite, updateMeta, user);
			if (!async || StringUtils.isBlank(reference)) {
				filePath = xnatResourceInfo.getRename();
				type = "out";
                  final List<String> duplicates = resourceModifier.addFile(writers, resourceIdentifier, type, filePath, buildResourceInfo(updateMeta, xnatResourceInfo, user), extract);
                  if (!overwrite && duplicates.size() > 0) {
                  	 isNew = false;
                  	throw new ResourceAlreadyExistsException("duplicate file", "");
                  } else {
                  }

				if (StringUtils.equals(XnatProjectdata.SCHEMA_ELEMENT_NAME, parent.getXSIType())) {
					final UserProjectCache cache = XDAT.getContextService().getBeanSafely(UserProjectCache.class);
					if (cache != null) {
						cache.clearProjectCacheEntry(projectId);
					}
					XDAT.triggerXftItemEvent(proj, XftItemEventI.UPDATE);
				}
			} else {
				if (workflow == null) {
					throw new Exception("Unexpected null workflow");
				}

				workflow.setStatus(PersistentWorkflowUtils.QUEUED);
				WorkflowUtils.save(workflow, workflow.buildEvent());

				final MoveStoredFileRequest request;
				if (StringUtils.equals(XnatProjectdata.SCHEMA_ELEMENT_NAME, parent.getXSIType())) {
					request = new MoveStoredFileRequest(resourceModifier, resourceIdentifier, writers, user, workflow.getWorkflowId(), delete, notifyList, type, filePath, buildResourceInfo(updateMeta, xnatResourceInfo, user), extract, projectId);
				} else {
					request = new MoveStoredFileRequest(resourceModifier, resourceIdentifier, writers, user, workflow.getWorkflowId(), delete, notifyList, type, filePath, buildResourceInfo(updateMeta, xnatResourceInfo, user), extract);
				}
				XDAT.sendJmsRequest(request);
			}
		} catch (Exception e) {
			log.error("Error occurred while trying to POST file", e);
		}
		return workflow;
	}

	private List<FileWriterWrapperI> getFileWriters(XnatResourceInfo xnatResourceInfo) {
		final List<FileWriterWrapperI> wrappers = new ArrayList<>();
		wrappers.add(new FileResourceWrapper(xnatResourceInfo.getResource(), xnatResourceInfo.getFile(),xnatResourceInfo.getFile().getName()));
		return wrappers;
	}

	private PersistentWorkflowI getWorkflow(PersistentWorkflowI workflow, boolean isNew, boolean skipUpdateStats, UserI user) throws JustificationAbsent, ActionNameAbsent, IDAbsent {
		if (workflow == null && !skipUpdateStats) {
			isNew = true;
			workflow = PersistentWorkflowUtils.buildOpenWorkflow(user, getSecurityItem().getItem(), newEventInstance(EventUtils.CATEGORY.DATA, (getAction() != null) ? getAction() : EventUtils.UPLOAD_FILE));
		}
		return workflow;
	}

	private EventMetaI getEventMetaI(PersistentWorkflowI workflow, UserI user) {
		final EventMetaI eventMeta;
		if (workflow == null) {
			eventMeta = EventUtils.DEFAULT_EVENT(user, null);
		} else {
			eventMeta = workflow.buildEvent();
		}
		return eventMeta;
	}

	private PersistentWorkflowI verifyAndGetWorkflow(PersistentWorkflowI workflow, XnatAbstractresource resource, UserI user) {
		if (workflow == null && resource != null && "SNAPSHOTS".equals(resource.getLabel())) {
            if (getSecurityItem() instanceof XnatExperimentdata) {
                final Collection<? extends PersistentWorkflowI> workflows = PersistentWorkflowUtils.getOpenWorkflows(user, ((ArchivableItem) security).getId());
                if (workflows != null && workflows.size() == 1) {
                    workflow = (WrkWorkflowdata) CollectionUtils.get(workflows, 0);
                    if (!"xnat_tools/AutoRun.xml".equals(workflow.getPipelineName())) {
                        workflow = null;
                    }
                }
            }
        }
		return workflow;
	}

	private Object verifyResourceIsNull(XnatAbstractresource resource) {
		final Object resourceIdentifier;
		 if (resource == null) {
             if (getCatalogs().rows().size() > 0) {
                 resourceIdentifier = getCatalogs().getFirstObject();
             } else {
                 if (!getResourceIds().isEmpty()) {
                     resourceIdentifier = getResourceIds().get(0);
                 } else {
                     resourceIdentifier = null;
                 }
             }
         } else {
             resourceIdentifier = resource.getXnatAbstractresourceId();
         }
		return resourceIdentifier;
	}

	private void verifyProjectIsNull() throws ElementNotFoundException {
		if (proj == null) {
            if (parent.getItem().instanceOf("xnat:experimentData")) {
                proj = ((XnatExperimentdata) parent).getPrimaryProject(false);
            } else if (security.getItem().instanceOf("xnat:experimentData")) {
                proj = ((XnatExperimentdata) security).getPrimaryProject(false);
            } else if (parent.getItem().instanceOf("xnat:subjectData")) {
                proj = ((XnatSubjectdata) parent).getPrimaryProject(false);
            } else if (security.getItem().instanceOf("xnat:subjectData")) {
                proj = ((XnatSubjectdata) security).getPrimaryProject(false);
            }
        }
	}


	public Integer getEventId() {
        final String id = getQueryVariable(EventUtils.EVENT_ID);
        if (id != null) {
            return Integer.valueOf(id);
        } else {
            return null;
        }
    }
	
	private XnatAbstractresource getResourceData(UserI user, List<String> _resourceIds) {
		XnatAbstractresource resource = null;
		  try {
	            if (!getResourceIds().isEmpty()) {
	                final List<Integer> alreadyAdded = new ArrayList<>();
	                if (hasCatalogs()) {
	                    for (final Object[] row : getCatalogs().rows()) {
	                        final Integer id    = (Integer) row[0];
	                        final String  label = (String) row[1];
	                        for (final String resourceId : _resourceIds) {
	                            if (!alreadyAdded.contains(id) && (id.toString().equals(resourceId) || (label != null && label.equals(resourceId)))) {
	                                final XnatAbstractresource xnatAbstractresource = XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(id, user, false);
	                                if (row.length == 7) {
	                                	xnatAbstractresource.setBaseURI((String) row[6]);
	                                }
	                                if (proj == null || Permissions.canReadProject(user, proj.getId())) {
	                                	getResources().clear();
	                                    getResources().add(xnatAbstractresource);
	                                    alreadyAdded.add(id);
	                                }
	                            }
	                        }
	                    }
	                }

	                // if caller is asking for the files directly by resource ID (e.g. /experiments/{EXPT_ID}/resources/{RESOURCE_ID}/files),
	                // the catalog will not be found by the superclass
	                // (unless caller passes all=true, which seems clunky to require given that they are passing in the resource PK).
	                // So here we provide an alternate path finding the resource
	                // added check to make sure it's an number.  You can also reference resource labels here (not just pks).
	                for (final String resourceId : getResourceIds()) {
	                    try {
	                        final Integer id = Integer.parseInt(resourceId);
	                        if (!alreadyAdded.contains(id)) {
	                            final XnatAbstractresource xnatAbstractresource = XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(id, user, false);
	                            if (xnatAbstractresource != null) {
	                                final XnatImageassessordata assessor = getAssessor((XnatResourcecatalog) xnatAbstractresource);
	                                if ((proj == null || Permissions.canReadProject(user, proj.getId())) && (assessor == null || Permissions.canRead(user, assessor))) {
	                                	getResources().clear();
	                                	getResources().add(xnatAbstractresource);
	                                }
	                            }
	                        }
	                    } catch (NumberFormatException e) {
	                        // ignore... this is probably a resource label
	                    }
	                }
	            }

			if (!getResources().isEmpty()) {
				resource = getResources().get(0);
			}
		} catch (Exception e) {
			log.error("Error occurred while initializing FileList service", e);
		}
		return resource;
	}

	 @Nullable
	    private XnatImageassessordata getAssessor(final @Nonnull XnatResourcecatalog resource) {
	        try {
	            final Matcher assessorUriMatcher = PATTERN_ASSESSOR_URI.matcher(resource.getUri());
	            if (assessorUriMatcher.find()) {
	                final String assessorId = assessorUriMatcher.group(1);
	                if (StringUtils.isNotBlank(assessorId)) {
	                    final XnatImageassessordata assessor = (XnatImageassessordata) XnatExperimentdata.getXnatExperimentdatasById(assessorId, Users.getAdminUser(), false);
	                    if (assessor != null) {
	                        return assessor;
	                    }
	                    final Matcher archiveUriMatcher = PATTERN_ARCHIVE_URI.matcher(resource.getUri());
	                    if (archiveUriMatcher.find()) {
	                        return (XnatImageassessordata) XnatExperimentdata.GetExptByProjectIdentifier(archiveUriMatcher.group(1), assessorId, Users.getAdminUser(), false);
	                    }
	                }
	            }
	        } catch (Exception e) {
	            log.error("Error getting assessor object to check permissions.", e);
	        }
	        return null;
	    }

	private static class FileRowMapper implements RowMapper<XnatResourcecatalog> {
		FileRowMapper(final UserI user) {
	        _user = user;
	    }
	    @Override
	    public XnatResourcecatalog mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
	        final String xnatAbstractResourceId = resultSet.getString("xnat_abstractresource_id");
	        XnatResourcecatalog xnatResourcecatalogs= XnatResourcecatalog.getXnatResourcecatalogsByXnatAbstractresourceId(xnatAbstractResourceId, _user, false);
	        return xnatResourcecatalogs;
	    }
	    private final UserI _user;
	    
	}
	
	private boolean isQueryVariableFalse(String string) {
		return false;
	}
	
	
	private static final String PROJECT_QUERY=  "SELECT xnat_abstractresource_id FROM xnat_projectdata_resource pr \n" + 
												"LEFT JOIN xnat_abstractresource abst ON pr.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id \n" + 
												"LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id";
	
	private static final String SUBJECT_QUERY= "SELECT xnat_abstractresource_id FROM xnat_subjectdata_resource map \n" + 
												"LEFT JOIN xnat_subjectdata sub ON map.xnat_subjectdata_id=sub.id \n" + 
												"LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id \n" + 
												"LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id ";
	
	private static final String SUBJECT_RESOURCE_QUERY= "SELECT xnat_abstractresource_id\n" + 
														"FROM xnat_subjectdata_resource map \n" + 
														"LEFT JOIN xnat_subjectdata sub ON map.xnat_subjectdata_id=sub.id \n" + 
														"LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id \n" + 
														"LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id";
	
	private static final String EXPERIMENT_ASSESSER_QUERY = "SELECT xnat_abstractresource_id\n" + 
															"FROM img_assessor_out_resource map \n" + 
															"LEFT JOIN xnat_experimentdata expt ON map.xnat_imageassessordata_id=expt.id  \n" + 
															"LEFT JOIN xdat_meta_element xmeexpt ON expt.extension=xmeexpt.xdat_meta_element_id \n" + 
															"LEFT JOIN xdat_element_security xes ON xmeexpt.element_name=xes.element_name \n" + 
															"LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id \n" + 
															"LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id \n" + 
															"LEFT JOIN xnat_imageassessordata xiad ON expt.id=xiad.id\n" + 
															"LEFT JOIN xnat_imageAssessorData iad ON map.xnat_imageassessordata_id=iad.id";
	
	private static final String PRO_SUB_EXP_ASS_QUERY=" SELECT xnat_abstractresource_id\n" + 
			  											"FROM img_assessor_out_resource map \n" + 
			  											"LEFT JOIN xnat_experimentdata expt ON map.xnat_imageassessordata_id=expt.id  \n" + 
			  											"LEFT JOIN xdat_meta_element xmeexpt ON expt.extension=xmeexpt.xdat_meta_element_id \n" + 
			  											"LEFT JOIN xdat_element_security xes ON xmeexpt.element_name=xes.element_name \n" + 
			  											"LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id \n" + 
			  											"LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id \n" + 
			  											"LEFT JOIN xnat_imageassessordata xiad ON expt.id=xiad.id\n" + 
			  											"LEFT JOIN xnat_imageAssessorData iad ON map.xnat_imageassessordata_id=iad.id\n" + 
			  											"LEFT JOIN xnat_subjectAssessorData sad ON iad.imagesession_id=sad.id  \n" + 
			  											"LEFT JOIN xnat_subjectdata sd ON sd.id=sad.subject_id ";
	
	
	private static final String EXP_FILE_RESOURCE_QUERY_1  = " SELECT xnat_abstractresource_id FROM xnat_experimentdata_resource res_map \n" + 
	   											  "  JOIN xnat_abstractresource abst  ON res_map.xnat_abstractresource_xnat_abstractresource_id = abst.xnat_abstractresource_id \n" + 
	   											  "  JOIN  xdat_meta_element xme  ON abst.extension = xme.xdat_meta_element_id      " ;
	
	
	private static final String EXP_FILE_RESOURCE_QUERY_2 =  " SELECT xnat_abstractresource_id FROM xnat_imagescanData isd \n" + 
				  									" JOIN  xnat_abstractresource abst ON isd.xnat_imagescandata_id = abst.xnat_imagescandata_xnat_imagescandata_id \n" + 
				  									" JOIN  xdat_meta_element xme ON abst.extension = xme.xdat_meta_element_id      " ;
	
	private static final String EXP_FILE_RESOURCE_QUERY_3 = " SELECT xnat_abstractresource_id FROM  xnat_imageassessordata iad \n" + 
												   " JOIN img_assessor_out_resource map  ON iad.id = map.xnat_imageassessordata_id \n" + 
												   " JOIN xnat_abstractresource abst  ON map.xnat_abstractresource_xnat_abstractresource_id = abst.xnat_abstractresource_id \n" + 
												   " JOIN xdat_meta_element xme  ON abst.extension = xme.xdat_meta_element_id \n" + 
												   " LEFT JOIN xdat_element_security xes  ON xme.element_name = xes.element_name       " ;
	
	private static final String EXP_FILE_RESOURCE_QUERY_4 =  " SELECT  xnat_abstractresource_id  FROM xnat_imageassessordata  iad \n" + 
													" JOIN xnat_experimentdata_resource map  ON iad.id = map.xnat_experimentdata_id \n" + 
													" JOIN  xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id = abst.xnat_abstractresource_id \n" + 
													" JOIN  xdat_meta_element xme ON abst.extension = xme.xdat_meta_element_id \n" + 
													" LEFT JOIN xdat_element_security xes   ON xme.element_name = xes.element_name     " ;
	
	
	private final String EXP_FILE_QUERY = EXP_FILE_RESOURCE_QUERY_1 + BY_WHERE +  BY_ID_WHERE_EXP_ID + BY_UNION + EXP_FILE_RESOURCE_QUERY_2
			+ BY_WHERE + BY_ID_WHERE_EXP_ID_IMG_SESSION_ID + BY_UNION + EXP_FILE_RESOURCE_QUERY_3 + BY_WHERE + BY_ID_WHERE_EXP_ID_IMAGESESSION_ID
			+ BY_UNION + EXP_FILE_RESOURCE_QUERY_4 + BY_WHERE + BY_ID_WHERE_EXP_ID_IMAGESESSION_ID;

	private final String EXP_RESOURCE_QUERY = EXP_FILE_RESOURCE_QUERY_1 + BY_WHERE + BY_ID_WHERE_EXP_ID + AND_WHERE + BY_ID_WHERE_RESOURCE_ID + BY_UNION + EXP_FILE_RESOURCE_QUERY_2
				+ BY_WHERE + BY_ID_WHERE_EXP_ID_IMG_SESSION_ID + AND_WHERE + BY_ID_WHERE_RESOURCE_ID + BY_UNION + EXP_FILE_RESOURCE_QUERY_3 + BY_WHERE + BY_ID_WHERE_EXP_ID_IMAGESESSION_ID + AND_WHERE + BY_ID_WHERE_RESOURCE_ID
				+ BY_UNION + EXP_FILE_RESOURCE_QUERY_4 + BY_WHERE + BY_ID_WHERE_EXP_ID_IMAGESESSION_ID + AND_WHERE + BY_ID_WHERE_RESOURCE_ID;

	
	private static final String BY_WHERE = " where";
	
	private static final String AND_WHERE = " and  ";
	
	private static final String BY_UNION = "  UNION ";
	
	private static final String BY_ID_WHERE_PROJECT = " where pr.xnat_projectdata_id = :projectId ";
	
	private static final String BY_WHERE_PRO_SUB_EXP_ASS= " WHERE expt.project = :projectId AND sad.subject_id= :subjectId AND iad.imagesession_id= :experimentId  AND map.xnat_imageassessordata_id = :assessedId ";
	
	private static final String BY_ID_WHERE_EXP_AND_ASSESSER = " WHERE iad.imagesession_id= :experimentId  AND  map.xnat_imageassessordata_id = :assessorId";
	
	private static final String BY_ID_WHERE_SUBJ_AND_RESOURCE = " WHERE xnat_subjectdata_id= :subjectId  AND map.xnat_abstractresource_xnat_abstractresource_id= :resourceId";
	
	private static final String BY_ID_WHERE_PROJ = " where sub.project = :projectId ";
	
	private static final String BY_ID_WHERE_PROJ_AND_RESOURCE = " WHERE xnat_projectdata_id= :projectId  AND pr.xnat_abstractresource_xnat_abstractresource_id = :resourceId ";
	
	private static final String BY_ID_WHERE_SUBJECT = " xnat_subjectdata_id = :subjectId ";
	
	private static final String BY_ID_WHERE_EXP_ID = " res_map.xnat_experimentdata_id = :experimentId  ";
	
	private static final String BY_ID_WHERE_EXP_ID_IMG_SESSION_ID = " isd.image_session_id = :experimentId  ";
	
	private static final String BY_ID_WHERE_EXP_ID_IMAGESESSION_ID = " iad.imagesession_id = :experimentId  ";
	
	private static final String BY_ID_WHERE_RESOURCE_ID  = "  abst.xnat_abstractresource_id = :resourceId ";

	private final NamedParameterJdbcTemplate _template;
	
	//private static final List<Variant> VARIANTS             = Arrays.asList(new Variant(MediaType.APPLICATION_JSON), new Variant(MediaType.TEXT_HTML), new Variant(MediaType.TEXT_XML), new Variant(MediaType.IMAGE_JPEG));
    private static final Pattern       PATTERN_ASSESSOR_URI = Pattern.compile("/assessors/([^/]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern       PATTERN_ARCHIVE_URI  = Pattern.compile("/archive/([^/]+)");
	
	private String filePath = "";
	//private XnatAbstractresource resource = null;
	private String reference;
	private final boolean acceptNotFound = false;
	private boolean delete = false;
	private boolean async = false ;
	private String[] notifyList = {};

}
