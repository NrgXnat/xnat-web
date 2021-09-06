package org.nrg.xnat.services.resources.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.oro.io.GlobFilenameFilter;
import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotAuthenticatedException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xapi.model.TriageDto;
import org.nrg.xapi.model.TriageFileDto;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.bean.CatCatalogBean;
import org.nrg.xdat.model.CatEntryI;
import org.nrg.xdat.om.WrkWorkflowdata;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImageassessordata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatResource;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.om.XnatSubjectassessordata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.helpers.Features;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.services.cache.UserDataCache;
import org.nrg.xdat.turbine.utils.TurbineUtils;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFTItem;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.XftItemEvent;
import org.nrg.xft.event.XftItemEventI;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.InvalidItemException;
import org.nrg.xft.exception.MetaDataException;
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xft.schema.Wrappers.XMLWrapper.SAXWriter;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xft.utils.zip.ZipUtils;
import org.nrg.xnat.dto.file.ResourceFileDto;
import org.nrg.xnat.dto.resource.DIRResourceDto;
import org.nrg.xnat.dto.resource.FileSet;
import org.nrg.xnat.dto.resource.MediaTypeUtil;
import org.nrg.xnat.dto.resource.ZipRepresentationUtil;
import org.nrg.xnat.helpers.resource.XnatResourceInfo;
import org.nrg.xnat.helpers.resource.direct.ResourceModifierA;
import org.nrg.xnat.helpers.resource.direct.ResourceModifierA.UpdateMeta;
import org.nrg.xnat.helpers.uri.URIManager;
import org.nrg.xnat.helpers.uri.UriParserUtils;
import org.nrg.xnat.helpers.uri.archive.ResourceURII;
import org.nrg.xnat.model.util.XNATCatalogTemplateUtil;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.model.util.XnatTemplateUtil;
import org.nrg.xnat.presentation.ChangeSummaryBuilderA;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.nrg.xnat.services.archive.CatalogService;
import org.nrg.xnat.services.cache.UserProjectCache;
import org.nrg.xnat.services.messaging.file.MoveStoredFileRequest;
import org.nrg.xnat.services.resources.ResourceService;
import org.nrg.xnat.services.triage.TriageManifest;
import org.nrg.xnat.services.triage.TriageUtils;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.nrg.xnat.utils.CatalogUtils;
import org.nrg.xnat.utils.WorkflowUtils;
import org.nrg.xnat.utils.CatalogUtils.CatEntryFilterI;
import org.nrg.xnat.utils.CatalogUtils.CatalogData;
import org.restlet.data.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResourceServiceImpl extends XNATCatalogTemplateUtil implements ResourceService{
	
	@Autowired
	public ResourceServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}

	/**
	 * Get list of resource with specified experiment ID
	 */
	@Override
	public List<XnatAbstractresource> findByExperimentId(UserI user, String experimentId) throws NotFoundException, DataFormatException {
		if(StringUtils.isBlank(experimentId)) {
    		throw new DataFormatException("The requested experiment ID "+ experimentId  + "wasn't found ");
		}
		List<XnatAbstractresource> resources = _template.query(EXPERIMENT_QUERY + BY_ID_WHERE_EXPERIMENT, new MapSqlParameterSource("experimentId", experimentId), new ResourceRowMapper(user));
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, experimentId) ;
		}
    		return resources;
	}
	
	/**
	 * Get a resource with specified resource ID and experiment ID
	 */
	@Override
	public Optional<XnatAbstractresource> findByIdAndExperimentId(UserI user, Integer resourceId, String experimentId) throws DataFormatException, NotFoundException {
		if(StringUtils.isBlank(experimentId)) {
    		throw new DataFormatException("The requested experiment ID "+ experimentId  + "wasn't found ");
		}
		XnatAbstractresource resource = _template.queryForObject(EXPERIMENT_QUERY + BY_ID_WHERE_EXPERIMENT + AND_WHERE + BY_RESOURCE_ID_WHERE, new MapSqlParameterSource("resourceId", resourceId).addValue("experimentId", experimentId), new ResourceRowMapper(user));
		if(Objects.isNull(resource)) {
    		throw new  NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, experimentId) ;
		}
    	return Optional.of(resource);
	}

	/**
	 * Get list of resource with specified experiment ID and scan ID
	 */
	@Override
	public List<XnatAbstractresource> findByExperimentIdAndScanId(UserI user, String assessorId, String scanId) throws DataFormatException, NotFoundException {
		if(StringUtils.isBlank(assessorId)) {
    		throw new DataFormatException("The requested assessor ID "+ assessorId  + "wasn't found ");
		}
		if(StringUtils.isBlank(scanId)) {
    		throw new DataFormatException("The requested scan ID "+ scanId  + "wasn't found ");
		}
		List<XnatAbstractresource> resources = getXnatAbstractResourceData(user, null, null, null, assessorId, scanId, null);
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME) ;
		}
		return resources;
	}
	
	/**
	 * Get list of resource with specified project ID
	 */
	@Override
	public List<XnatAbstractresource> findByProjectId(UserI user, String projectId) throws DataFormatException, NotFoundException {
		if(Objects.isNull(projectId)) {
    		throw new DataFormatException("The requested project ID "+ projectId  + "wasn't found ");
		}
		List<XnatAbstractresource> resources = _template.query(PROJECT_QUERY + BY_ID_WHERE_PROJECT, new MapSqlParameterSource("projectId", projectId), new ResourceRowMapper(user));
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, projectId) ;
		}
		return resources;
	}
	
	
	/**
	 * Get list of resource with specified project ID and label
	 */
	@Override
	public List<XnatAbstractresource> findByProjectIdAndLabel(UserI user, String projectId, String label) throws DataFormatException, NotFoundException {
		if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID "+ projectId  + "wasn't found ");
		}
		if(StringUtils.isBlank(label)) {
    		throw new DataFormatException("The requested label  "+ label  + "wasn't found ");
		}
		List<XnatAbstractresource> resources = _template.query(PROJECT_QUERY + BY_ID_WHERE_PROJECT + AND_WHERE + BY_ID_WHERE_label , new MapSqlParameterSource("projectId", projectId).addValue("label", label), new ResourceRowMapper(user));
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, projectId) ;
		}
		return resources;
	}

	
	/**
	 * Get a resource with specified resource ID and project ID
	 */
	@Override
	public Optional<XnatAbstractresource> findByIdAndProjectId(UserI user, Integer resourceId, String projectId) throws DataFormatException, NotFoundException {
		if(Objects.isNull(resourceId)) {
    		throw new DataFormatException("The requested resource ID "+ resourceId  + "wasn't found ");
		}
		if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID "+ projectId  + "wasn't found ");
		}
		XnatAbstractresource resource = _template.queryForObject(PROJECT_QUERY + BY_ID_WHERE_PROJECT + AND_WHERE + BY_RESOURCE_ID_WHERE, new MapSqlParameterSource("resourceId", resourceId).addValue("projectId", projectId), new ResourceRowMapper(user));
		if(Objects.isNull(resource)) {
    		throw new  NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, resourceId) ;
		}
    	return Optional.of(resource);
	}
	
	
	/**
	 * Get list of resource with specified subject ID
	 */
	@Override
	public List<XnatAbstractresource> findBySubjectId(UserI user, String subjectId) throws DataFormatException, NotFoundException {
		if(StringUtils.isBlank(subjectId)) {
    		throw new DataFormatException("The requested subject ID "+ subjectId  + "wasn't found ");
		}
		List<XnatAbstractresource> resources = _template.query(SUBJECT_QUERY + BY_WHERE + BY_ID_WHERE_SUBJECT, new MapSqlParameterSource("subjectId", subjectId), new ResourceRowMapper(user));
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, subjectId) ;
		}
		return resources;
	}
	
	
	/**
	 * Get list of resource with specified project ID, subject ID and experiment ID
	 */
	@Override
	public List<XnatAbstractresource> findByProjectIdAndSubjectIdAndExperimentId(UserI user, String projectId, String subjectId, String experimentId) throws DataFormatException, NotFoundException {
		if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID "+ projectId  + "wasn't found ");
		}
		if(StringUtils.isBlank(subjectId)) {
    		throw new DataFormatException("The requested subject ID "+ subjectId  + "wasn't found ");
		}
		if(StringUtils.isBlank(experimentId)) {
    		throw new DataFormatException("The requested experiment ID "+ experimentId  + "wasn't found ");
		}
		List<XnatAbstractresource> resources =  getXnatAbstractResourceData(user, projectId, subjectId, experimentId,null,null, null);
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, projectId) ;
		}
		return resources;
	}

	/**
	 * Get list of resource with specified project ID and subject ID
	 */
	@Override
	public List<XnatAbstractresource> findByProjectIdAndSubjectId(UserI user, String projectId, String subjectId) throws DataFormatException, NotFoundException {
		if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID "+ projectId  + "wasn't found ");
		}
		if(StringUtils.isBlank(subjectId)) {
    		throw new DataFormatException("The requested subject ID "+ subjectId  + "wasn't found ");
		}
		List<XnatAbstractresource> resources = _template.query(SUBJECT_QUERY + BY_WHERE_PROJECT + AND_WHERE + BY_ID_WHERE_SUBJECT  , new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId), new ResourceRowMapper(user));
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, projectId) ;
		}
		return resources;
	}

	/**
	 * Get a resource with specified resource ID, project ID and subject ID
	 */
	@Override
	public Optional<XnatAbstractresource> findByIdAndProjectIdAndSubjectId(UserI user, Integer resourceId, String projectId, String subjectId) throws DataFormatException, NotFoundException {
		if(Objects.isNull(resourceId)) {
    		throw new DataFormatException("The requested resource ID "+ resourceId  + "wasn't found ");
		}
		if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID "+ projectId  + "wasn't found ");
		}
		if(StringUtils.isBlank(subjectId)) {
    		throw new DataFormatException("The requested subject ID "+ subjectId  + "wasn't found ");
		}
		XnatAbstractresource resource = _template.queryForObject(SUBJECT_QUERY + BY_WHERE_PROJECT + AND_WHERE + BY_ID_WHERE_SUBJECT + AND_WHERE + BY_RESOURCE_ID_WHERE  , new MapSqlParameterSource("resourceId", resourceId).addValue("projectId", projectId).addValue("subjectId", subjectId), new ResourceRowMapper(user));
		if(Objects.isNull(resource)) {
    		throw new  NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, resourceId) ;
		}
		return Optional.of(resource);
	}
	
	/**
	 * Get a resource with specified resource ID and experiment ID
	 */
	@Override
	public Optional<XnatAbstractresource> findByIdAndSubjectId(UserI user, Integer resourceId, String subjectId) throws DataFormatException, NotFoundException {
		if(Objects.isNull(resourceId)) {
    		throw new DataFormatException("The requested resource ID "+ resourceId  + "wasn't found ");
		}
		if(StringUtils.isBlank(subjectId)) {
    		throw new DataFormatException("The requested subject ID "+ subjectId  + "wasn't found ");
		}
		XnatAbstractresource resource =  _template.queryForObject(SUBJECT_QUERY  + BY_WHERE + BY_RESOURCE_ID_WHERE + AND_WHERE + BY_ID_WHERE_SUBJECT , new MapSqlParameterSource("resourceId", resourceId).addValue("subjectId", subjectId), new ResourceRowMapper(user));
		if(Objects.isNull(resource)) {
    		throw new  NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, resourceId) ;
		}
		return Optional.of(resource);
	}
	
	/**
	 * Get list of resource with specified experiment ID and Assessor ID
	 */
	@Override
	public List<XnatAbstractresource> findByExperimentIdAndAssessedId(UserI user, String experimentId, String assessorId, String type) throws DataFormatException, NotFoundException {
		List<XnatAbstractresource> resources = new ArrayList<>();
		if(StringUtils.isBlank(experimentId)) {
    		throw new DataFormatException("The requested experiment ID "+ experimentId  + "wasn't found ");
		}
		if(StringUtils.isBlank(assessorId)) {
    		throw new DataFormatException("The requested assessor ID "+ assessorId  + "wasn't found ");
		}
		if(Objects.nonNull(type) && !type.isEmpty()) {
			resources  = getXnatAbstractResourceData(user, null, null, experimentId, assessorId, null, type);
		}else {
			resources = _template.query(EXPERIMENT_ASSESSER_QUERY + BY_WHERE_EXP_ASSE   , new MapSqlParameterSource("experimentId", experimentId).addValue("assessedId", assessorId), new ResourceRowMapper(user));
		}
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, experimentId) ;
		}
		return resources;
	}
	
	
	/**
	 * Get list of resource with specified experiment ID
	 */
	@Override
	public Optional<XnatAbstractresource> findByExperimentIdAndAssessedIdAndResourceId(UserI user, String experimentId, String assessedId, String type, Integer resourceId) throws DataFormatException, NotFoundException  {
		XnatAbstractresource resource = null;
		if(StringUtils.isBlank(experimentId)) {
    		throw new DataFormatException("The requested experiment ID "+ experimentId  + "wasn't found ");
		}
		if(StringUtils.isBlank(assessedId)) {
    		throw new DataFormatException("The requested assessed ID "+ assessedId  + "wasn't found ");
		}
		if(Objects.isNull(resourceId)) {
    		throw new DataFormatException("The requested resource ID "+ resourceId  + "wasn't found ");
		}
		if(Objects.nonNull(type) && !type.isEmpty())
			resource = getXnatResource(user, experimentId, assessedId, type, resourceId);
		else 
			resource = _template.queryForObject(EXPERIMENT_ASSESSER_QUERY + BY_WHERE_EXP_ASSE + AND_WHERE + BY_WHERE_RESOURCE  , new MapSqlParameterSource("experimentId", experimentId).addValue("assessedId", assessedId).addValue("resourceId", resourceId), new ResourceRowMapper(user));	
		if(Objects.isNull(resource)) {
    		throw new  NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, resourceId) ;
		}
		return Optional.of(resource);
	}
	
	/**
	 * Get list of resource with specified project ID, subject ID, experiment ID and scan ID
	 */
	@Override
	public List<XnatAbstractresource> findByProjectIdAndSubjectIdAndExperimentIdAndScanId(UserI user, String projectId, String subjectId, String assessorId, String scanId) throws DataFormatException, NotFoundException {
		List<XnatAbstractresource> resources = new ArrayList<>();
		if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID "+ projectId  + "wasn't found ");
		}
		if(StringUtils.isBlank(subjectId)) {
    		throw new DataFormatException("The requested subject ID "+ subjectId  + "wasn't found ");
		}
		if(StringUtils.isBlank(assessorId)) {
    		throw new DataFormatException("The requested assessor ID "+ assessorId  + "wasn't found ");
		}
		if(Objects.isNull(scanId)) {
    		throw new DataFormatException("The requested scan ID "+ scanId  + "wasn't found ");
		}
		resources = getXnatAbstractResourceData(user, projectId, subjectId, null, assessorId, scanId, null);
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, scanId) ;
		}
		return resources;
	}
	
	/**
	 * Get list of resource with specified project ID , subjectId, experiment ID and assessor ID
	 */
	@Override
	public List<XnatAbstractresource> findByProjectIdAndSubjectIdAndExperimentIdAndAssessorId(UserI user, String projectId, String subjectId, String experimentId, String assessorId, String type) throws DataFormatException, NotFoundException {
		List<XnatAbstractresource> resources = new ArrayList<>();
		if(StringUtils.isBlank(projectId)) {
    		throw new DataFormatException("The requested project ID "+ projectId  + "wasn't found ");
		}
		if(StringUtils.isBlank(subjectId)) {
    		throw new DataFormatException("The requested subject ID "+ subjectId  + "wasn't found ");
		}
		if(StringUtils.isBlank(experimentId)) {
    		throw new DataFormatException("The requested experiment ID "+ experimentId  + "wasn't found ");
		}
		if(StringUtils.isBlank(assessorId)) {
    		throw new DataFormatException("The requested assessor ID "+ assessorId  + "wasn't found ");
		}
		resources = getXnatAbstractResourceData(user, projectId, subjectId, experimentId, assessorId, null, type);
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, assessorId) ;
		}
		return resources;
	}

	
	/**
	 * create new resource catalog
	 */
	@Override
	public XnatResourcecatalog create(UserI user, String projectId, String subjectId, String experimentId, String assessorId, String scanId, String type, XnatResource xnatResource, XnatEventUtil event, String description, String format, String content, String [] tags) {
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

				xnatTemplateUtil.setCatalogAttributes(user, catResource, description, format, content, tags);

				PersistentWorkflowI wrk = PersistentWorkflowUtils.getWorkflowByEventId(user, XnatEventUtil.getEventId(event.getEventId()));
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
				
				xnatTemplateUtil.insertCatalogWrap(catResource, wrk, user, proj, sub, expts, assesseds, scans, event);
				
			} else
				throw new DataFormatException("Only ResourceCatalog documents can be PUT to this address.");

		} catch (ActionException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return (XnatResourcecatalog) xnatResource;
	}

	/**
	 * delete existing resource 
	 */
	@Override
	public void delete(UserI user, String projectId, String subjectId, String experimentId, String assessorId,String scanId,String type,String resourceId1, XnatEventUtil event) {
		
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
                    final PersistentWorkflowI workflow   = PersistentWorkflowUtils.getOrCreateWorkflowData(XnatEventUtil.getEventId(event.getEventId()), user, xsiType, securityId, proj.getId(), XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.REMOVE_CATALOG + " " + resourceId, event));
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

	
	/** Start DIR Service methods*/
	
	/**
	 * Get list of DIR resources
	 */
	@Override
	public List<DIRResourceDto> findAllDIRResources(UserI user, String projectId, String experimentId, String filepath, boolean recursive, boolean isXarReference) throws NotFoundException, NotAuthenticatedException, InvalidFileCharacters {
		
		List<DIRResourceDto> response = new ArrayList<>();
		
		XnatProjectdata proj = getXnatProject(projectId, user);
		
		XnatExperimentdata expt = getXnatExperiment(experimentId, proj, user);
		
		if (user.isGuest()) {
			throw new NotAuthenticatedException("");
		}
		if(expt instanceof XnatSubjectassessordata){
			if(filepath==null){
				filepath="";
			}
			final File session_dir=expt.getSessionDir();
			if(session_dir==null){
				throw new NotFoundException("Session directory doesn't exist in standard location for this experiment.");
			}
			try {
				final List<File> src = getSourceFile(filepath, session_dir);
				
				if (!(src.size() == 1 && !src.get(0).isDirectory())) {
					final List<FileSet> dest = getFileSet(src, recursive, isXarReference);
					response =  getDIRResourceResult(dest, session_dir, expt);
				}
			} catch (InvalidFileCharacters e) {
				throw new InvalidFileCharacters(String.format("'%s' is not allowed in this resource URI.",e.characters));
			}
		}
		return response;
	}
	
	/**
	 * Download the XAR resource file 
	 */
	@Override
	public StreamingResponseBody findAllXARResources(UserI user, String projectId, String experimentId, String filepath, boolean recursive, boolean isXarReference, HttpServletRequest sRequest, HttpHeaders hRequest, String compression) throws NotFoundException, NotAuthenticatedException, InvalidFileCharacters, InitializationException {
		
		//MediaType mediaType = hRequest.getContentType() != null? hRequest.getContentType(): MediaType.parseMediaType(MediaTypeUtil.APPLICATION_XAR);
		
		MediaType mediaType =  MediaType.parseMediaType(MediaTypeUtil.APPLICATION_XAR);
		
		XnatProjectdata proj = getXnatProject(projectId, user);
		
		XnatExperimentdata expt = getXnatExperiment(experimentId, proj, user);
		
		if (user.isGuest()) {
			throw new NotAuthenticatedException("");
		}
		if(expt instanceof XnatSubjectassessordata){
			if(filepath==null){
				filepath="";
			}
			final File session_dir=expt.getSessionDir();
			if(session_dir==null){
				throw new NotFoundException("Session directory doesn't exist in standard location for this experiment.");
			}
			try {
				final List<File> src = getSourceFile(filepath, session_dir);
				
				if (src.size() == 1 && !src.get(0).isDirectory()) {
					final File f=src.get(0);
					if (isZIPRequest(mediaType)) {
						zipFileRequest(mediaType, expt, f, compression);
					}
				}else{
					final List<FileSet> dest = getFileSet(src, recursive, isXarReference);
					if ((isZIPRequest(mediaType) || (mediaType.equals(MediaType.parseMediaType(MediaTypeUtil.APPLICATION_XAR))))) {
						ZipRepresentationUtil rep = zipRepresentation(mediaType, expt, user,sRequest, compression );
						for (final FileSet fileSet : dest) {
							rep.addAll(fileSet.getMatches());
						}
						FILE_NAME = rep.getDownloadName();
						return rep;
					}
				}
			} catch (InvalidFileCharacters e) {
				throw new InvalidFileCharacters(String.format("'%s' is not allowed in this resource URI.",e.characters));
			}
		}
		return null;
	}
	
	 
	/**
	 * Return the content composition
	 */
	@Override
	public String getContentDisposition() {
		return String.format(ATTACHMENT_DISPOSITION, FILE_NAME);
	}
	
	/** End DIR Service methods*/
	
    /** Start file service  Method */
	
	/**
	 * 
	 */
	@Override
	public List<ResourceFileDto> findByProjectId(UserI user, String projectId,String[] contents,String[] formats) throws DataFormatException, NotFoundException {
		if(StringUtils.isBlank(projectId)) {
			throw new DataFormatException("The requested project ID " + projectId + "wasn't found");
		}
		List<XnatResourcecatalog> resources = _template.query(PROJECT_FILE_QUERY + BY_ID_WHERE_PROJECT, new MapSqlParameterSource("projectId", projectId), new FileRowMapper(user));
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, projectId) ;
		}
		return getResourceFileData(resources, projectId, user, contents, formats);
	}
	
	/**
	 * 
	 */
	@Override
	public List<ResourceFileDto> findBySubjectId(UserI user, String subjectId, String[] contents,String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException {
		if(StringUtils.isBlank(subjectId)) {
			throw new DataFormatException("The requested subject ID " + subjectId + "wasn't found");
		}
		List<XnatResourcecatalog> resources = _template.query(SUBJECT_QUERY + BY_WHERE + BY_ID_WHERE_FILE_SUBJECT, new MapSqlParameterSource("subjectId", subjectId), new FileRowMapper(user));
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, subjectId) ;
		}
		XnatSubjectdata subject = XnatSubjectdata.getXnatSubjectdatasById(subjectId, user, false);
		if(Objects.isNull(subject)) {
    		throw new  NotFoundException(XnatSubjectdata.SCHEMA_ELEMENT_NAME, subjectId) ;
		}
		ItemI parent = subject;
		ItemI security = subject;
		XnatProjectdata project = getXnatProjectData(parent, security, null);
		if(Objects.isNull(project)) {
			throw new  NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME) ;
		}
		return getResourceFileData(resources, project.getId(), user, contents, formats);
	}
	
	/**
	 * 
	 */
	@Override
	public List<ResourceFileDto> findByProjectIdAndSubjectId(UserI user, String projectId, String subjectId, String[] contents,String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException {
		if(StringUtils.isBlank(projectId)) {
			throw new DataFormatException("The requested project ID " +projectId+ "wasn't found");
		}
		if(StringUtils.isBlank(subjectId)) {
			throw new DataFormatException("The requested subject ID " + subjectId + "wasn't found");
		}
		List<XnatResourcecatalog> resources = _template.query(SUBJECT_QUERY + BY_ID_WHERE_PROJ + AND_WHERE + BY_ID_WHERE_FILE_SUBJECT , new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId), new FileRowMapper(user));
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, subjectId) ;
		}
		XnatSubjectdata subject = XnatSubjectdata.getXnatSubjectdatasById(subjectId, user, false);
		if(Objects.isNull(subject)) {
    		throw new  NotFoundException(XnatSubjectdata.SCHEMA_ELEMENT_NAME, subjectId) ;
		}
		ItemI parent = subject;
		ItemI security = subject;
		XnatProjectdata project = getXnatProjectData(parent, security, XnatProjectdata.getXnatProjectdatasById(projectId, user, false));
		if(Objects.isNull(project)) {
			throw new  NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME) ;
		}
		return getResourceFileData(resources, project.getId(), user, contents, formats);
	}
	
	/**
	 * 
	 */
	@Override
	public  List<ResourceFileDto>  findByProjectIdAndResourceId(UserI user, String projectId, Integer resourceId, String[] contents,String[] formats ) throws DataFormatException, NotFoundException {
		if(StringUtils.isBlank(projectId)) {
			throw new DataFormatException("The requested project ID " + projectId + "wasn't found");
		}
		if(Objects.isNull(resourceId) ) {
			throw new DataFormatException("The requested resource ID " + resourceId + "wasn't found");
		}
		List<XnatResourcecatalog> resources = _template.query(PROJECT_FILE_QUERY + BY_ID_WHERE_PROJ_AND_RESOURCE, new MapSqlParameterSource("projectId", projectId).addValue("resourceId", resourceId), new FileRowMapper(user));
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, resourceId) ;
		}
		return getResourceFileData(resources, projectId, user,contents,formats);
	}
	

	/**
	 * 
	 */
	@Override
	public List<ResourceFileDto> findBySubjectIdAndResourceId(UserI user, String subjectId, Integer resourceId,String[] contents,String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException {
		if(StringUtils.isBlank(subjectId)) {
			throw new DataFormatException("The requested subject ID " + subjectId + "wasn't found");
		}
		if(Objects.isNull(resourceId) ) {
			throw new DataFormatException("The requested resource ID " + resourceId + "wasn't found");
		}
		XnatSubjectdata subject = XnatSubjectdata.getXnatSubjectdatasById(subjectId, user, false);
		if(Objects.isNull(subject)) {
    		throw new  NotFoundException(XnatSubjectdata.SCHEMA_ELEMENT_NAME, subjectId) ;
		}
		
		List<XnatResourcecatalog> resources = _template.query(SUBJECT_RESOURCE_QUERY + BY_ID_WHERE_SUBJ_AND_RESOURCE, new MapSqlParameterSource("subjectId", subjectId).addValue("resourceId", resourceId), new FileRowMapper(user));
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, resourceId) ;
		}
		ItemI parent = subject;
		ItemI security = subject;
		XnatProjectdata project = getXnatProjectData(parent, security, null);
		if(Objects.isNull(project)) {
			throw new  NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME) ;
		}
		return getResourceFileData(resources, project.getId(), user, contents, formats);
	}
	
	/**
	 * 
	 */
	@Override
	public List<ResourceFileDto> findByExperimentIdAndAssessorId(UserI user, String experimentId, String assessorId, String[] contents,String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException {
		if(StringUtils.isBlank(experimentId)) {
			throw new DataFormatException("The requested experiment ID " + experimentId + "wasn't found");
		}
		if(StringUtils.isBlank(assessorId)) {
			throw new DataFormatException("The requested assessor ID " +  assessorId + "wasn't found");
		}
		List<XnatResourcecatalog> resources = _template.query(EXPERIMENT_ASSESSER_FILE_QUERY + BY_ID_WHERE_EXP_AND_ASSESSER, new MapSqlParameterSource("experimentId", experimentId).addValue("assessorId", assessorId), new FileRowMapper(user));
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, assessorId) ;
		}
		XnatExperimentdata experiment = XnatExperimentdata.getXnatExperimentdatasById(assessorId, user, false);
		if(Objects.isNull(experiment)) {
    		throw new  NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME, assessorId) ;
		}
		ItemI parent = experiment;
		ItemI security = experiment;
		XnatProjectdata project = getXnatProjectData(parent, security, null);
		if(Objects.isNull(project)) {
			throw new  NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME) ;
		}
		return getResourceFileData(resources, project.getId(), user, contents, formats);
	}
	
	/**
	 * 
	 */
	@Override
	public List<ResourceFileDto> findByExperimentIdAndAssessorIdAndResourceId(UserI user, String experimentId, String assessorId, Integer resourceId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException {
		if(StringUtils.isBlank(experimentId)) {
			throw new DataFormatException("The requested experiment ID " + experimentId + "wasn't found");
		}
		if(StringUtils.isBlank(assessorId)) {
			throw new DataFormatException("The requested assessor ID " +  assessorId + "wasn't found");
		}
		List<XnatResourcecatalog> resources = _template.query(EXPERIMENT_ASSESSER_FILE_QUERY + BY_ID_WHERE_EXP_AND_ASSESSER_AND_RESOURCE, new MapSqlParameterSource("experimentId", experimentId).addValue("assessorId", assessorId).addValue("resourceId", resourceId), new FileRowMapper(user));
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, assessorId) ;
		}
		XnatExperimentdata experiment = XnatExperimentdata.getXnatExperimentdatasById(assessorId, user, false);
		if(Objects.isNull(experiment)) {
    		throw new  NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME, assessorId) ;
		}
		ItemI parent = experiment;
		ItemI security = experiment;
		XnatProjectdata project = getXnatProjectData(parent, security, null);
		if(Objects.isNull(project)) {
			throw new  NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME) ;
		}
		return getResourceFileData(resources, project.getId(), user, contents, formats);
	}
	
	/**
	 * 
	 */
	@Override
	public List<ResourceFileDto> findByProjectIdAndSubjectIdAndExperimentId(UserI user, String projectId, String subjectId, String experimentId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException {
		List<XnatResourcecatalog> resourceCatalog= new ArrayList<>();
		if(StringUtils.isBlank(projectId)) {
			throw new DataFormatException("The requested project ID " + projectId + "wasn't found");
		}
		if(StringUtils.isBlank(subjectId)) {
			throw new DataFormatException("The requested subject ID " + subjectId + "wasn't found");
		}
		if(StringUtils.isBlank(experimentId)) {
			throw new DataFormatException("The requested experimentId ID " + experimentId + "wasn't found");
		}
		List<XnatAbstractresource> resources = findByProjectIdAndSubjectIdAndExperimentId(user, projectId, subjectId, experimentId);
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, experimentId) ;
		}
		for (final XnatAbstractresource temp : resources) {
			final XnatResourcecatalog catResource = (XnatResourcecatalog) temp;
			resourceCatalog.add(catResource);
		}
		XnatExperimentdata experiment = XnatExperimentdata.getXnatExperimentdatasById(experimentId, user, false);
		if(Objects.isNull(experiment)) {
    		throw new  NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME, experimentId) ;
		}
		ItemI parent = experiment;
		ItemI security = experiment;
		XnatProjectdata project = getXnatProjectData(parent, security, null);
		if(Objects.isNull(project)) {
			throw new  NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME) ;
		}
		return getResourceFileData(resourceCatalog, project.getId(), user, contents, formats);
	}
	
	/**
	 * 
	 */
	@Override
	public List<ResourceFileDto> findByProjectIdAndSubjectIdAndExperimentIdAndAssessorId(UserI user,String projectId, String subjectId, String experimentId, String assessedId, String[] contents,String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException {
		if(StringUtils.isBlank(projectId)) {
			throw new DataFormatException("The requested project ID " + projectId + "wasn't found");
		}
		if(StringUtils.isBlank(subjectId)) {
			throw new DataFormatException("The requested subject ID " + subjectId + "wasn't found");
		}
		if(StringUtils.isBlank(experimentId)) {
			throw new DataFormatException("The requested experimentId ID " + experimentId + "wasn't found");
		}
		if(StringUtils.isBlank(assessedId)) {
			throw new DataFormatException("The requested assessed ID " + assessedId + "wasn't found");
		}
		List<XnatResourcecatalog> resources = _template.query(PRO_SUB_EXP_ASS_FILE_QUERY + BY_WHERE_PRO_SUB_EXP_ASS  , new MapSqlParameterSource("projectId", projectId).addValue("subjectId", subjectId).addValue("experimentId", experimentId).addValue("assessedId", assessedId), new FileRowMapper(user));
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, assessedId) ;
		}
		XnatExperimentdata experiment = XnatExperimentdata.getXnatExperimentdatasById(assessedId, user, false);
		if(Objects.isNull(experiment)) {
    		throw new  NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME, assessedId) ;
		}
		ItemI parent = experiment;
		ItemI security = experiment;
		XnatProjectdata project = getXnatProjectData(parent, security, null);
		if(Objects.isNull(project)) {
			throw new  NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME) ;
		}
		return getResourceFileData(resources, project.getId(), user, contents, formats);
	}
	
	/**
	 * 
	 */
	@Override
	public List<ResourceFileDto> findByExperimentId(UserI user, String experimentId, String[] contents,String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException {
		if(Objects.isNull(experimentId)) {
			throw new DataFormatException("The requested experiment ID " + experimentId + "wasn't found");
		}
		List<XnatResourcecatalog> resources = _template.query(EXP_FILE_QUERY, new MapSqlParameterSource("experimentId", experimentId), new FileRowMapper(user));
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, experimentId) ;
		}
		XnatExperimentdata  expriment = XnatExperimentdata.getXnatExperimentdatasById(experimentId, user, false);
		if(Objects.isNull(expriment)) {
    		throw new  NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME, experimentId) ;
		}
		ItemI parent = expriment;
		ItemI security = expriment;
		XnatProjectdata project = getXnatProjectData(parent, security, null);
		if(Objects.isNull(project)) {
			throw new  NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME) ;
		}
		return getResourceFileData(resources, project.getId(), user, contents, formats);
	}

	/**
	 * 
	 */
	@Override
	public List<ResourceFileDto> findByExperimentIdAndResourceId(UserI user, String experimentId, Integer resourceId, String[] contents,String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException {
		if(StringUtils.isBlank(experimentId)) {
			throw new DataFormatException("The requested experiment ID " + experimentId + "wasn't found");
		}
		if( Objects.isNull(resourceId) ) {
			throw new DataFormatException("The requested resource ID " + resourceId + "wasn't found");
		}
		List<XnatResourcecatalog> resources = _template.query(EXP_RESOURCE_QUERY, new MapSqlParameterSource("experimentId", experimentId).addValue("resourceId", resourceId), new FileRowMapper(user));
		if(Objects.isNull(resources) || resources.isEmpty()) {
    		throw new  NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, resourceId) ;
		}
		XnatExperimentdata  expriment = XnatExperimentdata.getXnatExperimentdatasById(experimentId, user, false);
		if(Objects.isNull(expriment)) {
    		throw new  NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME, experimentId) ;
		}
		ItemI parent = expriment;
		ItemI security = expriment;
		XnatProjectdata project = getXnatProjectData(parent, security, null);
		if(Objects.isNull(project)) {
			throw new  NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME) ;
		}
		return getResourceFileData(resources, project.getId(), user, contents, formats);
	}
	
	/**
	 * Delete the files from specific resource
	 */
	@Override
	public void deleteResourceFile(UserI user, String projectId,String subjectId, String experimentId, String assessorId, String scanId, String type,String resourceId, boolean removeFiles, XnatEventUtil event) throws Exception {
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
		PersistentWorkflowI work = WorkflowUtils.getOrCreateWorkflowData(XnatEventUtil.getEventId(event.getEventId()), user, security.getItem(),XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.REMOVE_FILE,event));

		// Step 9: delete resource file
		deleteResourceFiles(work, catalogData, entries, user, removeFiles);

	}
	
	/**
	 * Create resource file and upload into the specific resource
	 */
	@Override
	public Integer createResourceFile(UserI user, XnatResourceInfo xnatResourceInfo, String projectId,String subjectId, String experimentId, String assessorId, String scanId, String type,String resourceId, XnatEventUtil event) throws Exception{
		
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
			XnatAbstractresource xnatAbstractresource = null;

			xnatAbstractresource = getResourceData(user, _resourceIds);
		
		// step 4:
			if (parent != null && security != null) {
				if (Permissions.canEdit(user, security)) {
					Integer result=  resourceFileUpload(xnatAbstractresource, user, projectId, resourceId,xnatResourceInfo,event);
					if(Objects.nonNull(result))
						return result;
					else
						throw new InitializationException("Please check ..File Not Uploaded");
						
			}
		}
			throw new InitializationException("Please check ... File Not Uploaded");
	}

	
	/** End File service methods */
	
	/** Start refresh catalog service methods */
	/**
	 * 
	 */
	@Override
	public void createCatalogRefresh(UserI user,List<String> resources, boolean append, boolean checksum, boolean delete, boolean populateStats, List<String> options) throws ClientException, ServerException {
		_catalogService = XDAT.getContextService().getBean(CatalogService.class);
		
		loadValues(resources, append , checksum, delete , populateStats, options);
		
		_catalogService.refreshResourceCatalogs(user, _resources, _operations.toArray(new CatalogService.Operation[_operations.size()]));
	}
	/** End refresh catalog service methods */
	
	
	/** Start Triage service methods */
	/**
	 * 
	 */
	@Override
	public List<TriageDto> findTriageByProjectId(UserI user, String projectId, HttpServletRequest request) {
		String projectPath = TriageUtils.getTriageProjectPath(projectId);
		XnatProjectdata proj = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
		return returnXnameList(proj, projectPath + File.separator + "resources", request);
	}

	/**
	 * 
	 * @param proj
	 * @param projectPath
	 * @param request
	 * @return
	 */
	private List<TriageDto> returnXnameList(XnatProjectdata proj, String projectPath, HttpServletRequest request) {
		List<TriageDto> response = new ArrayList<>();
		File[] fileArray = new File(projectPath).listFiles();
		if (fileArray != null) {
			for (File f : fileArray) {
				String fn = f.getName();
				response.add(getTriageUtil(fn, f, request));
			}
		}
		// sendTableRepresentation : // Pending IMPL
		return response;
	}
	
	/**
	 * 
	 */
	@Override
	public void findTriagefilesByProjectIdAndXname(UserI user, String projectId, String xName,HttpServletRequest request, String compression) throws Exception {
		String projectPath = TriageUtils.getTriageProjectPath(projectId);
		MediaType mt = MediaType.parseMediaType(request.getContentType());
		XnatProjectdata proj = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
		if (proj != null && proj.canRead(user)) {
			if (xName != null) {
				if (isZIPRequest(mt)) {
					returnZippedFiles(proj, projectPath, xName, user, request, compression);
				} else {
					returnFileList(proj, projectPath, xName, request);
				}
			}
		}
	}

	/**
	 * 
	 */
	@Override
	public void findTriageByProjectIdAndXname(UserI user, String projectId, String xName, String file, HttpServletRequest request, String compression) throws InvalidItemException, NotFoundException, InsufficientPrivilegesException, ActionException, Exception {
		String projectPath = TriageUtils.getTriageProjectPath(projectId);
		MediaType mt = MediaType.parseMediaType(request.getContentType());
		XnatProjectdata proj = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
		if (proj != null && proj.canRead(user)) {
			if (xName != null) {
				if (isZIPRequest(mt)) {
					returnZippedFiles(proj, projectPath, xName, user, request, compression);
				} else {
					returnFile(proj, projectPath, xName, file, user);
				}
			}
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void create(UserI user, String projectId, String xname, String file, String eventReason, String eventComment, String eventId, String target, boolean inbody, String overwrite, String format, String content, String event_reason, String extract, HttpServletRequest request) {
		try {
			String projectPath = TriageUtils.getTriageUploadsPath();
			if (projectId == null) {
				// fail(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid Operation."); //Pending IMPL
			}
			XnatProjectdata proj = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
			if (proj != null && proj.canRead(user) && canEditDestination(target, user)) {
				if (xname == null && file == null) {
					// fail(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid Operation.");
				} else if (xname != null && file == null) {
					uploadTriageFile(projectPath, xname, null, inbody, target, overwrite, format, content, event_reason,
							extract, request, user);
				} else if (xname != null && file != null) {
					uploadTriageFile(projectPath, getxName(projectId, request), file, inbody, target, overwrite, format, content, event_reason, extract, request, user);
				}
				openworkflow(true, "Upload Quarantine Files", eventReason, eventComment, user, projectId);

				// XNATCR-834: stops IE opening save dialog
				// this.getResponse().setEntity("",MediaType.TEXT_HTML); //Pending IMPL
			} else {
				// fail(Status.CLIENT_ERROR_UNAUTHORIZED,"Not authorized");
			}
		} catch (Exception e) {
			// fail(Status.SERVER_ERROR_INTERNAL,e.getMessage());
			log.error("", e);
		}
	}

	/**
	 * 
	 */
	@Override
	public void updte(UserI user, String projectId, String xname, String file, String eventReason, String eventComment, String eventId, String target, boolean inbody, String overwrite, String format, String content, String event_reason, String extract, HttpServletRequest request) {
		try {
			String projectPath = TriageUtils.getTriageUploadsPath();
			if (projectId == null) {
				// fail(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid Operation."); //Pending IMPL
			}
			XnatProjectdata proj = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
			if (proj != null && proj.canRead(user) && canEditDestination(target, user)) {
				if (xname == null && file == null) {
					// fail(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid Operation.");
				} else if (xname != null && file == null) {
					createTriageResource(projectPath, xname, request);
					// uploadTriageFile(projectPath,xname, null,
					// inbody,target,overwrite,format,content,event_reason,extract, request,user);
				} else if (xname != null && file != null) {
					uploadTriageFile(projectPath, getxName(projectId, request), file, inbody, target, overwrite, format, content, event_reason, extract, request, user);
				}
				openworkflow(true, "Upload Quarantine Files", "Upload Quarantine Files", "Upload Quarantine Files",
						user, projectId);
			} else {
				// fail(Status.CLIENT_ERROR_UNAUTHORIZED,"Not authorized");
			}
		} catch (Exception e) {
			// fail(Status.SERVER_ERROR_INTERNAL,e.getMessage());
			log.error("", e);
		}
	}
	
	/** End Triage service methods*/
	
	
	/** Start resource private methods */
	/**
	 * 
	 * @param user
	 * @param projectId
	 * @param subjectId
	 * @param experimentId
	 * @param assessorId
	 * @param scanId
	 * @param type
	 * @return
	 */
	private List<XnatAbstractresource> getXnatAbstractResourceData(UserI user, String projectId, String subjectId, String experimentId, String assessorId,String scanId, String type){
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
		
		return getXnatAbstractResources(proj, sub, expts,assesseds,scans,user);
	}
	
	/**
	 * 
	 * @param proj
	 * @param sub
	 * @param expts
	 * @param assesseds
	 * @param scans
	 * @param user
	 * @return
	 */
	private List<XnatAbstractresource> getXnatAbstractResources(XnatProjectdata proj, XnatSubjectdata sub, ArrayList<XnatExperimentdata> expts, ArrayList<XnatExperimentdata> assesseds, ArrayList<XnatImagescandata> scans, UserI user) {
		 return _template.query(getSqlQuery(proj, sub , expts,assesseds,scans, user), new ResourceRowMapper(user));
	}

	/**
	 * 
	 * @param proj
	 * @param sub
	 * @param expts
	 * @param assesseds
	 * @param scans
	 * @param user
	 * @return
	 */
	private String getSqlQuery(XnatProjectdata proj, XnatSubjectdata sub, ArrayList<XnatExperimentdata> expts, ArrayList<XnatExperimentdata> assesseds, ArrayList<XnatImagescandata> scans, UserI user) {
		List<String> resourceIds = null;
		final boolean hasResourceIds = resourceIds != null && !resourceIds.isEmpty();
		final boolean isInResource = StringUtils.equalsIgnoreCase(type, "in");
		StringBuilder query = new StringBuilder();
		 if (assesseds.size() > 0 || expts.size() > 0 || scans.size() > 0 || sub != null || proj != null) {
	            try {
	                 query = getFinalQuery(null, false, true, user, hasResourceIds, isInResource);
	            } catch (Exception e) {
	                log.error("", e);
	            }
	        }
		return query.toString();
	}
	
	/**
	 * 
	 * @param resourceIds
	 * @param proj
	 * @param sub
	 * @param expts
	 * @param assesseds
	 * @param scans
	 * @param user
	 * @return
	 */
	private String getSqlQueryWithResourceIds(List<String> resourceIds, XnatProjectdata proj, XnatSubjectdata sub, ArrayList<XnatExperimentdata> expts, ArrayList<XnatExperimentdata> assesseds, ArrayList<XnatImagescandata> scans, UserI user) {
		final boolean hasResourceIds = resourceIds != null && !resourceIds.isEmpty();
		final boolean isInResource = StringUtils.equalsIgnoreCase(type, "in");
		StringBuilder query = new StringBuilder();
		 if (assesseds.size() > 0 || expts.size() > 0 || scans.size() > 0 || sub != null || proj != null) {
	            try {
	                 query = getFinalQuery(resourceIds, false, true, user, hasResourceIds, isInResource);
	            } catch (Exception e) {
	                log.error("", e);
	            }
	        }
		return query.toString();
	}
	
	/**
	 * 
	 * @param user
	 * @param experimentId
	 * @param assessorId
	 * @param type
	 * @param resourceId
	 * @return
	 */
	private XnatAbstractresource getXnatResource(UserI user, String experimentId, String assessorId, String type, Integer resourceId)  {
		
		expts = new ArrayList<>();
		assesseds = new ArrayList<>();
		
		String id = String.valueOf(resourceId);
		
		_resourceIds = setResourcesIds(id, user, false);
		
		if (Objects.nonNull(assessorId)) 
			assesseds = getXnatAssessordata(assessorId, user, proj);
		if(Objects.nonNull(experimentId)) 
			expts = getXnatExperimentData(experimentId, user,assesseds, type);
		
		 return _template.queryForObject(getSqlQueryWithResourceIds(_resourceIds, null, null, expts, assesseds, null, user), new MapSqlParameterSource(),new ResourceRowMapper(user));
		}
	
	
	/**
	 * 
	 * @param xsiType
	 * @param securityId
	 * @param securityItem
	 * @param parentItem
	 * @return
	 */
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

	/**
	 * 
	 * @param securityItem
	 * @param parentItem
	 * @return
	 */
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

	/**
	 * 
	 * @param user
	 * @param securityItem
	 * @param parentItem
	 */
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

	/**
	 * 
	 * @param user
	 * @param securityItem
	 * @throws Exception
	 */
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
	
	/**
	 * 
	 * @param parent
	 * @param security
	 * @return
	 * @throws ElementNotFoundException
	 */
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
	 
	 /**
	  * 
	  */
	 private static final Function<XnatAbstractresource, String> RESOURCE_TO_STRING_FUNCTION = new Function<XnatAbstractresource, String>() {
	        @Override
	        public String apply(final XnatAbstractresource resource) {
	            return getResourceDisplay(resource);
	        }
	    };
	    
	    /**
	     * 
	     * @param resource
	     * @return
	     */
	    @Nonnull
	    private static String getResourceDisplay(final XnatAbstractresource resource) {
	        final String resourceLabel = resource.getLabel();
	        return resource.getXnatAbstractresourceId() + (StringUtils.isBlank(resourceLabel) ? "" : " (" + resourceLabel + ")");
	    }
	 
	/**    
	 * 
	 * @author afour
	 *
	 */
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
	/** End Resource service Private Method */
	
	
	
	/** Start DIR Resource service Private Method */

	/**
	 *  
	 * @author afour
	 *
	 */
	public static class InvalidFileCharacters extends Exception {
		public String characters;

		public InvalidFileCharacters(String chars) {
			characters = chars;
		}
	}
	
	/**
	 * 
	 * @param mt
	 * @return
	 */
	public static boolean isZIPRequest(MediaType mt) {
		MediaType zip = MediaType.parseMediaType(MediaTypeUtil.APPLICATION_ZIP);
		MediaType tar = MediaType.parseMediaType(MediaTypeUtil.APPLICATION_TAR);
		return !(mt == null || !(mt.equals(zip)) || mt.equals(tar));
	}
	
	/**
	 * 
	 * @param dest
	 * @param session_dir
	 * @param expt
	 * @return
	 */
	private List<DIRResourceDto> getDIRResourceResult(List<FileSet> dest, File session_dir, XnatExperimentdata expt) {
		List<DIRResourceDto> response = new ArrayList<>();
		for(final FileSet fs:dest){
			final File parent=fs.getParent();
			for(final File f:fs.getMatches()){
				final String rel=(session_dir.toURI().relativize(f.toURI())).getPath();
				response.add(DIRResourceDto.builder()
						.DIR(f.isDirectory())
						.size(f.length())
						.name(parent.toURI().relativize(f.toURI()).getPath())
						.URI(String.format("/data/experiments/%1$s/DIR/%2$s%3$s", expt.getId(), rel, f.isDirectory() ? "?format=json" : ""))
						.build());
			}
		}
		return response;
	}

	/**
	 * 
	 * @param filepath
	 * @param session_dir
	 * @return
	 * @throws InvalidFileCharacters
	 * @throws NotFoundException
	 */
	private List<File> getSourceFile(String filepath, File session_dir) throws InvalidFileCharacters, NotFoundException {
		List<File> src = new ArrayList<>();
		if(filepath.equals("")){
			src= new ArrayList<>();
			src.add(session_dir);
		}else{
			src=getFiles(session_dir,filepath,true);
		}
		if(src.size()==0){
			throw new NotFoundException("Specified request didn't match any stored files.");
		}
		return src;
	}

	/**
	 * 
	 * @param experimentId
	 * @param proj
	 * @param user
	 * @return
	 * @throws NotFoundException
	 */
	private XnatExperimentdata getXnatExperiment(String experimentId, XnatProjectdata proj, UserI user) throws NotFoundException {
		XnatExperimentdata expt= null;
		if (StringUtils.isNotBlank(experimentId)) {
			expt=XnatExperimentdata.getXnatExperimentdatasById(experimentId, user, false);
			if(Objects.isNull(expt) && Objects.nonNull(proj)){
				expt= XnatExperimentdata.GetExptByProjectIdentifier(proj.getId(), experimentId, user, false);
			}
		}
		if(Objects.isNull(expt)) {
			throw new NotFoundException("");
		}
		return expt;
	}

	/**
	 * 
	 * @param projectId
	 * @param user
	 * @return
	 */
	private XnatProjectdata getXnatProject(String projectId, UserI user) {
		XnatProjectdata proj = null;
		if(StringUtils.isNotBlank(projectId)) {
			proj = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
		}
		return proj;
	}

	/**
	 * 
	 * @param src
	 * @param recursive
	 * @param isXarReference
	 * @return
	 */
	private List<FileSet> getFileSet(List<File> src, boolean recursive, boolean isXarReference) {
		final List<FileSet> dest= new ArrayList<>();
		for(File f: src){
			final FileSet set=new FileSet(f);
			if(f.isDirectory()){
				if(recursive || isXarReference) { 
					set.addAll(FileUtils.listFiles(f, null, true));
				}else{
					File[] children=f.listFiles();
					if(children!=null){
						set.addAll(Arrays.asList(children));
					}
				}
			}else{
				set.add(f);
			}
			dest.add(set);
		}
		return dest;
	}

	/**
	 * 
	 * @param mediaType
	 * @param expt
	 * @param f
	 * @param compression
	 * @return
	 */
	private ZipRepresentationUtil zipFileRequest(MediaType mediaType, XnatExperimentdata expt, File f, String compression) {
		if(isZIPRequest(mediaType)){
			ZipRepresentationUtil rep = null;
			try{
				rep=new ZipRepresentationUtil(mediaType,Collections.singletonList((expt).getArchiveDirectoryName()),identifyCompression(null, compression));
			} catch (ActionException e) {
				log.error("", e);
			}
			rep.addEntry(f);
			FILE_NAME = String.format("%s.zip", f.getName());
			return rep;
		}else{
			//return this.representFile(f, mediaType);
		}
		return null;
	}

	/**
	 * 
	 * @param mediaType
	 * @param expt
	 * @param user
	 * @param request
	 * @param compression
	 * @return
	 * @throws InitializationException
	 */
	private ZipRepresentationUtil zipRepresentation(MediaType mediaType, XnatExperimentdata expt, UserI user, HttpServletRequest request, String compression ) throws InitializationException {
		ZipRepresentationUtil rep = null;
		try{
			rep=new ZipRepresentationUtil(mediaType,Collections.singletonList((expt).getArchiveDirectoryName()),identifyCompression(null, compression));
		} catch (ActionException e) {
			log.error("", e);
		}
		if (mediaType.equals(MediaType.parseMediaType(MediaTypeUtil.APPLICATION_XAR))) {
			final File output = getUserDataCache().getUserDataCacheFile(user, Paths.get("expt_" + new Date().getTime()), UserDataCache.Options.DeleteOnExit, UserDataCache.Options.Overwrite);
			log.info("Getting ready to write item XML to the file {}", output.getAbsolutePath());

			try (final OutputStream outputStream = new FileOutputStream(output)) {
				final SAXWriter writer = new SAXWriter(outputStream, true);
				writer.setAllowSchemaLocation(true);
				writer.setLocation(TurbineUtils.GetRelativePath(request) + "/" + "schemas/");
				writer.setRelativizePath(expt.getArchiveDirectoryName() + "/");
				writer.write(expt.getItem());
				rep.addEntry(expt.getId() + ".xml", output);
			} catch (Exception e) {
				throw new InitializationException("Unable to retrieve/save session XML.");
			}
		}
		return rep;
	}

	/**
	 * 
	 * @param defaultCompression
	 * @param compression
	 * @return
	 * @throws ActionException
	 */
	private Integer identifyCompression(Integer defaultCompression, String compression) throws ActionException {
        try {
            if (StringUtils.isNoneBlank(compression)) {
                return Integer.valueOf(compression);
            }
        } catch (NumberFormatException e) {
            throw new ClientException(e.getMessage());
        }

        if (defaultCompression != null) {
            return defaultCompression;
        } else {
            return ZipUtils.DEFAULT_COMPRESSION;
        }
    }
	
	/**
	 * 
	 * @return
	 */
	private UserDataCache getUserDataCache() {
		_userDataCache = XDAT.getContextService().getBean(UserDataCache.class);
		return _userDataCache;
	}

	/**
	 * 
	 * @param dir
	 * @param path
	 * @param recursive
	 * @return
	 * @throws InvalidFileCharacters
	 */
	private static List<File> getFiles(File dir,String path,boolean recursive) throws InvalidFileCharacters{
		final List<File> files= new ArrayList<>();
		final int slash=path.indexOf("/");
		if(slash>-1){
			final String local=path.substring(0,slash);
			
			if(path.length()>(slash+1)){
				path=path.substring(slash+1);
			}else{
				recursive=false;
			}
			
			if(path.trim().equals("..")){
				throw new InvalidFileCharacters("..");
			}
			
			final GlobFilenameFilter glob = new GlobFilenameFilter(local);
			final String[] children=dir.list(glob);
			if (children != null) {
				for(final String child:children){
					final File f=new File(dir,child);
					if(recursive && f.isDirectory()){
						files.addAll(getFiles(f,path,true));
					}else{
						files.add(f);
					}
				}
			}

		}else{
			if(path.trim().equals("..")){
				throw new InvalidFileCharacters("..");
			}
			final GlobFilenameFilter glob = new GlobFilenameFilter((path.equals(""))?"*":path);
			final String[] children=dir.list(glob);
			if (children != null) {
				for (final String child : children) {
					files.add(new File(dir, child));
				}
			}
		}
		
		return files;
	}
	
	/** End DIR Resource private Methods */
	
	
	
	/** Start File service private methods */
	/**
	 * 
	 * @param work
	 * @param catalogData
	 * @param entries
	 * @param user
	 * @param removeFiles 
	 * @throws Exception
	 */
	private void deleteResourceFiles(PersistentWorkflowI work, CatalogData catalogData, Collection<CatEntryI> entries, UserI user, boolean removeFiles) throws Exception {
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
                    historyMap, !removeFiles);

            if (StringUtils.equals(XnatProjectdata.SCHEMA_ELEMENT_NAME, parent.getXSIType())) {
                XDAT.triggerXftItemEvent(XnatProjectdata.SCHEMA_ELEMENT_NAME, parent.getStringProperty("ID"),
                        XftItemEventI.DELETE);
            }
        } finally {
            WorkflowUtils.complete(work, work.buildEvent());
        }
		
	}

	/**
	 * Validate the resource
	 * 
	 * @param user
	 * @param resource
	 * @throws Exception
	 */
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

	/**
	 * Verify the project is null 
	 * 
	 * @throws ElementNotFoundException
	 */
	private void verifyProjIsNull() throws ElementNotFoundException {
		 if (proj == null) {
             if (parent.getItem().instanceOf("xnat:experimentData")) {
                 proj = ((XnatExperimentdata) parent).getPrimaryProject(false);
             } else if (security.getItem().instanceOf("xnat:experimentData")) {
                 proj = ((XnatExperimentdata) security).getPrimaryProject(false);
             }
         }
	}
	
	
	/**
	 * 
	 * 
	 * @param xnatAbstractresource
	 * @param user
	 * @param projectId
	 * @param resourceId
	 * @param xnatResourceInfo
	 * @return
	 * @throws Exception
	 */
	private Integer resourceFileUpload(XnatAbstractresource xnatAbstractresource, UserI user, String projectId, String resourceId, XnatResourceInfo xnatResourceInfo, XnatEventUtil event) throws Exception {
		
		verifyProjectIsNull();
		
		final Object resourceIdentifier = verifyResourceIsNull(xnatAbstractresource);
		
		final boolean overwrite = false; // HC
		final boolean extract = false; // HC

		PersistentWorkflowI workflow = PersistentWorkflowUtils.getWorkflowByEventId(user, XnatEventUtil.getEventId(event.getEventId()));

		workflow = verifyAndGetWorkflow(workflow, xnatAbstractresource, user);

		final boolean skipUpdateStats = false; // HC

		boolean isNew = false;

		if (workflow == null && !skipUpdateStats) {
			isNew = true;
			workflow = PersistentWorkflowUtils.buildOpenWorkflow(user, getSecurityItem().getItem(), XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, (event.getEventAction() != null) ? event.getEventAction() : EventUtils.UPLOAD_FILE, event));
		}

		final EventMetaI eventMeta = getEventMetaI(workflow, user);

		final UpdateMeta updateMeta = new UpdateMeta(eventMeta, !(skipUpdateStats));
		
		workflow = uploadFile(xnatResourceInfo,overwrite, updateMeta, user, projectId, workflow, resourceIdentifier, extract, isNew, type);
	
		if (StringUtils.isBlank(reference) && workflow != null && isNew) {
			WorkflowUtils.complete(workflow, eventMeta);
			return XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(resourceId, user, false).getXnatAbstractresourceId();
	}
		return null;
}

	/**
	 * Upload a resource file
	 * 
	 * @param xnatResourceInfo
	 * @param overwrite
	 * @param updateMeta
	 * @param user
	 * @param projectId
	 * @param workflow
	 * @param resourceIdentifier
	 * @param extract
	 * @param isNew
	 * @return
	 * @throws Exception 
	 */
	private PersistentWorkflowI uploadFile(XnatResourceInfo xnatResourceInfo, boolean overwrite, UpdateMeta updateMeta, UserI user, String projectId, PersistentWorkflowI workflow, Object resourceIdentifier, boolean extract, boolean isNew, String type) throws Exception {
			 final List<FileWriterWrapperI> writers = getFileWriters(user,xnatResourceInfo);
			 if (writers == null || writers.isEmpty()) {
                  if (xnatResourceInfo.getFileSize() == 0) {
                  	throw new DataFormatException("You tried to upload file " + xnatResourceInfo.getName() + " to this service, but didn't provide any data (found request entity size of 0). Please check the format of your service request.");
                  } else {
                  	throw new DataFormatException("You tried to upload file " + xnatResourceInfo.getName() + " a payload of " + CatalogUtils.formatSize(xnatResourceInfo.getFileSize()) + " to this service, but didn't provide any data. If you think you sent data to upload, you can try to upload file " + xnatResourceInfo.getName() + " with the query-string parameter inbody=true or use multipart/form-data encoding.");
                  }
              }

			final ResourceModifierA resourceModifier = buildResourceModifier(overwrite, updateMeta, user);
			if (!async || StringUtils.isBlank(reference)) {
				filePath = xnatResourceInfo.getRename();
                  final List<String> duplicates = resourceModifier.addFile(writers, resourceIdentifier, type, filePath, buildResourceInfo(updateMeta, xnatResourceInfo, user), extract);
                  if (!overwrite && duplicates.size() > 0) {
                  	 isNew = false;
                  	throw new ResourceAlreadyExistsException("duplicate file", "");
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
		return workflow;
	}

	/**
	 * Get the FileWriterWrapperI Object
	 * 
	 * @param user
	 * @param info
	 * @return
	 */
	private List<FileWriterWrapperI> getFileWriters(final UserI user, final XnatResourceInfo info) {
		return Collections.singletonList(XnatResourceInfo.copy(info).username(user.getUsername()).build());
	}

	/**
	 * 
	 * @param workflow
	 * @param user
	 * @return
	 */
	private EventMetaI getEventMetaI(PersistentWorkflowI workflow, UserI user) {
		final EventMetaI eventMeta;
		if (workflow == null) {
			eventMeta = EventUtils.DEFAULT_EVENT(user, null);
		} else {
			eventMeta = workflow.buildEvent();
		}
		return eventMeta;
	}

	/**
	 * 
	 * 
	 * @param workflow
	 * @param resource
	 * @param user
	 * @return
	 */
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

	/**
	 * 
	 * 
	 * @param resource
	 * @return
	 */
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

	/**
	 * 
	 * 
	 * @throws ElementNotFoundException
	 */
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
	
	private XnatProjectdata getXnatProjectData(ItemI parent,ItemI security, XnatProjectdata proj) throws ElementNotFoundException {
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
		return proj;
	}
	
	/**
	 * 
	 * @param resources
	 * @param projectId
	 * @param user
	 * @param contents
	 * @param formats
	 * @return
	 */
	private List<ResourceFileDto> getResourceFileData(List<XnatResourcecatalog> resources, String projectId, UserI user,String[] contents, String[] formats) {
		List<ResourceFileDto>  results = new ArrayList<ResourceFileDto>();
			for(XnatResourcecatalog resource : resources) {
			CatalogData catalogData = null;
			try {
				catalogData = CatalogData.getOrCreateAndClean(XnatProjectdata.getProjectByIDorAlias(projectId, user, false).getRootArchivePath(), resource, false, projectId);
			} catch (ServerException e) {
				e.printStackTrace();
			}
			final CatCatalogBean cat = catalogData.catBean;
			final String parentPath = catalogData.catPath;
			XNATCatalogTemplateUtil tmp = new XNATCatalogTemplateUtil();
			String baseURI = tmp.getBaseURI();
			final CatalogUtils.CatEntryFilterI entryFilter = buildFilter(contents, formats);
	        List<Object[]> objects= CatalogUtils.getEntryDetails(cat, parentPath, baseURI + "/resources/" + resource.getXnatAbstractresourceId() + "/files", resource, false, entryFilter, proj, "URI");
	        results = getListObjectData(objects, results);
			}
		
		
		return results;
		
	}
	
	/**
	 * 
	 * @param objects
	 * @param results
	 * @return
	 */
	private List<ResourceFileDto> getListObjectData(List<Object[]> objects, List<ResourceFileDto> results) {
		objects.forEach(object ->{
        	results.add(ResourceFileDto.builder()
        		.name(object[0].toString())
        		.size(Integer.parseInt(object[1].toString()))
        		.uri(object[2].toString())
        		.collection(object[3].toString())
        		.fileTags(object[4].toString())
        		.fileFormat(object[5].toString())
        		.fileContent(object[6].toString())
        		.catId(Integer.parseInt(object[7].toString()))
        		.digest(object[8].toString())
        		.build());
        	
        });
		return results;
		
	}

	/**
	 * 
	 * @param contents
	 * @param formats
	 * @return
	 */
	private CatEntryFilterI buildFilter(String[] contents, String[] formats) {
		final boolean hasContents = !ArrayUtils.isEmpty(contents);
		final boolean hasFormats = !ArrayUtils.isEmpty(formats);
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

	
	/**
	 * 
	 * 
	 * @param user
	 * @param _resourceIds
	 * @return
	 */
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

	/**
	 * 
	 * 
	 * @param resource
	 * @return
	 */
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

	
	 /**
	  * 
	  * @author afour
	  *
	  */
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
	/** End file service private Method */
	
	
	
	
	/** Start refresh catalog service  private Methods */
	
	/**
	 * 
	 * @param resources
	 * @param append
	 * @param checksum
	 * @param delete
	 * @param populateStats
	 * @param options
	 * @throws ClientException
	 */
	 private void loadValues(List<String> resources, boolean append, boolean checksum, boolean delete, boolean populateStats, List<String> options) throws ClientException {
		 if(Objects.nonNull(resources)) {
			 _resources = resources;
		 }
		 if(Objects.nonNull(append)) {
			 _operations.add(CatalogService.Operation.Append);
		 }
		 if(Objects.nonNull(checksum)) {
			 _operations.add(CatalogService.Operation.Checksum);
		 }
		 if(Objects.nonNull(delete)) {
			  _operations.add(CatalogService.Operation.Delete);
		 }
		 if(Objects.nonNull(populateStats)) {
			 _operations.add(CatalogService.Operation.PopulateStats);
		 }
		 if(Objects.nonNull(options)) {
			 loadOptions(options);
		 }
		 if (_operations.contains(CatalogService.Operation.All)) {
	            _operations.clear();
	            _operations.addAll(CatalogService.Operation.ALL);
	        }
	}

	 /**
	  * 
	  * @param options
	  */
	private void loadOptions(List<String> options) {
		if (options.contains(APPEND)) {
			_operations.add(CatalogService.Operation.Append);
		}
		if (options.contains(CHECKSUM)) {
			_operations.add(CatalogService.Operation.Checksum);
		}
		if (options.contains(DELETE)) {
			_operations.add(CatalogService.Operation.Delete);
		}
		if (options.contains(POPULATE_STATS)) {
			_operations.add(CatalogService.Operation.PopulateStats);
		}
	}
	 	
	/** End refresh catalog service Method */

    /** Start Triage Service private Methods*/
	 

	/**
	 * 
	 * @param projectPath
	 * @param pXNAME
	 * @param request
	 * @throws ResourceAlreadyExistsException
	 * @throws InitializationException
	 */
	private void createTriageResource(String projectPath, String pXNAME, HttpServletRequest request) throws ResourceAlreadyExistsException, InitializationException {

		// Create any subdirectories requested as well
		String dirString = "";
		// pXNAME +
		// getRequest().getResourceRef().getRemainingPart().replaceFirst("\\?.*$", "");
		File dir = new File(projectPath, dirString);
		if (dir.exists()) {
			throw new ResourceAlreadyExistsException("Resource with this name already exists.", dirString);
		} else {
			if (!dir.mkdirs()) {
				throw new InitializationException("Could not create resource directory.");
			}
		}

	}
		
		public void openworkflow(boolean status,String action,String reason,String comment, UserI user, String projectId) throws Exception{
			PersistentWorkflowI work=WorkflowUtils.buildOpenWorkflow( user, "xnat:projectData", projectId, projectId,EventUtils.newEventInstance(EventUtils.CATEGORY.DATA,EventUtils.TYPE.WEB_FORM,  action,reason,comment));
			if(status){
				WorkflowUtils.complete(work, work.buildEvent());		
			}else{
				WorkflowUtils.fail(work, work.buildEvent());
			}
		}
		
		private boolean uploadTriageFile(String projectPath,String xname,String file, boolean inbody,String target,String overwrite,String format,String content,String event_reason, String extract, HttpServletRequest request, UserI user) throws InitializationException {

			// Create any subdirectories requested as well
			String fileName=null;
			File directory=null;
			File resourceFile=new File(projectPath + File.separator + xname);
			fileName=resourceFile.getName();
			directory=resourceFile.getParentFile();
			log.warn("resourceFile:"+resourceFile.getAbsolutePath());
			log.warn("directory:"+directory.getAbsolutePath());

			
			//Upload to non-existing resources (auto-create) or fail?  Should auto-create.
			if (!directory.exists()) {
				if (!directory.mkdirs()) {
					throw new InitializationException("Could not create resource directory.");
				}
			}
			
			saveTriageManifestFile(directory.getAbsolutePath(),fileName,target,overwrite,format,content,event_reason,request,user);
			
			
			if(inbody){
				return handleInbodyTriageFileUpload(projectPath,directory.getAbsolutePath(),fileName);
			} else {
				return handleAttachedTriageFileUpload(projectPath,directory.getAbsolutePath(),fileName, extract);
			}
			
		}
		
	  private boolean handleInbodyTriageFileUpload(String projectPath, String dirString, String fileName) throws InitializationException {
			try {
				
				// This is probably redundant due to current doPut/doPost coding, but including it anyway.
				if (fileName==null || fileName.length()<1) {
		        	throw new DataFormatException("Please use HTTP PUT request to specify a file name in the URL.");
				}
		        
		        // Write original file if not requesting or have non-archive file
				File ouf=new File(dirString,fileName);
				
				ouf.getParentFile().mkdirs();
				
				//(new FileWriterWrapper(this.getRequest().getEntity(),fileName)).write(ouf);  //Pending IMPL
		        
				log.warn("fileName "+fileName);
				log.warn("dirString "+dirString);
				

		        return true;
				
			} catch (Exception e) {
				log.error("",e);
				throw new InitializationException(e.getMessage());
			}
			
		}

	  private Map<String,String> bodyParams=Maps.newHashMap();
	  private boolean handleAttachedTriageFileUpload(String projectPath, String dirString, String requestedName, String extract) throws InitializationException {
		
		org.apache.commons.fileupload.DefaultFileItemFactory factory = new org.apache.commons.fileupload.DefaultFileItemFactory();
		org.restlet.ext.fileupload.RestletFileUpload upload = new  org.restlet.ext.fileupload.RestletFileUpload(factory);

	    List<FileItem> fileItems = null;
		try {
			
			//fileItems = upload.parseRequest(this.getRequest()); // Pending IMPL

			for (FileItem fi:fileItems) {    						         
		    	
				if (fi.isFormField()) {
	            	// Load form field to passed parameters map
					bodyParams.put(fi.getFieldName(),fi.getString());
	               	continue;
	            } 
				
		        String fileName;
				if (requestedName==null || requestedName.length()<1) {
					fileName=fi.getName();
				} else {
					fileName=requestedName;
				}
				
				//sfinal String extract=this.retrieveParam("extract");
		        /*if (extract!=null && extract.equalsIgnoreCase("true")) {
		        	// Write extracted files
		        	CompressionMethod method = getCompressionMethod(fileName);
		        	if (method != CompressionMethod.NONE) {
		        		if (!extractCompressedFile(fi.getInputStream(),dirString,fileName,method)) {
		        			this.getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,"Error extracting file.");
		        			return false;
		        		} else {
		        			// If successfully extracted, don't create unextracted file
		        			continue;
		        		}
		        	}
		        }*/
	        	fi.write(new File(dirString + "/" + fileName));
			
		    }
			return true;
	    
		} catch (Exception e) {
			log.error("",e);
			throw new InitializationException(e.getMessage());
		}
	}
		
		private boolean saveTriageManifestFile(String path,String name, String target,String overwrite,String format,String content,String event_reason, HttpServletRequest request, UserI user) {
			boolean success=false;
			
			File manifestFile;
			File resourceFile;
			ObjectMapper mapper = new ObjectMapper();
			try {
				
				if(StringUtils.isNotEmpty(name)){
					name = StringUtils.stripEnd(name,"/");
					resourceFile=new File(path+File.separator+name);
					manifestFile=new File(path+File.separator+MANIFEST);
				}else{
					resourceFile=new File(path);
					manifestFile=this.getManifestFileFromResource(resourceFile);
				}
				log.warn(manifestFile.getPath());
				log.warn(resourceFile.getPath());
				
				TriageManifest tManifest=(manifestFile.exists())?mapper.readValue(manifestFile, TriageManifest.class): new TriageManifest();
				
				Map<String, String> entry=new HashMap<String,String>();
				entry.put("Resource", constructResourceURI(resourceFile.getName(),request));
				entry.put("URI", constructResourceURI(resourceFile.getName(), request));
				entry.put("TARGET", constructTargetURI(resourceFile.getName(), target, request));
				entry.put("FTARGET", constructFormattedTargetURI(resourceFile.getName(),target,request, user));
				//entry.put("FSOURCE", constructResourceNames(resourceFile.getName()));

				entry.put("USER",user.getUsername());
				entry.put("DATE",(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format( Calendar.getInstance().getTime()));

				//entry.put("DATE",java.util.Calendar.getInstance(java.util.TimeZone.getDefault()).getTime().toString());
				entry.put(OVERWRITE,constructOverwrite(overwrite));
				entry.put(EVENT_REASON,constructEventReason(event_reason));
				
				entry.put(FORMAT,constructFormat(format));
				entry.put(CONTENT,constructContent(content));
				
				tManifest.addEntry(entry);
				mapper.writeValue(manifestFile, tManifest);
				
				
				success=true;
			} catch (JsonGenerationException e) {
				log.warn(e.getMessage());
				success=false;
			} catch (JsonMappingException e) {
				log.warn(e.getMessage());
				success=false;

			} catch (IOException e) {
				log.warn(e.getMessage());
				success=false;
			}

			return success;
		}
		
		  private String constructOverwrite(String overwrite) {
			    if (StringUtils.isEmpty(overwrite)) {
			    	overwrite="false";
			    }
			    return overwrite;
		     }
		    private String constructContent(String content) {
			    if (StringUtils.isEmpty(content)) {
			    	content="";
			    }
			   
			    return content;
		     }
		    private String constructFormat(String format) {
			    if (StringUtils.isEmpty(format)) {
			    	format="";
			    }
			    return format;
		     }
		    
		    private String constructEventReason( String event_reason) {
		 	    if (StringUtils.isEmpty(event_reason)) {
		 	    	event_reason="";
					
		 	    }
		 	    return event_reason;
		      }
		
		 private String constructTargetURI(String resource, String target, HttpServletRequest request) {
			    if (StringUtils.isEmpty(target)) {
			    	//construct target from url
			    	String requestPart = request.getServletPath() + request.getPathInfo();
			    	requestPart= this.appendFiles(requestPart, resource);
			    	//replace triage with archive
			    	target=requestPart.replace(TRIAGE, ARCHIVE);
			    }
			    if(!target.startsWith(DATA)){
			    	target=target.substring(target.indexOf(DATA));
			    }
			    if(!target.endsWith(FILES)){
			    	target=target+File.separator+FILES;
			    }
			    return target;
		     }
		 
		 public  String constructFormattedTargetURI(String resource, String target, HttpServletRequest request, UserI user) {
	    	 String formatted=constructTargetURI(resource, target,request);
	    	 //need to format IDs to Labels for user.
	    	
	    	 String[] params = StringUtils.split(formatted, "/");
	    	 for(int i=0;i<params.length-1;i++){
	    		 if("experiments".equals(params[i])){
	    			 String expid=params[i+1];
	    			 XnatExperimentdata exp=XnatExperimentdata.getXnatExperimentdatasById(expid, user, false);
	    			 if(exp!=null){
	    		    	 formatted=formatted.replace(expid,exp.getLabel());
	    			 }
	    		 }
	    		 if("subjects".equals(params[i])){
	    			 String subjid=params[i+1];
	    			 XnatSubjectdata subj=XnatSubjectdata.getXnatSubjectdatasById(subjid, user, false);
	    			 if(subj!=null){
	    		    	 formatted=formatted.replace(subjid,subj.getLabel());
	    			 }
	    		 }
	    		 if("assessors".equals(params[i])){
	    			 String assid=params[i+1];
	    			 XnatExperimentdata exp=XnatExperimentdata.getXnatExperimentdatasById(assid, user, false);
	    			 if(exp!=null){
	    		    	 formatted=formatted.replace(assid,exp.getLabel());
	    			 }
	    		 }
	    	 }
		   
	    	 formatted=formatted.replace("/data/archive/projects/","<b>Project</b>: ");
	    	 formatted=formatted.replace("/subjects/","<br><b>Subject</b>: ");
	    	 formatted=formatted.replace("/data/archive/experiments/","<b>Session</b>: ");
	    	 formatted=formatted.replace("/assessors/","<br><b>Assessor</b>: ");
	    	 formatted=formatted.replace("/scans/","<br><b>Scan</b>: ");
	    	 formatted=formatted.replace("/resources/","<br><b>Resource</b>: ");
	    	 formatted=formatted.replace("/resources","<br><b>Resource</b>: ");
	    	 formatted=formatted.replace("/files/","");
	    	 formatted=formatted.replace("/files","");
	    	
	    	 return formatted;
	     }
		
		
		private boolean canEditDestination(String target, UserI user) throws Exception {
			String targetResource = target.replaceAll("(/files)?$", "/files");
			ResourceURII arcURI = convertValue(targetResource);
			return arcURI.getSecurityItem().canEdit(user);
		}
		public ResourceURII convertValue(final String key) throws ClientException{
			try {
				URIManager.DataURIA uri=UriParserUtils.parseURI(key);
				
				if(uri instanceof ResourceURII){
					return (ResourceURII)uri;
				}else{
					throw new ClientException("Invalid Destination:"+ key);
				}
			} catch (MalformedURLException e) {
				throw new ClientException("Invalid Destination:"+ key,e);
			}
		}
		
		String getxName(String project, HttpServletRequest request){
			String requestPart = this.mapToProjectResources(request.getServletPath() + request.getPathInfo());

			int bindex =StringUtils.indexOf(requestPart, "/projects/"+project);
			//only compress urls for subject and deeper
			int lindex =StringUtils.lastIndexOf(requestPart, "/resources");
			String xName=requestPart.substring(bindex);
			//mapped=originalResourceUrl.replace(extra, "");
			
			return xName;
		}
		
//		private boolean uploadTriageFile(String projectPath,String pXNAME) {
//			return uploadTriageFile(projectPath,pXNAME,null);
//		}
		
	private void returnFile(XnatProjectdata proj, String projectPath,String xname,String file, UserI user) throws InvalidItemException, Exception {
			
			String escapedPath=projectPath+File.separator+ File.separator+"resources"+File.separator+xname+File.separator+"files"+File.separator+file;
			String resourcePath=projectPath+File.separator+ File.separator+"resources";
			String path=URLDecoder.decode(escapedPath, "UTF-8");
			File reqFile = new File (path);
			if (reqFile.exists() && reqFile.isFile() && canRead(proj, new File(escapedPath), user)) {
				//sendFileRepresentation(reqFile);
			} else {
				throw new NotFoundException("Quarantine file not found.");
			}
		}

		@Override
		public void deleteTriage(UserI user, String projectId, String xname, String file, String eventReason, String eventComment, String eventId) {
			try {   
			XnatProjectdata proj = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
				String projectPath=TriageUtils.getTriageProjectPath(projectId);
		        if(proj!=null && proj.canRead(user)){//only continue when the user can read the project.
		        	
			        if (xname == null && file == null) {
			        	//fail(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid Operation.");
			        } else if (xname != null && file == null) {
			        	deleteTriageResource(proj,projectPath,xname,user,eventReason, eventComment, eventId, projectId);
			        } else if (xname != null && file != null) {
			        	deleteTriageFiles(proj,projectPath,xname,file, user, eventReason, eventComment,eventId,projectId);
			        }
		        }
			} catch (Exception e) {
				//fail(Status.SERVER_ERROR_INTERNAL,e.getMessage());
				log.error("",e);
			}
		}


		private void deleteTriageFiles(XnatProjectdata proj, String projectPath, String xname, String file, UserI user, String eventReason, String eventComment, String eventId, String projectId) throws Exception {
			File fi = new File (projectPath+File.separator+ File.separator+"resources"+File.separator+xname+File.separator+"files"+File.separator+file);
			if(canDelete(proj, fi,user)){
				ArrayList<File> fileList=new ArrayList<File>();
		
		        if (fi.exists()) {
		           	fileList.add(fi);
		        }
				
				boolean deleteOK = true;
				if (fileList.size()>0) {
		            for (File f : fileList) {  
		            	if (f.isDirectory()) {
		            		try {
		            			FileUtils.deleteDirectory(f);
		            		} catch (IOException e) {
		            			deleteOK = false;
		            		}
		            	} else {
		            		if (!f.delete()) {
		            			deleteOK = false;
		            		}
		            	}
		            }
		            if (deleteOK) {
			        	workflow(true,"Delete Quarantine Files", eventReason, eventComment,user, eventId, projectId);
		            } else {
		            	throw new InitializationException("Problem deleting one or more server files.");
		            }
				} else {
					throw new NotFoundException("No matching files found.");
				}
			}else{
				throw new InsufficientPrivilegesException("User account doesn't have permission to delete this file.");
			}
		}
		
		

		private void deleteTriageResource(XnatProjectdata proj, String projectPath, String xname, UserI user, String eventReason, String eventComment, String eventId, String projectId) throws Exception {
			File dir = new File (projectPath+File.separator+RESOURCES+File.separator+xname);
			if(this.canDelete(proj, dir, user)){
				if (dir.exists() && dir.isDirectory()) {
					
					try {
						FileUtils.deleteDirectory(dir);
			        	workflow(true,"Delete Quarantine Files",eventReason, eventComment, user, eventId, projectId);

					} catch (IOException e) {
						log.error("",e);
						throw new InitializationException(e.getMessage());
					}
				} else {
					throw new NotFoundException("Quarantine directory not found or is not a directory.");
				}
			}else{
				throw new InsufficientPrivilegesException("User account doesn't have permission to modify this resource.");
			}
		}
		
		public void workflow(boolean status,String action,String reason,String comment, UserI user, String eventId, String projectId) throws Exception{
			PersistentWorkflowI work=WorkflowUtils.getOrCreateWorkflowData(XnatEventUtil.getEventId(eventId), user, "xnat:projectData", projectId, projectId ,EventUtils.newEventInstance(EventUtils.CATEGORY.DATA,EventUtils.TYPE.WEB_FORM, action,reason,comment));
			if(status){
				WorkflowUtils.complete(work, work.buildEvent());		
			}else{
				WorkflowUtils.fail(work, work.buildEvent());
			}
		}
		
		boolean canDelete(XnatProjectdata proj,File file, UserI user) throws InvalidItemException, Exception{
	        boolean allowed=false;
	        if (Features.checkFeature(user,proj.getSecurityTags().getHash().values(), "QuarantineReview") || StringUtils.equals(user.getUsername(),getUser(file))){
	            allowed= true;
	        }else{
	        	allowed= false;
	        }
			return allowed;
		}
		
		String getUser(File file){
			String username="";
			File manifest=new File(file+File.separator+FILES+File.separator+MANIFEST);
			if(file.isFile()){
				username=this.getPropertyFromManifest(file, USER);
			}else{
				username=this.getPropertyFromManifest(manifest, USER);
			}
			return username;
		}

		private TriageDto getTriageUtil(String fn, File f, HttpServletRequest request) {
			return TriageDto.builder()
					 .resource(fn)
					 .uri(constructResourceURI(fn, request))
					 .target(getPropertyFromManifest(f, TARGET))
					 .user(getPropertyFromManifest(f, USER))
					 .date(getPropertyFromManifest(f, DATE))
					 .overwrite(getPropertyFromManifest(f, OVERWRITE))
					 .eventReason(getPropertyFromManifest(f, EVENT_REASON))
					 .ftarget(getPropertyFromManifest(f, FTARGET))
					 .format(getPropertyFromManifest(f, FORMAT))
					 .content(getPropertyFromManifest(f, CONTENT))
					 .fSource("").build();
		}

		private String constructResourceURI(String resource, HttpServletRequest request) {
			String requestPart = this.mapToProjectResources(request.getServletPath() + request.getPathInfo());

			if (!requestPart.endsWith(resource)) {
				requestPart += File.separator + resource;
			}
			return requestPart;

		}

		String mapToProjectResources(String originalResourceUrl) {
			String mapped = originalResourceUrl;
			int bindex = StringUtils.indexOf(originalResourceUrl, "/subjects/");
			// only compress urls for subject and deeper
			if (bindex > 0) {
				int lindex = StringUtils.lastIndexOf(originalResourceUrl, "/resources");
				String extra = originalResourceUrl.substring(bindex, lindex);
				mapped = originalResourceUrl.replace(extra, "");
			}
			return originalResourceUrl;
		}

		private String getPropertyFromManifest(File resourceFile, String prop) {
			ObjectMapper mapper = new ObjectMapper();
			String target = "";
			try {
				File manifestFile = getManifestFileFromResource(resourceFile);
				TriageManifest tManifest = mapper.readValue(manifestFile, TriageManifest.class);
				target = tManifest.getFirstMatchingEntry(prop);
			} catch (JsonParseException e) {
				log.warn(e.getMessage());
			} catch (JsonMappingException e) {
				log.warn(e.getMessage());
			} catch (IOException e) {
				log.warn(e.getMessage());
			}
			return target;
		}

		private File getManifestFileFromResource(File resourceFile) {
			File manifestFile = new File(resourceFile.getParentFile().getAbsolutePath() + File.separator + MANIFEST);
			if (!manifestFile.exists()) {
				manifestFile = new File(resourceFile.getAbsolutePath() + File.separator + FILES + File.separator + MANIFEST);
			}
			return manifestFile;
		}
		
		private void returnFileList(XnatProjectdata xproj, String projectPath, String xName, HttpServletRequest request) {
			List<TriageFileDto> response = new  ArrayList<>();
			File dir = new File (projectPath+File.separator+ File.separator+"resources"+File.separator+xName+File.separator+"files");
			//need to ignore .json files.
			if (dir.exists() && dir.isDirectory()) {
				ArrayList<File> fileList = new ArrayList<File>();
				fileList.addAll(FileUtils.listFiles(dir,null,true));
				Iterator<File> i = fileList.iterator();
		        while (i.hasNext()) {
		        	File f = i.next();
		        	if(!MANIFEST.equals(f.getName())){
		        		String fileRelativeName= relative(dir, f);
		        		response.add(getTriageFileData(fileRelativeName, f, request));
		        	}
		        }
			}
		}
		
		private TriageFileDto getTriageFileData(String fileRelativeName, File f, HttpServletRequest request) {
			return TriageFileDto.builder()
					 .name(fileRelativeName)
					 .uri(constructURI(fileRelativeName, request))
					 .target(getPropertyFromManifest(f, TARGET))
					 .user(getPropertyFromManifest(f, USER))
					 .date(getPropertyFromManifest(f, DATE))
					 .overwrite(getPropertyFromManifest(f, OVERWRITE))
					 .eventReason(getPropertyFromManifest(f, EVENT_REASON))
					 .ftarget(getPropertyFromManifest(f, FTARGET))
					 .format(getPropertyFromManifest(f, FORMAT))
					 .content(getPropertyFromManifest(f, CONTENT))
					 .size(f.length()).build();
		}
		
	    private String constructURI(String resource, HttpServletRequest request) {
	    	String requestPart = this.mapToProjectResources(request.getServletPath() + request.getPathInfo());
	 
	    	return appendFiles(requestPart, resource);
	    	
	    }
	    private String appendFiles(String requestPart,String resource){
	    	if (!requestPart.endsWith(resource) && !requestPart.endsWith("/files") && !requestPart.endsWith("/files/")) {
	    		requestPart+="/files";
	    	}
	    	if (!requestPart.endsWith(resource)){
	    		requestPart+=File.separator+resource;
	    	}
	    	return requestPart;
	    }

		public String relative( final File base, final File file ) {
		    final int rootLength = base.getAbsolutePath().length();
		    final String absFileName = file.getAbsolutePath();
		    final String relFileName = absFileName.substring(rootLength + 1);
		    return relFileName;
		}

		private void returnZippedFiles(XnatProjectdata xproj, String projectPath, String xName,UserI user, HttpServletRequest request, String compression) throws InvalidItemException, NotFoundException, InsufficientPrivilegesException, ActionException, Exception {
			String dirPath = File.separator+"resources"+File.separator+xName+File.separator+"files";
			String resourcePath=projectPath+File.separator+RESOURCES+File.separator+xName;
			if(canRead(xproj, new File(resourcePath),user)){
				File dir = new File (projectPath,dirPath);
				if (dir.exists() && dir.isDirectory()) {
					ArrayList<File> fileList = new ArrayList<File>();
					fileList.addAll(FileUtils.listFiles(dir,null,true));
					sendZippedFiles(projectPath,xName,xName,fileList, request, compression);
				} else {
					throw new NotFoundException("Quarantine directory not found or is not a directory.");
				}
			}else{
				throw new InsufficientPrivilegesException("Not authorized");
			}
		}
		
		private void sendZippedFiles(String projectPath,String pXNAME,String fileName,ArrayList<File> fileList, HttpServletRequest request, String compression) throws ActionException {
			
			ZipRepresentationUtil zRep;
			if(MediaTypeUtil.getRequestedMediaType(request.getContentType())!=null && MediaTypeUtil.getRequestedMediaType(request.getContentType()).equals(MediaTypeUtil.APPLICATION_GNU_TAR)){
				zRep = new ZipRepresentationUtil(MediaType.parseMediaType(MediaTypeUtil.APPLICATION_GNU_TAR),projectPath,ZipOutputStream.DEFLATED);
				setContentDisposition(String.format("%s.tar.gz", fileName));
			}else if(MediaTypeUtil.getRequestedMediaType(request.getContentType())!=null && MediaTypeUtil.getRequestedMediaType(request.getContentType()).equals(MediaTypeUtil.APPLICATION_TAR)){
				zRep = new ZipRepresentationUtil(MediaType.parseMediaType(MediaTypeUtil.APPLICATION_TAR),projectPath,ZipOutputStream.STORED);
				setContentDisposition(String.format("%s.tar.gz", fileName));
			}else{
				zRep = new ZipRepresentationUtil(MediaType.parseMediaType(MediaTypeUtil.APPLICATION_ZIP), projectPath, identifyCompression(null, compression));
				setContentDisposition(String.format("%s.zip", fileName));
			}
			zRep.addAllAtRelativeDirectory(projectPath,fileList);
			//this.getResponse().setEntity(zRep);
		}

		private String setContentDisposition(String fileName) {
			return String.format(ATTACHMENT_DISPOSITION, fileName);
		}
		
		boolean canRead(XnatProjectdata proj,File f, UserI user) throws InvalidItemException, Exception{
			boolean allowed=false;
			if (Features.checkFeature(user,proj.getSecurityTags().getHash().values(), "QuarantineReview") || StringUtils.equals(user.getUsername(),getUser(f))){
	            allowed= true;
	        }else{
	        	allowed= false;
	        }
			return allowed;
		}
		
	 /** End Triage Service Methods*/
		
   private final String RESOURCES ="resources";
   private static final String FORMAT = "format";
   private static final String CONTENT = "content";
   private final String FILES ="files";
   private final String MANIFEST=".manifest";	
   private final String TARGET = "TARGET";
   private final String FTARGET = "FTARGET";
   private final String USER = "USER";
   private final String DATE = "DATE";
   private final String EVENT_REASON = "EVENT_REASON";
   private final String OVERWRITE = "OVERWRITE";
   private final String TRIAGE="/services/triage/";
   private final String ARCHIVE="/archive/";
   private final String RESOURCE ="Resource";
   private final String DATA ="/data";
   private final String FSOURCE ="FSOURCE";
   private static final String _ON_FAILURE_RETURN_JS = "_onFailureReturnJS";
   private static final String _ON_FAILURE_RETURN_HTML = "_onFailureReturnHTML";
   private static final String COMPRESSION = "compression";
   //private static final String ATTACHMENT_DISPOSITION = "attachment; filename=\"%s\"";
	 	
    private CatalogService _catalogService;
	private List<String> _resources   = Lists.newArrayList();
	private final List<CatalogService.Operation> _operations  = Lists.newArrayList();
	private static final String APPEND = "append";
	private static final String CHECKSUM = "checksum";
	private static final String DELETE = "delete";
	private static final String POPULATE_STATS = "populateStats";
	
    private UserDataCache _userDataCache;
	private static final String ATTACHMENT_DISPOSITION = "attachment; filename=\"%s\"";
	private static String FILE_NAME = "";
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
	
	//
	private static final String PROJECT_FILE_QUERY=  "SELECT xnat_abstractresource_id FROM xnat_projectdata_resource pr \n" + 
			"LEFT JOIN xnat_abstractresource abst ON pr.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id \n" + 
			"LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id";

//private static final String SUBJECT_FILE__QUERY= "SELECT xnat_abstractresource_id FROM xnat_subjectdata_resource map \n" + 
//			"LEFT JOIN xnat_subjectdata sub ON map.xnat_subjectdata_id=sub.id \n" + 
//			"LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id \n" + 
//			"LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id ";

	private static final String SUBJECT_RESOURCE_QUERY= "SELECT xnat_abstractresource_id\n" + 
					"FROM xnat_subjectdata_resource map \n" + 
					"LEFT JOIN xnat_subjectdata sub ON map.xnat_subjectdata_id=sub.id \n" + 
					"LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id \n" + 
					"LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id";

	private static final String EXPERIMENT_ASSESSER_FILE_QUERY = "SELECT xnat_abstractresource_id\n" + 
						"FROM img_assessor_out_resource map \n" + 
						"LEFT JOIN xnat_experimentdata expt ON map.xnat_imageassessordata_id=expt.id  \n" + 
						"LEFT JOIN xdat_meta_element xmeexpt ON expt.extension=xmeexpt.xdat_meta_element_id \n" + 
						"LEFT JOIN xdat_element_security xes ON xmeexpt.element_name=xes.element_name \n" + 
						"LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id \n" + 
						"LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id \n" + 
						"LEFT JOIN xnat_imageassessordata xiad ON expt.id=xiad.id\n" + 
						"LEFT JOIN xnat_imageAssessorData iad ON map.xnat_imageassessordata_id=iad.id";

	private static final String PRO_SUB_EXP_ASS_FILE_QUERY=" SELECT xnat_abstractresource_id\n" + 
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



	private static final String BY_UNION = "  UNION ";

	private static final String BY_ID_WHERE_EXP_AND_ASSESSER = " WHERE iad.imagesession_id= :experimentId  AND  map.xnat_imageassessordata_id = :assessorId";

	private static final String BY_ID_WHERE_EXP_AND_ASSESSER_AND_RESOURCE = " WHERE iad.imagesession_id= :experimentId  AND  map.xnat_imageassessordata_id = :assessorId AND abst.xnat_abstractresource_id = :resourceId";

	private static final String BY_ID_WHERE_SUBJ_AND_RESOURCE = " WHERE xnat_subjectdata_id= :subjectId  AND map.xnat_abstractresource_xnat_abstractresource_id= :resourceId";

	private static final String BY_ID_WHERE_PROJ = " where sub.project = :projectId ";
	
	private static final String BY_ID_WHERE_PROJ_AND_RESOURCE = " WHERE xnat_projectdata_id= :projectId  AND pr.xnat_abstractresource_xnat_abstractresource_id = :resourceId ";

	private static final String BY_ID_WHERE_FILE_SUBJECT = " xnat_subjectdata_id = :subjectId ";

	private static final String BY_ID_WHERE_EXP_ID = " res_map.xnat_experimentdata_id = :experimentId  ";

	private static final String BY_ID_WHERE_EXP_ID_IMG_SESSION_ID = " isd.image_session_id = :experimentId  ";

	private static final String BY_ID_WHERE_EXP_ID_IMAGESESSION_ID = " iad.imagesession_id = :experimentId  ";

	private static final String BY_ID_WHERE_RESOURCE_ID  = "  abst.xnat_abstractresource_id = :resourceId ";


	private static final Pattern  PATTERN_ASSESSOR_URI = Pattern.compile("/assessors/([^/]+)", Pattern.CASE_INSENSITIVE);
	private static final Pattern  PATTERN_ARCHIVE_URI  = Pattern.compile("/archive/([^/]+)");

	private String filePath = "";
	private String reference = "";
	private boolean delete = false;
	private boolean async = false ;
	private String[] notifyList = {};
	
	private final NamedParameterJdbcTemplate _template;

}