package org.nrg.xnat.services.resources.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.om.WrkWorkflowdata;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatResource;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFTItem;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.XftItemEvent;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.MetaDataException;
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.model.util.XNATCatalogTemplateUtil;
import org.nrg.xnat.model.util.XnatTemplateUtil;
import org.nrg.xnat.services.resources.ResourceService;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.restlet.data.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResourceServiceImpl extends XNATCatalogTemplateUtil implements ResourceService{
	
	@Autowired
	public ResourceServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}

	@Override
	public List<XnatAbstractresource> findByExperimentId(UserI user, String experimentId) {
		return _template.query(EXPERIMENT_QUERY + BY_ID_WHERE_EXPERIMENT, new MapSqlParameterSource("experimentId", experimentId), new ResourceRowMapper(user));
	}
	
	
	@Override
	public XnatAbstractresource findByIdAndExperimentId(UserI user, Integer resourceId, String experimentId) {
		return _template.queryForObject(EXPERIMENT_QUERY + BY_ID_WHERE_EXPERIMENT + AND_WHERE + BY_RESOURCE_ID_WHERE, new MapSqlParameterSource("resourceId", resourceId).addValue("experimentId", experimentId), new ResourceRowMapper(user));
	}

	@Override
	public List<XnatAbstractresource> findResourceByExperimentAndScan(UserI user, String assessedId, String scanId) {
		XnatTemplateUtil xnatTemplateUtil =new XnatTemplateUtil();
		List<XnatAbstractresource> xnatAbstractresources;
		ArrayList<XnatExperimentdata> assesseds = xnatTemplateUtil.getXnatAssessordata(assessedId, user,null);
		ArrayList<XnatImagescandata> scans = xnatTemplateUtil.getXnatImageScanData(scanId, user, assesseds);
		String query = XnatTemplateUtil.getQuery(scans,assesseds, null);
		 xnatAbstractresources = _template.query(query,  new ResourceRowMapper(user));
		 return xnatAbstractresources;
	}
	
	@Override
	public List<XnatAbstractresource> findByProject(UserI user, String projectId) {
		return _template.query(PROJECT_QUERY + BY_ID_WHERE_PROJECT, new MapSqlParameterSource("projectId", projectId), new ResourceRowMapper(user));
	}
	
	@Override
	public List<XnatAbstractresource> findByProjectAndLabel(UserI user, String projectId, String label) {
		return _template.query(PROJECT_QUERY + BY_ID_WHERE_PROJECT + AND_WHERE + BY_ID_WHERE_label , new MapSqlParameterSource("projectId", projectId).addValue("label", label), new ResourceRowMapper(user));
	}

	@Override
	public XnatAbstractresource findByIdAndProject(UserI user, Integer resourceId, String projectId) {
		return _template.queryForObject(PROJECT_QUERY + BY_ID_WHERE_PROJECT + AND_WHERE + BY_RESOURCE_ID_WHERE, new MapSqlParameterSource("resourceId", resourceId).addValue("projectId", projectId), new ResourceRowMapper(user));
	}
	
	@Override
	public List<XnatAbstractresource> findBySubject(UserI user, String subjectId) {
		return _template.query(SUBJECT_QUERY + BY_WHERE + BY_ID_WHERE_SUBJECT, new MapSqlParameterSource("subjectId", subjectId), new ResourceRowMapper(user));
	}
	
	@Override
	public List<XnatAbstractresource> findByProjectAndSubjectAndExperiment(UserI sessionUser, String projectId, String subjectId, String experimentId) {
		return null;
	}

	
	@Override
	public List<XnatAbstractresource> findByProjectAndSubject(UserI user, String projectId, String subjectId) {
		return _template.query(SUBJECT_QUERY + BY_WHERE_PROJECT + AND_WHERE + BY_ID_WHERE_SUBJECT  , new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId), new ResourceRowMapper(user));
	}


	@Override
	public XnatAbstractresource findByIdAndProjectAndSubject(UserI user, Integer resourceId, String projectId, String subjectId) {
		return _template.queryForObject(SUBJECT_QUERY + BY_WHERE_PROJECT + AND_WHERE + BY_ID_WHERE_SUBJECT + AND_WHERE + BY_RESOURCE_ID_WHERE  , new MapSqlParameterSource("resourceId", resourceId).addValue("projectId", projectId).addValue("subjectId", subjectId), new ResourceRowMapper(user));
	}
	
	@Override
	public XnatAbstractresource findByIdAndSubject(UserI user, Integer resourceId, String subjectId) {
		return _template.queryForObject(SUBJECT_QUERY  + BY_WHERE + BY_RESOURCE_ID_WHERE + AND_WHERE + BY_ID_WHERE_SUBJECT , new MapSqlParameterSource("resourceId", resourceId).addValue("subjectId", subjectId), new ResourceRowMapper(user));
	}
	
	@Override
	public List<XnatAbstractresource> findResourceByexperimentIdAndAssessedId(UserI user, String experimentId, String assessedId ) {
		return _template.query(EXPERIMENT_ASSESSER_QUERY + BY_WHERE_EXP_ASSE   , new MapSqlParameterSource("experimentId", experimentId).addValue("assessedId", assessedId), new ResourceRowMapper(user));
	}
	
	@Override
	public XnatAbstractresource findResourceByexperimentIdAndAssessedIdAndResourceId(UserI user, String experimentId, String assessedId, Integer resourceId) {
		return _template.queryForObject(EXPERIMENT_ASSESSER_QUERY + BY_WHERE_EXP_ASSE + AND_WHERE + BY_WHERE_RESOURCE  , new MapSqlParameterSource("experimentId", experimentId).addValue("assessedId", assessedId).addValue("resourceId", resourceId), new ResourceRowMapper(user));	
	}
	
	@Override
	public List<XnatAbstractresource> findByIdAndProjectAndSubjectAndExperimentAndAssessors(UserI user, String projectId, String subjectId, String experimentId, String assessedId) {
		return _template.query(PRO_SUB_EXP_ASS_QUERY + BY_WHERE_PRO_SUB_EXP_ASS  , new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId).addValue("experimentId", experimentId).addValue("assessedId", assessedId), new ResourceRowMapper(user));
	}
	
	@Override
	public XnatResourcecatalog create(UserI user, String projectId, String subjectId, String experimentId, String assessorId, String scanId, String type, XnatResource xnatResource) {
		proj = null;
		sub = null;
		expts = new ArrayList<>();
		assesseds = new ArrayList<>();
		scans = new ArrayList<>();
		
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
		
		
		XFTItem item;
		XnatTemplateUtil xnatTemplateUtil = new XnatTemplateUtil();
		try {
			item = xnatResource.getItem();
			if (item == null)
				throw new DataFormatException("Need POST Contents");

			if (item.instanceOf("xnat:resourceCatalog")) {
				XnatResourcecatalog catResource = (XnatResourcecatalog) BaseElement.GetGeneratedItem(item);

				if (catResource.getXnatAbstractresourceId() != null) {
					XnatAbstractresource existing = XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(catResource.getXnatAbstractresourceId(), user, false);
					if (existing != null)
						throw new ResourceAlreadyExistsException("Specified catalog already exists.", projectId);
					else
						throw new DataFormatException("Contains erroneous generated fields (xnat_abstractresource_id)");
				}

				xnatTemplateUtil.setCatalogAttributes(user, catResource);

				PersistentWorkflowI wrk = PersistentWorkflowUtils.getWorkflowByEventId(user, getEventId());
				if (wrk == null && "SNAPSHOTS".equals(catResource.getLabel())) {
					if (getSecurityItem() instanceof XnatExperimentdata) {
						Collection<? extends PersistentWorkflowI> workflows = PersistentWorkflowUtils
								.getOpenWorkflows(user, ((ArchivableItem) getSecurityItem()).getId());
						if (workflows != null && workflows.size() == 1) {
							wrk = (WrkWorkflowdata) CollectionUtils.get(workflows, 0);
							if (!"xnat_tools/AutoRun.xml".equals(wrk.getPipelineName())) {
								wrk = null;
							}
						}
					}
				}
				
				xnatTemplateUtil.insertCatalogWrap(catResource, wrk, user, proj, sub, expts, assesseds, scans);
				
			} else
				throw new DataFormatException("Only ResourceCatalog documents can be PUT to this address.");

		} catch (ActionException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return (XnatResourcecatalog) xnatResource;
	}

	@Override
	public void deleteByProjectIdAndResourceId(UserI user, String projectId, String subjectId, String experimentId, String assessorId,String scanId,String type,String resourceId1) {
		
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
		
		_resourceIds = setResourcesIds(resourceId1, user, false);
		
        final XFTItem securityItem = security.getItem();
        final XFTItem parentItem   = parent.getItem();

        checkPermsAndStatus(user,securityItem, parentItem );
       
        final Triple<XnatProjectdata, String, String> securityTriple = getProjXsiTypeAndId(securityItem, parentItem);
        
        if (proj == null) {
            proj = securityTriple.getLeft();
        }

        final String xsiType    = securityTriple.getMiddle();
        final String securityId = securityTriple.getRight();

        try {
        	getAbstractResourceItem(xsiType,securityId,securityItem,parentItem );
        	
            final List<String> failed = new ArrayList<>();
            final String archivePath  = proj.getRootArchivePath();
            final String project      = proj.getId();
            for(String rId: _resourceIds) {
            	final XnatAbstractresource resource = XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(rId, user, false);
                    final String              resourceId = getResourceDisplay(resource);
                    final PersistentWorkflowI workflow   = PersistentWorkflowUtils.getOrCreateWorkflowData(getEventId(), user, xsiType, securityId, proj.getId(), newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.REMOVE_CATALOG + " " + resourceId));
                    final EventMetaI meta  = workflow.buildEvent();
                    try {
                        resource.deleteWithBackup(archivePath, project, user, meta);
                        SaveItemHelper.authorizedRemoveChild(parentItem, xmlPath, resource.getItem(), user, meta);
                        PersistentWorkflowUtils.complete(workflow, meta);
                    } catch (Exception e) {
                        failed.add(getResourceDisplay(resource));
                        workflow.setDetails(e.getMessage());
                        PersistentWorkflowUtils.fail(workflow, meta);
                    }
            }
            
            if (!failed.isEmpty()) {
                if (failed.size() == getResources().size()) {
                	 throw new InitializationException( "Deletion failed for all resources: " + StringUtils.join(failed, ", "));
                } else {
                    //getResponse().setStatus(Status.SUCCESS_MULTI_STATUS, "Deleted resources as requested, but the following resources failed somehow: " + StringUtils.join(failed, ", "));
                }
            }
            XDAT.triggerXftItemEvent(xsiType, securityId, XftItemEvent.UPDATE);
        } catch (ClientException e) {
        	log.error( e.getMessage());
        } catch (Exception e) {
            log.error("An error occurred trying to delete resources from the secured object {}/ID={}: {}", xsiType, securityId, getResourceIds(), e);
        }
	}

	private List<String> getAbstractResourceItem(String xsiType, String securityId, XFTItem securityItem, XFTItem parentItem) {
		final List<String> ineligible = Lists.newArrayList(Iterables.transform(Iterables.filter(getResources(), new Predicate<XnatAbstractresource>() {
            @Override
            public boolean apply(final XnatAbstractresource resource) {
                try {
                    return resource.getItem().isLocked() || !resource.getItem().isActive() && !resource.getItem().isQuarantine();
                } catch (MetaDataException e) {
                    log.error("An error occurred trying to check the lock/active/quarantine status of the resource {} associated with {}/ID={}", resource.getXnatAbstractresourceId(), xsiType, securityId);
                    return true;
                }
            }
        }), RESOURCE_TO_STRING_FUNCTION));

        if (!ineligible.isEmpty()) {
            try {
				throw new ClientException(Status.CLIENT_ERROR_FORBIDDEN, "Item " + securityItem.getXSIType() + "/ID=" + securityItem.getIDValue() + " has " + ineligible.size() + " resources that are either locked or are not active or quarantined and can't be deleted: " + StringUtils.join(ineligible));
			} catch (ClientException | XFTInitException | ElementNotFoundException e) {
				e.printStackTrace();
			}
        }
		return ineligible;
	}

	private Triple<XnatProjectdata, String, String> getProjXsiTypeAndId(XFTItem securityItem, XFTItem parentItem) {
		 Triple<XnatProjectdata, String, String> securityTriple = null;
        try {
            securityTriple = getProjectXsiTypeAndId(parent, security);
            if (securityTriple.getLeft() == null) {
                log.warn("Got a parent item of type {}/ID={} and security item of type {}/ID={}, but neither of these is a project, subject, or experiment.", parentItem.getIDValue(), parentItem.getXSIType(), securityItem.getIDValue(), securityItem.getXSIType());
                throw new DataFormatException("You can't directly delete insecure items");
            }
        } catch (XFTInitException | ElementNotFoundException | DataFormatException e) {
            log.error("An error occurred trying to delete resources", e.getMessage());
        }
        return securityTriple;
	}

	private void checkPermsAndStatus(UserI user, XFTItem securityItem, XFTItem parentItem) {
		 try {
	            checkPermissionsAndStatus(user, securityItem);
	        } catch (ClientException e) {
	        	log.error( e.getMessage());
	            return;
	        } catch (Exception e) {
	            try {
	                log.error("An error occurred trying to delete the specified resources on parent item {}/ID={} and security item {}/ID={}: {}", parentItem.getIDValue(), parentItem.getXSIType(), securityItem.getIDValue(), securityItem.getXSIType(), StringUtils.join(getResourceIds(), ", "), e);
	                throw new InitializationException(e.getMessage());
	            } catch (XFTInitException | ElementNotFoundException  | InitializationException ex) {
	                log.error("An error occurred trying to delete resources", ex.getMessage());
	            }
	            return;
	        }
	}

	private void checkPermissionsAndStatus(final UserI user, final XFTItem securityItem) throws Exception {
        if (!Permissions.canDelete(user, security)) {
            throw new ClientException(Status.CLIENT_ERROR_FORBIDDEN, "User account doesn't have permission to modify this session.");
        }
        if (securityItem.isLocked()) {
            //cannot modify item if it's locked
            throw new ClientException(Status.CLIENT_ERROR_FORBIDDEN, "Item " + securityItem.getXSIType() + "/ID=" + securityItem.getIDValue() + " is locked, resource deletion not allowed.");
        }
        if (!securityItem.isActive() && !securityItem.isQuarantine()) {
            //cannot modify item if it isn't active or quarantined.
            throw new ClientException(Status.CLIENT_ERROR_FORBIDDEN, "Item " + securityItem.getXSIType() + "/ID=" + securityItem.getIDValue() + " is not active or quarantined, resource deletion not allowed.");
        }
    }
	
	 @Nonnull
	    private Triple<XnatProjectdata, String, String> getProjectXsiTypeAndId(final ItemI parent, final ItemI security) throws ElementNotFoundException {
	        final XFTItem parentItem   = parent.getItem();
	        final XFTItem securityItem = security.getItem();
	        if (parentItem.instanceOf(XnatExperimentdata.SCHEMA_ELEMENT_NAME)) {
	            final XnatExperimentdata experiment = (XnatExperimentdata) this.parent;
	            return ImmutableTriple.of(experiment.getPrimaryProject(false), experiment.getXSIType(), experiment.getId());
	        }
	        if (securityItem.instanceOf(XnatExperimentdata.SCHEMA_ELEMENT_NAME)) {
	            final XnatExperimentdata experiment = (XnatExperimentdata) security;
	            return ImmutableTriple.of(experiment.getPrimaryProject(false), experiment.getXSIType(), experiment.getId());
	        }
	        if (parentItem.instanceOf(XnatSubjectdata.SCHEMA_ELEMENT_NAME)) {
	            final XnatSubjectdata subject = (XnatSubjectdata) parent;
	            return ImmutableTriple.of(subject.getPrimaryProject(false), XnatSubjectdata.SCHEMA_ELEMENT_NAME, subject.getId());
	        }
	        if (securityItem.instanceOf(XnatSubjectdata.SCHEMA_ELEMENT_NAME)) {
	            final XnatSubjectdata subject = (XnatSubjectdata) security;
	            return ImmutableTriple.of(subject.getPrimaryProject(false), XnatSubjectdata.SCHEMA_ELEMENT_NAME, subject.getId());
	        }
	        if (parentItem.instanceOf(XnatProjectdata.SCHEMA_ELEMENT_NAME)) {
	            final XnatProjectdata project = (XnatProjectdata) parent;
	            return ImmutableTriple.of(project, XnatProjectdata.SCHEMA_ELEMENT_NAME, project.getId());
	        }
	        if (securityItem.instanceOf(XnatProjectdata.SCHEMA_ELEMENT_NAME)) {
	            final XnatProjectdata project = (XnatProjectdata) security;
	            return ImmutableTriple.of(project, XnatProjectdata.SCHEMA_ELEMENT_NAME, project.getId());
	        }
	        return ImmutableTriple.nullTriple();
	    }
	 
	 private static final Function<XnatAbstractresource, String> RESOURCE_TO_STRING_FUNCTION = new Function<XnatAbstractresource, String>() {
	        @Override
	        public String apply(final XnatAbstractresource resource) {
	            return getResourceDisplay(resource);
	        }
	    };
	    
	    @Nonnull
	    private static String getResourceDisplay(final XnatAbstractresource resource) {
	        final String resourceLabel = resource.getLabel();
	        return resource.getXnatAbstractresourceId() + (StringUtils.isBlank(resourceLabel) ? "" : " (" + resourceLabel + ")");
	    }
	 
	 private Integer getEventId() {
		final String id = getQueryVariable(EventUtils.EVENT_ID);
		if (id != null) {
			return Integer.valueOf(id);
		} else {
			return null;
		}
	}
	private String getQueryVariable(String string) {
		return null;
	}

	private static class ResourceRowMapper implements RowMapper<XnatAbstractresource> {
		ResourceRowMapper(final UserI user) {
			_user = user;
		}

		@Override
		public XnatAbstractresource mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			final String xnatAbstractResourceId = resultSet.getString("xnat_abstractresource_id");
			XnatAbstractresource xnatAbstractresource = XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(xnatAbstractResourceId, _user, false);
			return xnatAbstractresource;
		}

		private final UserI _user;

	}
	
	private static final String BY_ID_WHERE_EXPERIMENT = " where x.id = :experimentId ";
	
	private static final String BY_ID_WHERE_PROJECT = " where pr.xnat_projectdata_id = :projectId ";
	
	private static final String BY_ID_WHERE_label = "  ar.label = :label ";
	
	private static final String AND_WHERE = " and";
	
	private static final String BY_RESOURCE_ID_WHERE = " ar.xnat_abstractresource_id = :resourceId ";
	
	private static final String BY_ID_WHERE_SUBJECT = " s.id = :subjectId ";

	private static final String BY_WHERE = " where";
	
	private static final String BY_WHERE_PROJECT = " where s.project = :projectId ";
	
	private static final String PROJECT_QUERY = "SELECT  ar.xnat_abstractresource_id FROM xnat_abstractresource ar \n" +
												"LEFT JOIN xnat_projectdata_resource pr ON ar.xnat_abstractresource_id = pr.xnat_abstractresource_xnat_abstractresource_id";
	
	private static final String SUBJECT_QUERY = "SELECT DISTINCT ar.xnat_abstractresource_id FROM xnat_subjectdata s\n" + 
												" LEFT JOIN xnat_subjectdata_resource r ON s.id = r.xnat_subjectdata_id\n" + 
												" LEFT JOIN xnat_abstractresource ar ON ar.xnat_abstractresource_id = r.xnat_abstractresource_xnat_abstractresource_id\n" + 
												" LEFT JOIN xdat_meta_element e ON ar.extension = e.xdat_meta_element_id";
	
	private static final String EXPERIMENT_QUERY = "SELECT DISTINCT ar.xnat_abstractresource_id, e.element_name FROM  xnat_experimentdata x \n" + 
													" LEFT JOIN xnat_imagescandata s ON x.id = s.image_session_id\n" + 
													" LEFT JOIN xnat_experimentdata_resource r ON r.xnat_experimentdata_id = x.id\n" + 
													" LEFT JOIN img_assessor_in_resource air ON air.xnat_imageassessordata_id = x.id\n" + 
													" LEFT JOIN img_assessor_out_resource aor ON aor.xnat_imageassessordata_id = x.id\n" + 
													" LEFT JOIN xnat_imageassessordata a ON x.id = a.id\n" + 
													" LEFT JOIN xnat_abstractresource ar ON ar.xnat_imagescandata_xnat_imagescandata_id = s.xnat_imagescandata_id OR\n" + 
													" ar.xnat_abstractresource_id IN (r.xnat_abstractresource_xnat_abstractresource_id, air.xnat_abstractresource_xnat_abstractresource_id, aor.xnat_abstractresource_xnat_abstractresource_id)\n" + 
													" LEFT JOIN xdat_meta_element e ON ar.extension = e.xdat_meta_element_id "; 
	
	private static final String EXPERIMENT_ASSESSER_QUERY= "SELECT xnat_abstractresource_id\n" + 
																	"FROM img_assessor_out_resource map \n" + 
																	"LEFT JOIN xnat_experimentdata expt ON map.xnat_imageassessordata_id=expt.id  \n" + 
																	"LEFT JOIN xdat_meta_element xmeexpt ON expt.extension=xmeexpt.xdat_meta_element_id \n" + 
																	"LEFT JOIN xdat_element_security xes ON xmeexpt.element_name=xes.element_name \n" + 
																	"LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id \n" + 
																	"LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id \n" + 
																	"LEFT JOIN xnat_imageassessordata xiad ON expt.id=xiad.id\n" + 
																	"LEFT JOIN xnat_imageAssessorData iad ON map.xnat_imageassessordata_id=iad.id";
	
	private static final String BY_WHERE_EXP_ASSE = "  WHERE iad.imagesession_id= :experimentId  AND map.xnat_imageassessordata_id = :assessedId ";
  
	private static final String BY_WHERE_RESOURCE = " xnat_abstractresource_id = :resourceId";
	
	
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
	
	private static final String BY_WHERE_PRO_SUB_EXP_ASS= " WHERE expt.project = :projectId AND sad.subject_id= :subjectId AND iad.imagesession_id= :experimentId  AND map.xnat_imageassessordata_id = :assessedId ";
	
	private final NamedParameterJdbcTemplate _template;

}