package org.nrg.xapi.resources;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatResource;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.resources.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
	
	@ApiOperation(value = "Gets the requested resources", notes = "Returns the  resources with the specified Experiment ID", response = XnatAbstractresource.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resources."),
		 			@ApiResponse(code = 400, message = "The requested experimentId wasn't found."),
		 			@ApiResponse(code = 404, message = "The requested resources wasn't found."),
		 			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatAbstractresource> getByExperimentId( @ApiParam(value = "The ID of the experiment.") @PathVariable  final String experimentId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested resources with experimentId {} }", getSessionUser().getUsername(), experimentId);
		return _resourceService.findByExperimentId(getSessionUser(), experimentId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, experimentId));
	}
	
	@ApiOperation(value = "Gets the requested resource", notes = "Returns the  resource with the specified ID and experimentId", response = XnatAbstractresource.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either experimentId or resourceId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/resources/{resourceId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET)
	public XnatAbstractresource getByIdAndExperimentId(@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
													   @ApiParam(value = "The ID of the resource.") @PathVariable  final Integer resourceId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested resources with experimentId {} and with ID {}}", getSessionUser().getUsername(), experimentId, resourceId);
		return _resourceService.findByIdAndExperimentId(getSessionUser(), resourceId, experimentId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, experimentId));
	}
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified ID and experimentId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either projectId or subjectId or experimentId  wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatAbstractresource> getByProjectIdAndSubjectIdExperimentId(
			@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId,
			@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested resources with projectId {} , with subjectId {} and with experimentId {} }", getSessionUser().getUsername(),projectId, subjectId , experimentId);
		return _resourceService.findByProjectIdAndSubjectIdAndExperimentId(getSessionUser(), projectId, subjectId, experimentId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, experimentId));
	}
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified ID and experimentId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
				    @ApiResponse(code = 400, message = "The requested either assessedId or scanId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{assessedId}/scans/{scanId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatAbstractresource> getByxperimentIdAndScanId(@ApiParam(value = "The ID of the assessed.") @PathVariable final String assessedId,
															    @ApiParam(value = "The ID of the scan.") @PathVariable final String scanId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested resources with assessedId {} and with scanId {} }", getSessionUser().getUsername(),assessedId, scanId);
		return _resourceService.findByExperimentIdAndScanId(getSessionUser(), assessedId, scanId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, assessedId));
	}

	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  projectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatAbstractresource> getByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId)throws Exception {
		log.debug("User {} requested resources with projectId {} }", getSessionUser().getUsername(),projectId);
		return _resourceService.findByProjectId(getSessionUser(), projectId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, projectId));
	}
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  Id and projectId", response = XnatAbstractresource.class, responseContainer = "Single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either projectId or resourceId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/resources/{resourceId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET)
	public XnatAbstractresource getByIdAndProjectId(@ApiParam(value = "The ID of the resource.") @PathVariable final Integer resourceId,
													@ApiParam(value = "The ID of the project.") @PathVariable final String projectId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested resources with ID {} and with projectId {} }", getSessionUser().getUsername(),resourceId, projectId);
		return _resourceService.findByIdAndProjectId(getSessionUser(), resourceId, projectId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, resourceId));
	}
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  subjectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested subjectId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/subjects/{subjectId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatAbstractresource> getBySubject(@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested resources with subjectId {}}", getSessionUser().getUsername(),subjectId);
		return _resourceService.findBySubjectId(getSessionUser(), subjectId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, subjectId));
	}
	
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  subjectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either subjectId or resourceId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/subjects/{subjectId}/resources/{resourceId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET)
	public XnatAbstractresource getByIdAndSubjectId(@ApiParam(value = "The ID of the resource.") @PathVariable(required = false) final Integer resourceId,
			@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested resources wth ID {} and with subjectId {} }", getSessionUser().getUsername(),resourceId, subjectId);
		return _resourceService.findByIdAndSubjectId(getSessionUser(), resourceId, subjectId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, subjectId));
	}
	
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  projectId and subjectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either projectId or subjectId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatAbstractresource> getByProjectIdAndSubjectId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
															     @ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId) throws NotFoundException, DataFormatException  {
		log.debug("User {} requested resources wth projectId {} and with subjectId {} }", getSessionUser().getUsername(),projectId, subjectId);
		return _resourceService.findByProjectIdAndSubjectId(getSessionUser(), projectId, subjectId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, subjectId));
	}
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  projectId and subjectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either projectId or subjectId or resourceId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/resources/{resourceId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET)
	public XnatAbstractresource getByIdAndProjectIdAndSubjectId(@ApiParam(value = "The ID of the resource.") @PathVariable final Integer resourceId,
																@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
																@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested resources wth projectId {} , with subjectId {} and with ID {} }", getSessionUser().getUsername(),projectId, subjectId, resourceId);
		return  _resourceService.findByIdAndProjectIdAndSubjectId(getSessionUser(), resourceId, projectId, subjectId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, resourceId));
	}
	
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
		log.debug("User {} requested resources wth experimentId {} and with assessedId {} }", getSessionUser().getUsername(),experimentId, assessedId);
		return _resourceService.findByExperimentIdAndAssessedId(getSessionUser(), experimentId, assessedId, type).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, assessedId));
	}
	
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
		log.debug("User {} requested resources wth experimentId {} , with assessedId{} and with ID {} }", getSessionUser().getUsername(),experimentId, assessedId, resourceId);
		return _resourceService.findByExperimentIdAndAssessedIdAndResourceId(getSessionUser(), experimentId, assessedId, type, resourceId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, assessedId));
	}
	
	
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
		log.debug("User {} requested resources wth projectId {} , with subjectId{} , with experimentId {} and with assessedId {} }", getSessionUser().getUsername(),projectId, subjectId, experimentId, assessedId);
		return _resourceService.findByProjectIdAndSubjectIdAndExperimentIdAndAssessorId(getSessionUser(),projectId, subjectId, experimentId, assessedId,type).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, assessedId));
	}
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  projectId and subjectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 404, message = "The requested resource wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{assessedId}/scans/{scanId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatAbstractresource> getResourceByProjectAndSubjectAndExperimentAndScans(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId,
			@ApiParam(value = "The ID of the assessed.") @PathVariable(required = false) final String assessedId,
			@ApiParam(value = "The ID of the scan.") @PathVariable(required = false) final String scanId)throws Exception {
		log.debug("User {} requested resources wth projectId {} , with subjectId{} , with assessedId {} and with scanId {} }", getSessionUser().getUsername(),projectId, subjectId, assessedId, scanId);
		return _resourceService.findByProjectIdAndSubjectIdAndExperimentIdAndScanId(getSessionUser(), projectId, subjectId, assessedId, scanId).orElseThrow(() -> new NotFoundException(XnatAbstractresource.SCHEMA_ELEMENT_NAME, scanId));
	}
	
	
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
				 @RequestBody final XnatResource xnatResource) throws Exception {
	        log.debug("Controller Api- Create resource: {}", projectId);
	        
	        if (StringUtils.isNotBlank(label) && !StringUtils.equals(xnatResource.getLabel(), label)) {
	            throw new DataFormatException("You specified the label " + label + " in your request but the resource is assigned to project " + projectId + ". These values must be the same.");
	        }
	        
	        List<XnatAbstractresource> xnatResourcecatalogs = _resourceService.findByProjectIdAndLabel(getSessionUser(), projectId, label).get();
	       
	        if(xnatResourcecatalogs.size()>0)
	        	throw new ResourceAlreadyExistsException("You specified the label in your request is alreay exists", label);
	        
	        return _resourceService.create(getSessionUser(),projectId, subjectId, experimentId,assessorId, scanId, type, xnatResource );
	    }
	
	 
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
	    public void deleteProject(@ApiParam("The ID of the project to be deleted") @PathVariable(required = false) final String projectId,
	    		@ApiParam("The ID of the subject to be deleted") @PathVariable(required = false) final String subjectId,
	    		@ApiParam("The ID of the experimentto be deleted") @PathVariable(required = false) final String experimentId,
	    		@ApiParam(value = "The ID of the assessor.") @PathVariable(required = false) final String assessorId,
	    		@ApiParam(value = "The ID of the scans.") @PathVariable(required = false) final String scanId,
	    		@ApiParam(value = "The label of the type.") @RequestParam(required = false) final String type,
			@ApiParam("The ID of the resource to be deleted") @PathVariable final String resourceId) {
		log.debug("Controller Api- Delete resource {}", resourceId);
			_resourceService.deleteByProjectIdAndResourceId(getSessionUser(), projectId, subjectId, experimentId,assessorId,scanId,type, resourceId);
	}
	 
	private final ResourceService _resourceService;

}