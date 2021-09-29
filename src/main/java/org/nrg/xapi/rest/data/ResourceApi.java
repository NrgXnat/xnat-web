package org.nrg.xapi.rest.data;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NoContentException;
import org.nrg.xapi.exceptions.NotAuthenticatedException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xapi.model.TriageDto;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatResource;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xapi.model.ResourceFile;
import org.nrg.xapi.model.DIRResource;
import org.nrg.xnat.dto.resource.MediaTypeUtil;
import org.nrg.xnat.helpers.resource.XnatResourceInfo;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.resources.ResourceService;
import org.nrg.xnat.services.resources.impl.ResourceServiceImpl.InvalidFileCharacters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ResourceApi extends AbstractXapiProjectRestController {

	@Autowired
	public ResourceApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ResourceService resourceService) {
		super(userManagementService, roleHolder);
		_resourceService = resourceService;
	}
	
	/**
	 * Get list of resources with the specified Experiment ID
	 * 
	 * @param experimentId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	@ApiOperation(value = "Gets the requested resources", notes = "Returns the  resources with the specified Experiment ID", response = XnatAbstractresource.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resources."),
		 			@ApiResponse(code = 400, message = "The requested experimentId wasn't found."),
		 			@ApiResponse(code = 404, message = "The requested resources wasn't found."),
		 			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatAbstractresource> getByExperimentId( @ApiParam(value = "The ID of the experiment.") @PathVariable  final String experimentId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested resources with experiment ID {} ", getSessionUser().getUsername(), experimentId);
		return _resourceService.findByExperimentId(getSessionUser(), experimentId);
	}
	
	/**
	 * Get resource with the specified resource ID and experiment ID
	 * 
	 * @param experimentId
	 * @param resourceId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	@ApiOperation(value = "Gets the requested resource", notes = "Returns the  resource with the specified ID and experimentId", response = XnatAbstractresource.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either experimentId or resourceId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/resources/{resourceId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET)
	public XnatAbstractresource getByIdAndExperimentId(@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
													   @ApiParam(value = "The ID of the resource.") @PathVariable  final Integer resourceId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested resources with experiment ID {} and with ID {}", getSessionUser().getUsername(), experimentId, resourceId);
		return _resourceService.findByIdAndExperimentId(getSessionUser(), resourceId, experimentId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, experimentId));
	}
	
	/**
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param experimentId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified project ID, subject ID and experimentId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either projectId or subjectId or experimentId  wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatAbstractresource> getByProjectIdAndSubjectIdExperimentId(
			@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId,
			@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested resources with project ID {} , with subject ID {} and with experiment ID {} ", getSessionUser().getUsername(),projectId, subjectId , experimentId);
		return _resourceService.findByProjectIdAndSubjectIdAndExperimentId(getSessionUser(), projectId, subjectId, experimentId);
	}
	
	/**
	 * 
	 * @param assessedId
	 * @param scanId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified ID and experimentId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
				    @ApiResponse(code = 400, message = "The requested either assessedId or scanId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{assessedId}/scans/{scanId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatAbstractresource> getByxperimentIdAndScanId(@ApiParam(value = "The ID of the assessed.") @PathVariable final String assessedId,
															    @ApiParam(value = "The ID of the scan.") @PathVariable final String scanId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested resources with assessed ID {} and with scan ID {} s", getSessionUser().getUsername(),assessedId, scanId);
		return _resourceService.findByExperimentIdAndScanId(getSessionUser(), assessedId, scanId);
	}

	
	/**
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  projectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatAbstractresource> getByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId)throws Exception {
		log.debug("User {} requested resources with project ID {} ", getSessionUser().getUsername(),projectId);
		return _resourceService.findByProjectId(getSessionUser(), projectId);
	}
	
	/**
	 * 
	 * @param resourceId
	 * @param projectId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  Id and projectId", response = XnatAbstractresource.class, responseContainer = "Single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either projectId or resourceId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/resources/{resourceId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET)
	public XnatAbstractresource getByIdAndProjectId(@ApiParam(value = "The ID of the resource.") @PathVariable final Integer resourceId,
													@ApiParam(value = "The ID of the project.") @PathVariable final String projectId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested resources with ID {} and with project ID {}", getSessionUser().getUsername(),resourceId, projectId);
		return _resourceService.findByIdAndProjectId(getSessionUser(), resourceId, projectId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, resourceId));
	}
	
	/**
	 * 
	 * @param subjectId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  subjectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested subjectId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/subjects/{subjectId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatAbstractresource> getBySubject(@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested resources with subject ID {}", getSessionUser().getUsername(),subjectId);
		return _resourceService.findBySubjectId(getSessionUser(), subjectId);
	}
	
	
	/**
	 * 
	 * @param resourceId
	 * @param subjectId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  subjectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either subjectId or resourceId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/subjects/{subjectId}/resources/{resourceId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET)
	public XnatAbstractresource getByIdAndSubjectId(@ApiParam(value = "The ID of the resource.") @PathVariable(required = false) final Integer resourceId,
			@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested resources wth ID {} and with subject ID {} ", getSessionUser().getUsername(),resourceId, subjectId);
		return _resourceService.findByIdAndSubjectId(getSessionUser(), resourceId, subjectId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, subjectId));
	}
	
	
	/**
	 * 
	 * @param projectId
	 * @param subjectId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  projectId and subjectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either projectId or subjectId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatAbstractresource> getByProjectIdAndSubjectId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
															     @ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId) throws NotFoundException, DataFormatException  {
		log.debug("User {} requested resources wth projectId {} and with subject ID{} ", getSessionUser().getUsername(),projectId, subjectId);
		return _resourceService.findByProjectIdAndSubjectId(getSessionUser(), projectId, subjectId);
	}
	
	/**
	 * 
	 * @param resourceId
	 * @param projectId
	 * @param subjectId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  projectId and subjectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either projectId or subjectId or resourceId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/resources/{resourceId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET)
	public XnatAbstractresource getByIdAndProjectIdAndSubjectId(@ApiParam(value = "The ID of the resource.") @PathVariable final Integer resourceId,
																@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
																@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested resources wth project ID {} , with subject ID {} and with ID {} ", getSessionUser().getUsername(),projectId, subjectId, resourceId);
		return  _resourceService.findByIdAndProjectIdAndSubjectId(getSessionUser(), resourceId, projectId, subjectId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, resourceId));
	}
	
	
	/**
	 * 
	 * @param experimentId
	 * @param assessedId
	 * @param type
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified ID and experimentId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either experimentId or assessedId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = {"/experiments/{assessedId}/assessors/{experimentId}/resources",
			 					 "/experiments/{assessedId}/assessors/{experimentId}/{type}/resources"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatAbstractresource> getByexperimentIdAndAssessed(@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
																   @ApiParam(value = "The ID of the assessed.") @PathVariable final String assessedId,
																   @ApiParam(value = "The type of resource.") @PathVariable(required = false) final String type) throws NotFoundException, DataFormatException {
		log.debug("User {} requested resources wth experiment ID {} and with assessed ID {} ", getSessionUser().getUsername(),experimentId, assessedId);
		return _resourceService.findByExperimentIdAndAssessedId(getSessionUser(), experimentId, assessedId, type);
	}
	
	
	/**
	 * 
	 * @param experimentId
	 * @param assessedId
	 * @param type
	 * @param resourceId
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified ID and experimentId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either experimentId or assessedId or resourceId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = {"/experiments/{assessedId}/assessors/{experimentId}/resources/{resourceId}",
								"/experiments/{experimentId}/assessors/{assessedId}/{type}/resources/{resourceId}"}, produces = MediaType.APPLICATION_XML_VALUE, method = GET)
	public XnatAbstractresource getByexperimentIdAndAssessedIdAndresourceId(@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
																			@ApiParam(value = "The ID of the assessed.") @PathVariable final String assessedId,
																			@ApiParam(value = "The type of resource") @PathVariable(required = false) final String type,
																			@ApiParam(value = "The ID of the resource.") @PathVariable final Integer resourceId) throws NotFoundException, DataFormatException{
		log.debug("User {} requested resources wth experiment ID {} , with assessed ID {} and with ID {} }", getSessionUser().getUsername(),experimentId, assessedId, resourceId);
		return _resourceService.findByExperimentIdAndAssessedIdAndResourceId(getSessionUser(), experimentId, assessedId, type, resourceId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, assessedId));
	}
	
	
	/**
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param experimentId
	 * @param assessedId
	 * @param type
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  projectId and subjectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either subjectId or experimentId or assessedId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = {"/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/assessors/{experimentId}/resources",
								 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/assessors/{experimentId}/{type}/resources"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatAbstractresource> getByIdAndProjectIdAndSubjectIdAndExperimentIdAndAssessorId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId,
			@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
			@ApiParam(value = "The ID of the assessed.") @PathVariable final String assessedId,
			@ApiParam(value = "The type string.") @PathVariable(required = false) final String type) throws NotFoundException, DataFormatException{
		log.debug("User {} requested resources wth project ID {} , with subject ID {} , with experiment ID {} and with assessedId {} }", getSessionUser().getUsername(),projectId, subjectId, experimentId, assessedId);
		return _resourceService.findByProjectIdAndSubjectIdAndExperimentIdAndAssessorId(getSessionUser(),projectId, subjectId, experimentId, assessedId,type);
	}
	
	/**
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param assessedId
	 * @param scanId
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  projectId and subjectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/scans/{scanId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatAbstractresource> getResourceByProjectAndSubjectAndExperimentAndScans(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId,
			@ApiParam(value = "The ID of the assessed.") @PathVariable(required = false) final String assessedId,
			@ApiParam(value = "The ID of the scan.") @PathVariable(required = false) final String scanId)throws Exception {
		log.debug("User {} requested resources wth project ID {} , with subject ID {} , with assessed ID {} and with scan ID {} }", getSessionUser().getUsername(),projectId, subjectId, assessedId, scanId);
		return _resourceService.findByProjectIdAndSubjectIdAndExperimentIdAndScanId(getSessionUser(), projectId, subjectId, assessedId, scanId);
	}
	
	
	/**
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param experimentId
	 * @param assessorId
	 * @param scanId
	 * @param label
	 * @param type
	 * @param eventReason
	 * @param eventId
	 * @param eventType
	 * @param eventAction
	 * @param eventComment
	 * @param description
	 * @param format
	 * @param content
	 * @param tags
	 * @param xnatResource
	 * @return
	 * @throws DataFormatException
	 * @throws NotFoundException
	 * @throws ResourceAlreadyExistsException
	 */
	 @ApiOperation(value = "Create a new resource", notes = "Creates the submitted resource.", response = XnatResourcecatalog.class)
	 @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created resource."),
	                @ApiResponse(code = 403, message = "The user doesn't have permission to create resource"),
	                @ApiResponse(code = 404, message = "The specified project doesn't exist"),
	                @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
	    @XapiRequestMapping(value = {"/projects/{projectId}/resources",
	    							 "/subjects/{subjectId}/resources", 
	    							 "/experiments/{experimentId}/resources",
	    							 "/experiments/{assessorId}/scans/{scanId}/resources",
	    							 "/experiments/{assessorId}/assessors/{experimentId}/resources",
	    							 "/experiments/{assessorId}/assessors/{experimentId}/{type}/resources",
	    							 "/projects/{projectId}/subjects/{subjectId}/resources",
	    							 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/resources",
	    							 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/scans/{scanId}/resources",
	    							 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/assessors/{experimentId}/resources",
	    							 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/assessors/{experimentId}/{type}/resources"},
	                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
	                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
	                        method = POST)
	    public XnatResourcecatalog createResource(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
	    										  @ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId,
	    										  @ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
	    										  @ApiParam(value = "The ID of the assessor.") @PathVariable(required = false) final String assessorId,
	    										  @ApiParam(value = "The ID of the scans.") @PathVariable(required = false) final String scanId,
	    										  @ApiParam(value = "The label of the resource.") @RequestParam(required = false) final String label,
	    										  @ApiParam(value = "The label of the type.") @RequestParam(required = false) final String type,
	    										  @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
	    										  @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
	    										  @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
	    										  @ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false)String eventAction,
	    										  @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment,
	    										  @ApiParam("The description value ") @RequestParam(name = "description", required = false)String description,
	    										  @ApiParam("The format value ") @RequestParam(name = "format", required = false)String format,
	    										  @ApiParam("The content value ") @RequestParam(name = "content", required = false)String content,
	    										  @ApiParam("The tags value ") @RequestParam(name = "tags", required = false)String [] tags,
	    										  @RequestBody final XnatResource xnatResource) throws DataFormatException, NotFoundException, ResourceAlreadyExistsException  {
	        log.debug("Creating  resource with project ID {}", projectId);
	        
	        if (StringUtils.isNotBlank(label) && !StringUtils.equals(xnatResource.getLabel(), label)) {
	            throw new DataFormatException("You specified the label " + label + " in your request but the resource is assigned to project " + projectId + ". These values must be the same.");
	        }
	        
	        List<XnatAbstractresource> xnatResourcecatalogs = _resourceService.findByProjectIdAndLabel(getSessionUser(), projectId, label);
	       
	        if(xnatResourcecatalogs.size()>0)
	        	throw new ResourceAlreadyExistsException("You specified the label in your request is alreay exists", label);
	        
	        return _resourceService.create(getSessionUser(),projectId, subjectId, experimentId,assessorId, scanId, type, xnatResource, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment ),
	        		description, format, content, tags);
	    }
	
	 
	 /**
	  * 
	  * @param projectId
	  * @param subjectId
	  * @param experimentId
	  * @param assessorId
	  * @param scanId
	  * @param type
	  * @param resourceId
	  * @param eventReason
	  * @param eventId
	  * @param eventType
	  * @param eventAction
	  * @param eventComment
	  */
	 @ApiOperation(value = "Delete an existing resource", notes = "Deletes the specified resource.")
	 @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified resource."),
		 			@ApiResponse(code = 403, message = "The user doesn't have permission to delete projects in the specified resource"),
		 			@ApiResponse(code = 404, message = "The specified resource or project doesn't exist"),
		 			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
	 @XapiRequestMapping(value = {"/projects/{projectId}/resources/{resourceId}", 
	    							 "/subjects/{subjectId}/resources/{resourceId}",
	    							 "/experiments/{experimentId}/resources/{resourceId}",
	    							 "/experiments/{assessorId}/scans/{scanId}/resources/{resourceId}",
	    							 "/experiments/{assessorId}/assessors/{experimentId}/resources/{resourceId}",
	    							 "/projects/{projectId}/subjects/{subjectId}/resources/{resourceId}",
	    							 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/resources/{resourceId}",
	    							 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/scans/{scanId}/resources/{resourceId}",
	    							 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/assessors/{experimentId}/resources/{resourceId}",
	    							 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/assessors/{experimentId}/{type}/resources/{resourceId}"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE)
	    public void deleteResource(@ApiParam("The ID of the project to be deleted") @PathVariable(required = false) final String projectId,
	    				   @ApiParam("The ID of the subject to be deleted") @PathVariable(required = false) final String subjectId,
	    				   @ApiParam("The ID of the experimentto be deleted") @PathVariable(required = false) final String experimentId,
	    				   @ApiParam(value = "The ID of the assessor.") @PathVariable(required = false) final String assessorId,
	    				   @ApiParam(value = "The ID of the scans.") @PathVariable(required = false) final String scanId,
	    				   @ApiParam(value = "The label of the type.") @RequestParam(required = false) final String type,
	    				   @ApiParam("The ID of the resource to be deleted") @PathVariable final String resourceId,
	    				   @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
	    				   @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
	    				   @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
	    				   @ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false)String eventAction,
	    				   @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) {
		log.debug("Deleting resource  with project ID {}", resourceId);
			_resourceService.delete(getSessionUser(), projectId, subjectId, experimentId,assessorId,scanId,type, resourceId, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment ));
	}
	 
	 
	 /**
	  * 
	  * @param projectId
	  * @param experimentId
	  * @param filepath
	  * @param recursive
	  * @param isXarReference
	  * @return
	  * @throws NotFoundException
	  * @throws DataFormatException
	  * @throws NotAuthenticatedException
	  * @throws InvalidFileCharacters
	 * @throws org.nrg.xnat.services.resources.impl.ResourceServiceImpl.InvalidFileCharacters 
	  */
	 @ApiOperation(value = "Gets the requested  project", notes = "Returns the  project with the specified ID", response = XnatProjectdata.class, responseContainer = "single")
	  @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
	    	         @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
	                 @ApiResponse(code = 404, message = "The requested project wasn't found."),
	                 @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
	    @XapiRequestMapping(value = {"/experiments/{experimentId}/DIR","/projects/{projectId}/experiments/{experimentId}/DIR"}, produces = {MediaType.APPLICATION_JSON_VALUE}, method = GET)
	    public List<DIRResource>  getAllDIRResources(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
                                                     @ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
                                                     @ApiParam(value = "The value  of the filepath.") @RequestParam(required = false) final String filepath,
                                                     @ApiParam(value = "The value of the recursive.") @RequestParam(required = false) final boolean recursive,
                                                     @ApiParam(value = "The value of the isXarReference.") @RequestParam(required = false) final boolean isXarReference) throws NotFoundException, DataFormatException, NotAuthenticatedException, InvalidFileCharacters {
			
		 log.debug("User {} requested project with ID {}", getSessionUser().getUsername(), projectId);
	    	 return _resourceService.findAllDIRResources(getSessionUser(), projectId, experimentId, filepath, recursive, isXarReference);
	    }
	 
	 
	/**
	 * 
	 * @param projectId
	 * @param experimentId
	 * @param filepath
	 * @param recursive
	 * @param isXarReference
	 * @param compression
	 * @param sRequest
	 * @param hRequest
	 * @return
	 * @throws InsufficientPrivilegesException
	 * @throws NoContentException
	 * @throws NotFoundException
	 * @throws NotAuthenticatedException
	 * @throws InvalidFileCharacters
	 * @throws InitializationException
	 * @throws org.nrg.xnat.services.resources.impl.ResourceServiceImpl.InvalidFileCharacters 
	 */
	@ApiOperation(value = "Downloads the contents of the specified resource XAR.", response = StreamingResponseBody.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "The requested resources were successfully downloaded."),
			@ApiResponse(code = 204, message = "No resources were specified."),
			@ApiResponse(code = 400, message = "Something is wrong with the request format."),
			@ApiResponse(code = 403, message = "The user is not authorized to access one or more of the specified resources."),
			@ApiResponse(code = 404, message = "The request was valid but one or more of the specified resources was not found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred") })
	@XapiRequestMapping(value = {"/experiments/{experimentId}/XAR","/projects/{projectId}/experiments/{experimentId}/XAR"}, produces = MediaTypeUtil.APPLICATION_XAR, method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<StreamingResponseBody> downloadXarResourceZip(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
		@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
		@ApiParam(value = "The value  of the filepath.") @RequestParam(required = false) final String filepath,
		@ApiParam(value = "The value  of the recursive.") @RequestParam(required = false) final boolean recursive,
		@ApiParam(value = "The value  of the isXarReference.") @RequestParam(required = false) final boolean isXarReference,
		@ApiParam(value = "The value  of the compression.") @RequestParam(required = false) final String compression,
		@ApiParam(value = "The value  of the sRequest.") final  HttpServletRequest sRequest,
		@ApiParam(value = "The value  of the hRequest.") @RequestHeader HttpHeaders hRequest) throws InsufficientPrivilegesException, NoContentException, NotFoundException, NotAuthenticatedException, InvalidFileCharacters, InitializationException, org.nrg.xnat.services.resources.impl.ResourceServiceImpl.InvalidFileCharacters {
		final UserI user = getSessionUser();
		
		StreamingResponseBody result  = _resourceService.findAllXARResources(user, projectId, experimentId, filepath, recursive, isXarReference,sRequest,hRequest,compression );
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaTypeUtil.APPLICATION_XAR)
				.header(HttpHeaders.CONTENT_DISPOSITION, _resourceService.getContentDisposition())
				.body(result);
	}
	
	
	/**
	 * 
	 * @param projectId
	 * @param contents
	 * @param formats
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  projectId", response = ResourceFile.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<ResourceFile> getByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
                                             @ApiParam(value = "The values of the contents.") @RequestParam(name="contents", required = false) final String[] contents,
                                             @ApiParam(value = "The values of the formats.") @RequestParam(name="formats", required = false) final String[] formats) throws NotFoundException, DataFormatException{
		log.debug("User {} requested  resource catalog with Project ID {}", getSessionUser().getUsername(), projectId);
		return _resourceService.findByProjectId(getSessionUser(), projectId, contents, formats);
	}
	
	/**
	 * 
	 * @param subjectId
	 * @param contents
	 * @param formats
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 * @throws ElementNotFoundException
	 */
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = ResourceFile.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested subjectId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog  wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/subjects/{subjectId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<ResourceFile> getBySubjectId(@ApiParam(value = "The ID of the subject.") @PathVariable  final String subjectId,
                                             @ApiParam(value = "The values of the contents.") @RequestParam(name="contents", required = false) final String[] contents,
                                             @ApiParam(value = "The values of the formats.") @RequestParam(name="formats", required = false) final String[] formats) throws NotFoundException, DataFormatException, ElementNotFoundException{
		log.debug("User {} requested  resource catalog with subject ID {}", getSessionUser().getUsername(), subjectId);
		return _resourceService.findBySubjectId(getSessionUser(), subjectId, contents, formats);
		
	}
	
	
	/**
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param contents
	 * @param formats
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 * @throws ElementNotFoundException
	 */
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  projectId", response = ResourceFile.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either projectId or subjectId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<ResourceFile> getByProjectIdAndSubjectId(@ApiParam(value = "The ID of the project.") @PathVariable  final String projectId,
                                                         @ApiParam(value = "The ID of the subject.") @PathVariable  final String subjectId,
                                                         @ApiParam(value = "The values of the contents.") @RequestParam(name="contents", required = false) final String[] contents,
                                                         @ApiParam(value = "The values of the formats.") @RequestParam(name="formats", required = false) final String[] formats) throws NotFoundException, DataFormatException, ElementNotFoundException {
		log.debug("User {} requested  resource catalog with Project ID {} and subject ID {}", getSessionUser().getUsername(), projectId, subjectId);
		return  _resourceService.findByProjectIdAndSubjectId(getSessionUser(), projectId, subjectId, contents, formats);
	}
	
	/**
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param experimentId
	 * @param contents
	 * @param formats
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 * @throws ElementNotFoundException
	 */
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  projectId", response = ResourceFile.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either projectId or subjectId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<ResourceFile> getByProjectIdAndSubjectIdAndExperimentId(@ApiParam(value = "The ID of the project.") @PathVariable  final String projectId,
                                                                        @ApiParam(value = "The ID of the subject.") @PathVariable  final String subjectId,
                                                                        @ApiParam(value = "The ID of the experiment.") @PathVariable  final String experimentId,
                                                                        @ApiParam(value = "The values of the contents.") @RequestParam(name="contents", required = false) final String[] contents,
                                                                        @ApiParam(value = "The values of the formats.") @RequestParam(name="formats", required = false) final String[] formats) throws NotFoundException, DataFormatException, ElementNotFoundException {
		log.debug("User {} requested  resource catalog with Project ID {} and subject ID {}", getSessionUser().getUsername(), projectId, subjectId);
		return  _resourceService.findByProjectIdAndSubjectIdAndExperimentId(getSessionUser(), projectId, subjectId,experimentId, contents, formats);
	}
	
	/**
	 * 
	 * @param projectId
	 * @param resourceId
	 * @param contents
	 * @param formats
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 */
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either projectId or resourceId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/resources/{resourceId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<ResourceFile> getByProjectIdAndResourceId(@ApiParam(value = "The ID of the project.") @PathVariable  final String projectId,
                                                          @ApiParam(value = "The ID of the resource.") @PathVariable  final Integer resourceId,
                                                          @ApiParam(value = "The values of the contents.") @RequestParam(name="contents", required = false) final String[] contents,
                                                          @ApiParam(value = "The values of the formats.") @RequestParam(name="formats", required = false) final String[] formats) throws NotFoundException, DataFormatException{
		log.debug("User {} requested  resource catalog with Project ID {}", getSessionUser().getUsername(), projectId);
		return _resourceService.findByProjectIdAndResourceId(getSessionUser(), projectId, resourceId,contents, formats);
		
	}
	
	/**
	 * 
	 * @param subjectId
	 * @param resourceId
	 * @param contents
	 * @param formats
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 * @throws ElementNotFoundException
	 */
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = ResourceFile.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either subjectId or resourceId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/subjects/{subjectId}/resources/{resourceId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<ResourceFile> getBySubjectIdAndResourceId(@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId,
                                                          @ApiParam(value = "The ID of the resource.") @PathVariable final Integer resourceId,
                                                          @ApiParam(value = "The values of the contents.") @RequestParam(name="contents", required = false) final String[] contents,
                                                          @ApiParam(value = "The values of the formats.") @RequestParam(name="formats", required = false) final String[] formats) throws NotFoundException, DataFormatException, ElementNotFoundException{
		log.debug("User {} requested  resource catalog with subject ID {} and resource ID {}", getSessionUser().getUsername(),subjectId, resourceId);
		return _resourceService.findBySubjectIdAndResourceId(getSessionUser(), subjectId, resourceId, contents, formats);
		
	}
	
	/**
	 * 
	 * @param experimentId
	 * @param assessorId
	 * @param contents
	 * @param formats
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 * @throws ElementNotFoundException
	 */
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = ResourceFile.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either experimentId or assessorId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/assessors/{assessorId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<ResourceFile> getByExperimentIdAndAssessorId(@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
                                                             @ApiParam(value = "The ID of the assessorId.") @PathVariable final String assessorId,
                                                             @ApiParam(value = "The values of the contents.") @RequestParam(name="contents", required = false) final String[] contents,
                                                             @ApiParam(value = "The values of the formats.") @RequestParam(name="formats", required = false) final String[] formats) throws NotFoundException, DataFormatException, ElementNotFoundException{
		log.debug("User {} requested  resource catalog with experiment ID {} and with assessor ID {}", getSessionUser().getUsername(),experimentId, assessorId);
		return _resourceService.findByExperimentIdAndAssessorId(getSessionUser(), experimentId, assessorId, contents, formats);
	}
	
	/**
	 * 
	 * @param experimentId
	 * @param assessorId
	 * @param resourceId
	 * @param contents
	 * @param formats
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 * @throws ElementNotFoundException
	 */
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = ResourceFile.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either experimentId or assessorId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/assessors/{assessorId}/resources/{resourceId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<ResourceFile> getByExperimentIdAndAssessorIdAndResourceId(@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
                                                                          @ApiParam(value = "The ID of the assessorId.") @PathVariable final String assessorId,
                                                                          @ApiParam(value = "The ID of the resource.") @PathVariable final Integer resourceId,
                                                                          @ApiParam(value = "The values of the contents.") @RequestParam(name="contents", required = false) final String[] contents,
                                                                          @ApiParam(value = "The values of the formats.") @RequestParam(name="formats", required = false) final String[] formats) throws NotFoundException, DataFormatException, ElementNotFoundException{
		log.debug("User {} requested  resource catalog with experiment ID {} and with assessor ID {} and with resource ID {}", getSessionUser().getUsername(),experimentId, assessorId, resourceId);
		return _resourceService.findByExperimentIdAndAssessorIdAndResourceId(getSessionUser(), experimentId, assessorId, resourceId, contents, formats);
	}
	
	/**
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param experimentId
	 * @param assessedId
	 * @param contents
	 * @param formats
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 * @throws ElementNotFoundException
	 */
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  files with the specified  projectId and subjectId", response = ResourceFile.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested files."),
					@ApiResponse(code = 400, message = "The requested either projectId or subjectId or experimentId or assessorId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/assessors/{assessedId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<ResourceFile> getByProjectIdAndSubjectIdAndExperimentIdAndAssessorId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
                                                                                     @ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId,
                                                                                     @ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
                                                                                     @ApiParam(value = "The ID of the assessed.") @PathVariable final String assessedId,
                                                                                     @ApiParam(value = "The values of the contents.") @RequestParam(name="contents", required = false) final String[] contents,
                                                                                     @ApiParam(value = "The values of the formats.") @RequestParam(name="formats", required = false) final String[] formats) throws NotFoundException, DataFormatException, ElementNotFoundException {
		log.debug("User {} requested  resource catalog with project ID {}, with subject ID {} , with experiment ID {} and with assessed ID {} ", getSessionUser().getUsername(), projectId, subjectId, experimentId, assessedId);
		return _resourceService.findByProjectIdAndSubjectIdAndExperimentIdAndAssessorId(getSessionUser(),projectId, subjectId, experimentId, assessedId, contents, formats);
	}
	
	
	/**
	 * 
	 * @param experimentId
	 * @param contents
	 * @param formats
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 * @throws ElementNotFoundException
	 */
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  experimentId", response = ResourceFile.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested files."),
					@ApiResponse(code = 400, message = "The requested experimentId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<ResourceFile> getByExperiment(@ApiParam(value = "The ID of the experiment.") @PathVariable  final String experimentId,
                                              @ApiParam(value = "The values of the contents.") @RequestParam(name="contents", required = false) final String[] contents,
                                              @ApiParam(value = "The values of the formats.") @RequestParam(name="formats", required = false) final String[] formats) throws NotFoundException, DataFormatException, ElementNotFoundException {
		log.debug("User {} requested  resource catalog with experiment ID {}", getSessionUser().getUsername(), experimentId);
		return _resourceService.findByExperimentId(getSessionUser(), experimentId, contents, formats);
	}
	
	/**
	 * 
	 * @param experimentId
	 * @param resourceId
	 * @param contents
	 * @param formats
	 * @return
	 * @throws NotFoundException
	 * @throws DataFormatException
	 * @throws ElementNotFoundException
	 */
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  experimentId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested files."),
					@ApiResponse(code = 400, message = "The requested either experimentId or resourceId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/resources/{resourceId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<ResourceFile> getByExperimentIdAndResourceId(@ApiParam(value = "The ID of the experiment.") @PathVariable  final String experimentId,
                                                             @ApiParam(value = "The ID of the resource.") @PathVariable final Integer resourceId,
                                                             @ApiParam(value = "The values of the contents.") @RequestParam(name="contents", required = false) final String[] contents,
                                                             @ApiParam(value = "The values of the formats.") @RequestParam(name="formats", required = false) final String[] formats) throws NotFoundException, DataFormatException, ElementNotFoundException {
		log.debug("User {} requested  resource catalog with experiment ID {} and resource ID {} ", getSessionUser().getUsername(), experimentId, resourceId);
		return _resourceService.findByExperimentIdAndResourceId(getSessionUser(), experimentId, resourceId,contents, formats);
	}
	
	/**
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param experimentId
	 * @param assessorId
	 * @param scanId
	 * @param type
	 * @param resourceId
	 * @param removeFiles
	 * @param eventReason
	 * @param eventId
	 * @param eventType
	 * @param eventAction
	 * @param eventComment
	 * @throws Exception
	 */
	 @ApiOperation(value = "Delete an existing resource file", notes = "Deletes the specified resource file.")
	    @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified resource file."),
	                   @ApiResponse(code = 403, message = "The user doesn't have permission to delete resource file in the specified resource file"),
	                   @ApiResponse(code = 404, message = "The specified project or project doesn't exist"),
	                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
	    @XapiRequestMapping(value = {"/projects/{projectId}/resources/{resourceId}/files",
	    							 "/projects/{projectId}/subjects/{subjectId}/resources/{resourceId}/files",
	    							 "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/resources/{resourceId}/files",
	    							 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/assessors/{experimentId}/resources/{resourceId}/files",
	    							 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/assessors/{experimentId}/{type}/resources/{resourceId}/files",
				     				 "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/scans/{scanId}/resources/{resourceId}/files"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE)
	    public void deleteFile(@ApiParam("The ID of the resource file to be deleted") @PathVariable(required = false) final String projectId,
	    						  @ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId,
	    						  @ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
	    						  @ApiParam(value = "The ID of the assessor.") @PathVariable(required = false) final String assessorId,
	    						  @ApiParam(value = "The ID of the scans.") @PathVariable(required = false) final String scanId,
	    						  @ApiParam(value = "The label of the type.") @RequestParam(required = false) final String type,
	    						  @ApiParam("The ID of the project") @PathVariable(required = false) final String  resourceId,
	    						  @ApiParam("The remove Files  value ") @RequestParam(name = "removeFiles", required = false)boolean removeFiles,
	    						  @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
	    						  @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
	    						  @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
	    						  @ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false)String eventAction,
	    						  @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) throws Exception {
	        log.debug("Deleting resource file with project ID {}", projectId);
	        _resourceService.deleteResourceFile(getSessionUser(), projectId,subjectId,experimentId,assessorId,scanId,type,resourceId, removeFiles ,XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment ));
	    }
	
	 /**
	  * 
	  * @param file
	  * @param projectId
	  * @param subjectId
	  * @param experimentId
	  * @param assessorId
	  * @param scanId
	  * @param type
	  * @param resourceId
	  * @param requestRename
	  * @param requestDesc
	  * @param requestFormat
	  * @param requestContent
	  * @param requestTags
	  * @param eventReason
	  * @param eventId
	  * @param eventType
	  * @param eventAction
	  * @param eventComment
	  * @return
	  * @throws Exception
	  */
	@ApiOperation(value = "Create a new resource file", notes = "Creates the submitted resource file.", response = Integer.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created project."),
    	@ApiResponse(code = 403, message = "The user doesn't have permission to create projects"),
    	@ApiResponse(code = 404, message = "The specified project doesn't exist"),
    	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/resources/{resourceId}/files", "/subjects/{subjectId}/resources/{resourceId}/files",
    		 					  "/experiments/{experimentId}/resources/{resourceId}/files", "/projects/{projectId}/subjects/{subjectId}/resources/{resourceId}/files",
    							  "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/resources/{resourceId}/files",
    							  "/experiments/{assessorId}/assessors/{experimentId}/resources/{resourceId}/files",
    							  "/experiments/{assessorId}/assessors/{experimentId}/{type}/resources/{resourceId}/files",
    							  "/experiments/{assessorId}/scans/{scanId}/resources/{resourceId}/files",
    							  "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/assessors/{experimentId}/resources/{resourceId}/files",
    							  "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/assessors/{experimentId}/{type}/resources/{resourceId}/files",
    							  "/projects/{projectId}/subjects/{subjectId}/experiments/{assessorId}/scans/{scanId}/resources/{resourceId}/files"},
     consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = POST)
    public Integer createResourceFile(@ApiParam("The resource file to be created.")  @RequestParam MultipartFile file,
    								  @ApiParam("The ID of the project.") @PathVariable(required = false) final String  projectId,
    								  @ApiParam("The ID of the subject.") @PathVariable(required = false) final String  subjectId,
    								  @ApiParam("The ID of the experiment.") @PathVariable(required = false) final String  experimentId,
    								  @ApiParam("The ID of the assessor.") @PathVariable(required = false) final String  assessorId,
    								  @ApiParam("The ID of the scan.") @PathVariable(required = false) final String  scanId,
    								  @ApiParam("The ID of the type") @PathVariable(required = false) final String  type,
    								  @ApiParam("The ID of the project") @PathVariable(required = false) final String  resourceId,
    								  @ApiParam("The file rename.") @RequestParam(name= "rename", required = false) final String requestRename,
    								  @ApiParam("The file description.") @RequestParam(name= "description", required = false) final String requestDesc,
    								  @ApiParam("The file format.") @RequestParam(name= "format",required = false) final String requestFormat,
    								  @ApiParam("Thefile content.") @RequestParam(name= "content", required = false) final String requestContent,
    								  @ApiParam("The file tags.") @RequestParam(name= "tags",required = false) final List<String>  requestTags,
    								  @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
    								  @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
    								  @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
    								  @ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false)String eventAction,
    								  @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) 
    		throws Exception {
        log.debug("Creating resource file with project ID: {}", projectId);
        
        XnatResourceInfo xnatResourceInfo = getXnatResourceInfo(requestContent,requestFormat,requestTags, requestDesc,requestRename,file);

       return _resourceService.createResourceFile(getSessionUser(), xnatResourceInfo, projectId, subjectId,experimentId,assessorId, scanId, type, resourceId, XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment ));
	}
	
	/**
	 * 
	 * @param requestContent
	 * @param requestFormat
	 * @param requestTags
	 * @param requestDesc
	 * @param requestRename
	 * @param file
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	private XnatResourceInfo getXnatResourceInfo(String requestContent, String requestFormat, List<String> requestTags, String requestDesc, String requestRename, MultipartFile file) throws IllegalStateException, IOException {
		return XnatResourceInfo.builder()
							   .username(getSessionUser().getUsername())
							   .created(new Date())
							   .content(requestContent)
							   .format(requestFormat)
							   .tags(requestTags)
							   .description(requestDesc)
							   .name(file.getOriginalFilename())
							   .fileSize(file.getSize())
							   .rename(requestRename)
							   .multipartFile(file).build();
	}
	
	
	/**
	 * 
	 * @param resources
	 * @param append
	 * @param checksum
	 * @param delete
	 * @param populateStats
	 * @param options
	 * @throws NotFoundException
	 * @throws DataFormatException
	 * @throws ClientException
	 * @throws ServerException
	 */
	 @ApiOperation(value = "create catalog refresh", notes = "Returns the  project with the specified ID", response = XnatProjectdata.class, responseContainer = "single")
	    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
	    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
	                   @ApiResponse(code = 404, message = "The requested project wasn't found."),
	                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
	    @XapiRequestMapping(value = "/services/refresh/catalog", produces = MediaType.APPLICATION_JSON_VALUE, method = POST)
	    public void createCatalogRefresh(@ApiParam(value = "The value of the resources.") @RequestParam(name="resources") final List<String> resources,
	    		@ApiParam(value = "The value of the append.") @RequestParam(name="append",required=false, defaultValue="false") final boolean append,
	    		@ApiParam(value = "The value of the checksum.") @RequestParam(name="checksum", required=false, defaultValue="false") final boolean checksum,
	    		@ApiParam(value = "The value of the delete.") @RequestParam(name="delete", required=false, defaultValue="false") final boolean delete,
	    		@ApiParam(value = "The value of the populateStats.") @RequestParam(name="populateStats", required=false, defaultValue="false") final boolean populateStats,
	    		@ApiParam(value = "The value of the options.") @RequestParam(name="options") List<String> options) throws NotFoundException, DataFormatException, ClientException, ServerException {
	    	log.debug("User {} requested resources {}", getSessionUser().getUsername(), resources);
	    	_resourceService.createCatalogRefresh( getSessionUser(),resources,append,checksum, delete,populateStats,options);
	    }
	 
	 
	 
	 /**
	  * 
	  * @param projectId
	  * @param request
	  * @return
	  * @throws NotFoundException
	  * @throws DataFormatException
	  * @throws InsufficientPrivilegesException
	  * @throws InitializationException
	  */
	 @ApiOperation(value = "Gets the All Triage resource", notes = "Returns the  Triage resource", response = String.class, responseContainer = "single")
	    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
	    	           @ApiResponse(code = 400, message = "The requested projectId wasn't found."),
	                   @ApiResponse(code = 404, message = "The requested Triage resource wasn't found."),
	                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
	    @XapiRequestMapping(value = "{/services/triage/projects/projectId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	    public List<TriageDto> getAll(@ApiParam("The ID of the project ") @PathVariable final String projectId,
	    		@ApiParam("The value of Http Servlet request") HttpServletRequest request) throws NotFoundException, DataFormatException, InsufficientPrivilegesException, InitializationException {
			log.debug("User {} requested Triage resource", getSessionUser().getUsername());
			return _resourceService.findTriageByProjectId(getSessionUser(),projectId, request);
		}
		
	 
	 /**
	  * 
	  * @param projectId
	  * @param xname
	  * @param file
	  * @param eventReason
	  * @param eventComment
	  * @param eventId
	  * @param target
	  * @param inbody
	  * @param overwrite
	  * @param format
	  * @param content
	  * @param event_reason
	  * @param extract
	  * @param request
	  * @throws InitializationException
	  * @throws DataFormatException
	  */
		@ApiOperation(value = "create Triage resource", notes = " creating the Triage resource", response = void.class, responseContainer = "single")
	    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested  Triage resource."),
	    	           @ApiResponse(code = 400, message = "The requested  Triage resource wasn't found."),
	                   @ApiResponse(code = 404, message = "The requested  Triage resource wasn't found."),
	                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
		 @XapiRequestMapping(value = {"/services/triage/projects/{PROJECT}/resources",
				 					  "/services/triage/{PROJECT}/resources/{XNAME}",
				 					  "/services/triage/{PROJECT}/resources/{XNAME}/files",
				 					  "/services/triage/{PROJECT}/resources/{XNAME}/files/{FILE}"}, 
		 					consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
		 					produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = POST)
	    public void createTriage(@ApiParam("The ID of the project ") @PathVariable final String projectId,
	    		@ApiParam("The value of xname") @PathVariable(required = false) final String xname,
	    		@ApiParam("The value of file") @PathVariable(required = false) final String file,
	    		@ApiParam("The value of eventReason") @RequestParam(required = false) final String eventReason,
	    		@ApiParam("The value of eventComment") @RequestParam(required = false) final String eventComment,
	    		@ApiParam("The value of eventId") @RequestParam(required = false) final String eventId,
	    		@ApiParam("The value of target") @RequestParam(required = false) final String target,
	    		@ApiParam("The value of inbody") @RequestParam(required = false) final boolean inbody,
	    		@ApiParam("The value of overwrite") @RequestParam(required = false) final String overwrite,
	    		@ApiParam("The value of format") @RequestParam(required = false) final String format,
	    		@ApiParam("The value of content") @RequestParam(required = false) final String content,
	    		@ApiParam("The value of event_reason") @RequestParam(required = false) final String event_reason,
	    		@ApiParam("The value of extract") @RequestParam(required = false) final String extract,
	    		@ApiParam("The value of Http Servlet request") HttpServletRequest request) throws InitializationException, DataFormatException {
			log.debug("User {} requested Study Routing", getSessionUser().getUsername());
			_resourceService.create(getSessionUser(), projectId, xname, file, eventReason, eventComment, eventId, target, inbody, overwrite, format, content, event_reason, extract, request);
		}
		
		
		
		/**
		 * 
		 * @param projectId
		 * @param xname
		 * @param file
		 * @param eventReason
		 * @param eventComment
		 * @param eventId
		 * @throws InsufficientPrivilegesException
		 * @throws InitializationException
		 */
		@ApiOperation(value = "delete  Triage resource", notes = " delete the  Triage resource ", response = void.class, responseContainer = "single")
	    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested  Triage resource."),
	    	           @ApiResponse(code = 400, message = "The requested  Triage resource wasn't found."),
	                   @ApiResponse(code = 404, message = "The requested  Triage resource wasn't found."),
	                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
		 @XapiRequestMapping(value = {"/services/triage/projects/{PROJECT}/resources",
				 					  "/services/triage/projects/{PROJECT}/resources/{XNAME}",
				 					  "/services/triage/projects/{PROJECT}/resources/{XNAME}/files",
				 					  "/services/triage/projects/{PROJECT}/resources/{XNAME}/files/{FILE}"}, 
		 produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE)
	    public void deleteStudyRouting(@ApiParam("The ID of the project ") @PathVariable final String projectId,
	    		@ApiParam("The value of xname") @PathVariable(required = false) final String xname,
	    		@ApiParam("The value of file") @PathVariable(required = false) final String file,
	    		@ApiParam("The value of xname") @RequestParam(required = false) final String eventReason,
	    		@ApiParam("The value of xname") @RequestParam(required = false) final String eventComment,
	    		@ApiParam("The value of xname") @RequestParam(required = false) final String eventId) throws InsufficientPrivilegesException, InitializationException {
			log.debug("User {} requested Study Routing", getSessionUser().getUsername());
			_resourceService.deleteTriage(getSessionUser(), projectId, xname, file, eventReason, eventComment, eventId);
		}
	 
	private final ResourceService _resourceService;

}