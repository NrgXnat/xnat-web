package org.nrg.xnat.services.resources.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ActionException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.om.WrkWorkflowdata;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatAbstractresourceTag;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatReconstructedimagedata;
import org.nrg.xdat.om.XnatResource;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFTItem;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.EventUtils.CATEGORY;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.uri.UriParserUtils;
import org.nrg.xnat.model.util.XnatTemplateUtil;
import org.nrg.xnat.services.archive.CatalogService;
import org.nrg.xnat.services.projects.ProjectService;
import org.nrg.xnat.services.resources.ResourceService;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.nrg.xnat.utils.WorkflowUtils;
import org.restlet.data.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResourceServiceImpl implements ResourceService{
	
	@Autowired
	public ResourceServiceImpl(final NamedParameterJdbcTemplate template, ProjectService projectService) {
		_template = template;
		_catalogService = XDAT.getContextService().getBean(CatalogService.class);
		_projectService = projectService;
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
		List<XnatAbstractresource> xnatAbstractresources;
		ArrayList<XnatExperimentdata> assesseds = XnatTemplateUtil.getXnatExperimentdata(assessedId, user,null);
		ArrayList<XnatImagescandata> scans = XnatTemplateUtil.getXnatImageScanData(scanId, user, assesseds);
		String query = XnatTemplateUtil.getQuery(scans,assesseds, null);
		 xnatAbstractresources = _template.query(query,  new ResourceRowMapper(user));
		 return xnatAbstractresources;
	}
	
	@Override
	public List<XnatAbstractresource> findByProject(UserI user, String projectId) {
		return _template.query(PROJECT_QUERY + BY_ID_WHERE_PROJECT, new MapSqlParameterSource("projectId", projectId), new ResourceRowMapper(user));
	}
	
	@Override
	public XnatAbstractresource findByProjectAndLabel(UserI user, String projectId, String label) {
		return _template.queryForObject(PROJECT_QUERY + BY_ID_WHERE_PROJECT + AND_WHERE + BY_ID_WHERE_label , new MapSqlParameterSource("projectId", projectId).addValue("label", label), new ResourceRowMapper(user));
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
	public XnatResourcecatalog create(UserI user, String projectId, XnatResource xnatResource) {
		
		  XFTItem item;
	        try {
	            item=xnatResource.getItem();
	            if(item==null)
	            	throw new DataFormatException("Need POST Contents");
	            
	            if(item.instanceOf("xnat:resourceCatalog")){
	                XnatResourcecatalog catResource = (XnatResourcecatalog)BaseElement.GetGeneratedItem(item);

	                if(catResource.getXnatAbstractresourceId()!=null){
	                    XnatAbstractresource existing=XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(catResource.getXnatAbstractresourceId(), user, false);
	                    if(existing!=null)
	                    	throw new ResourceAlreadyExistsException("Specified catalog already exists.", projectId);
	                  else
	                	  throw new DataFormatException("Contains erroneous generated fields (xnat_abstractresource_id)");
	                }

	                //setCatalogAttributes(user, catResource);

	                PersistentWorkflowI wrk=PersistentWorkflowUtils.getWorkflowByEventId(user,getEventId());
	                if(wrk==null && "SNAPSHOTS".equals(catResource.getLabel())){
	                    if(getSecurityItem() instanceof XnatExperimentdata){
	                        Collection<? extends PersistentWorkflowI> workflows = PersistentWorkflowUtils.getOpenWorkflows(user,((ArchivableItem)getSecurityItem()).getId());
	                        if(workflows!=null && workflows.size()==1){
	                            wrk=(WrkWorkflowdata)CollectionUtils.get(workflows, 0);
	                            if(!"xnat_tools/AutoRun.xml".equals(wrk.getPipelineName())){
	                                wrk=null;
	                            }
	                        }
	                    }
	                }
	                insertCatalogWrap(catResource,wrk,user);
	            }else
	            	throw new DataFormatException("Only ResourceCatalog documents can be PUT to this address.");
	            
	        } catch (ActionException e) {
	        	log.error(e.getMessage());
			} catch (Exception e) {
				log.error(e.getMessage());
	        }
			return (XnatResourcecatalog) xnatResource;
	}
	
	 private Integer getEventId() {
		  final String id = getQueryVariable(EventUtils.EVENT_ID);
	        if (id != null) {
	            return Integer.valueOf(id);
	        } else {
	            return null;
	        }
	}

	public void insertCatalogWrap(XnatResourcecatalog catResource, PersistentWorkflowI wrk, UserI user) throws Exception {
	        final boolean isNew;
	        final Integer wrkId;
	        if (wrk == null) {
	            isNew = true;
	            wrk = PersistentWorkflowUtils.buildOpenWorkflow(user, getSecurityItem().getItem(), newEventInstance(EventUtils.CATEGORY.DATA, (getAction() != null) ? getAction() : EventUtils.CREATE_RESOURCE));
	            if (wrk == null) {
	                throw new Exception("Unable to build open workflow for inserting catalog " + catResource.getUri());
	            }

	            wrk.setStatus(PersistentWorkflowUtils.IN_PROGRESS);
	            PersistentWorkflowUtils.save(wrk, wrk.buildEvent());
	            wrkId=wrk.getWorkflowId();
	        } else {
	            wrkId=null;
	            isNew = false;
	        }

	        insertCatalog(catResource, wrkId, user);

	        if (isNew) {
	            WorkflowUtils.complete(wrk, wrk.buildEvent());
	        }
	    }
	 
	public EventDetails newEventInstance(EventUtils.CATEGORY cat, String action) {
        return EventUtils.newEventInstance(cat, getEventType(), (getAction() != null) ? getAction() : action, getReason(), getComment());
    }
	private String getComment() {
		return null;
	}

	private String getReason() {
		return null;
	}

	public EventUtils.TYPE getEventType() {
    	return EventUtils.TYPE.WEB_FORM;
    }
	private String getAction() {
		return null;
	}

	public boolean insertCatalog(XnatResourcecatalog catResource, Integer eventId, UserI user) throws Exception {
	        final XnatExperimentdata assessed = assesseds.size() == 1 ? assesseds.get(0) : null;
	        if (recons.size() > 0) {
	            if (assessed == null) {
	                //getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND, "Invalid session id.");
	                return false;
	            }

	            final XnatReconstructedimagedata reconstruction = recons.get(0);
	            return _catalogService.insertResourceCatalog(user, UriParserUtils.getArchiveUri(assessed, reconstruction), catResource,eventId) != null;
	        } else if (scans.size() > 0) {
	            if (assessed == null) {
	                //getResponse().setStatus(Status.CLIENT_ERROR_GONE, "Invalid session id.");
	                return false;
	            }
	            final XnatImagescandata scan = scans.get(0);
	            return _catalogService.insertResourceCatalog(user, UriParserUtils.getArchiveUri(assessed, scan), catResource, eventId) != null;
	        } else if (expts.size() > 0) {
	            final XnatExperimentdata experiment = expts.get(0);
	            return _catalogService.insertResourceCatalog(user, UriParserUtils.getArchiveUri(experiment), catResource, eventId) != null;
	        } else if (sub != null) {
	            return _catalogService.insertResourceCatalog(user, UriParserUtils.getArchiveUri(sub), catResource, eventId) != null;
	        } else if (proj != null) {
	            return _catalogService.insertResourceCatalog(user, UriParserUtils.getArchiveUri(proj), catResource, eventId) != null;
	        }
	        return true;
	    }
	
	public ItemI getSecurityItem() {
        if (this.security != null) {
            return security;
        }

        XnatExperimentdata assessed = null;
        if (this.assesseds.size() == 1) {
            assessed = assesseds.get(0);
        }

        if (recons.size() > 0) {
            return assessed;
        } else if (scans.size() > 0) {
            return assessed;
        } else if (expts.size() > 0) {
//			experiment
            return expts.get(0);
        } else if (sub != null) {
            return sub;
        } else if (proj != null) {
            return proj;
        } else {
            return null;
        }
    }
	
	protected void setCatalogAttributes(final UserI user, final XnatResourcecatalog catalog) throws Exception {
        if (StringUtils.isNotBlank(getQueryVariable("description"))) {
            catalog.setDescription(getQueryVariable("description"));
        }
        if (StringUtils.isNotBlank(getQueryVariable("format"))) {
            catalog.setFormat(getQueryVariable("format"));
        }
        if (StringUtils.isNotBlank(getQueryVariable("content"))) {
            catalog.setContent(getQueryVariable("content"));
        }

        final String[] tags = getQueryVariables("tags");
        if (tags != null) {
            for (final String variable : tags) {
                if (StringUtils.isNotBlank(variable)) {
                    for (final String instance : variable.split("\\s*,\\s*")) {
                        final XnatAbstractresourceTag tag = new XnatAbstractresourceTag(user);
                        if (instance.contains("=")) {
                            final String[] atoms = instance.split("=");
                            tag.setName(atoms[0]);
                            tag.setTag(atoms[1]);
                        } else if (instance.contains(":")) {
                            final String[] atoms = instance.split(":");
                            tag.setName(atoms[0]);
                            tag.setTag(atoms[1]);
                        } else {
                            tag.setTag(instance);
                        }
                        catalog.setTags_tag(tag);
                    }
                }
            }
        }
    }

	private String[] getQueryVariables(String string) {
		return null;
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
	        XnatAbstractresource xnatAbstractresource= XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(xnatAbstractResourceId, _user, false);
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
	 private final CatalogService _catalogService;
	 private final ProjectService _projectService;
	 XnatProjectdata proj = null;
	    XnatSubjectdata sub  = null;

	    ArrayList<XnatExperimentdata> expts = new ArrayList<>();

	    ArrayList<XnatImagescandata> scans = new ArrayList<>();

	    ArrayList<XnatReconstructedimagedata> recons = new ArrayList<>();

	    ArrayList<XnatExperimentdata> assesseds = new ArrayList<>();
	    String type = null;

	    ItemI parent = null;

	    ItemI security = null;

	    String xmlPath = null;

}