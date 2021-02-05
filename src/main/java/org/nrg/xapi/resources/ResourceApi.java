package org.nrg.xapi.resources;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatResource;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.resources.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
	
	@ApiOperation(value = "Gets the requested  resources", notes = "Returns the  resources with the specified Experiment ID", response = XnatAbstractresource.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resources."),
	@ApiResponse(code = 404, message = "The requested resources wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatAbstractresource>> getResourceByExperimentId(
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId)
			throws Exception {
		log.debug("Controller Api- get resources by experimentId");
		List<XnatAbstractresource> xnatAbstractresources = _resourceService.findByExperimentId(getSessionUser(), experimentId);
		if (xnatAbstractresources == null) {
			throw new NotFoundException("No resources with experimentId was found.");
		}
		return new ResponseEntity<>(xnatAbstractresources, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified ID and experimentId", response = XnatAbstractresource.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/resources/{resourceId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET)
	public ResponseEntity<XnatAbstractresource> getResourceByIdAndExperimentId(
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
			@ApiParam(value = "The ID of the resource.") @PathVariable(required = false) final Integer resourceId)
			throws Exception {
		log.debug("Controller Api- get resources by Id and experimentId");
		XnatAbstractresource xnatAbstractresource = _resourceService.findByIdAndExperimentId(getSessionUser(), resourceId, experimentId);
		if (xnatAbstractresource == null) {
			throw new NotFoundException("No resource with ID and experimentId  was found.");
		}
		return new ResponseEntity<>(xnatAbstractresource, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified ID and experimentId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatAbstractresource>> getResourceByProjectIdAndSubjectIdExperimentId(
			@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId,
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId)
			throws Exception {
		log.debug("Controller Api- get resources by Id and experimentId");
		List<XnatAbstractresource> xnatAbstractresources = _resourceService.findByProjectAndSubjectAndExperiment(getSessionUser(), projectId, subjectId, experimentId);
		if (xnatAbstractresources == null) {
			throw new NotFoundException("No resource with ID and experimentId  was found.");
		}
		return new ResponseEntity<>(xnatAbstractresources, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified ID and experimentId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{assessedId}/scans/{scanId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatAbstractresource>> getResourceByxperimentIdAndScanId(
			@ApiParam(value = "The ID of the assessed.") @PathVariable(required = false) final String assessedId,
			@ApiParam(value = "The ID of the scan.") @PathVariable(required = false) final String scanId)
			throws Exception {
		log.debug("Controller Api- get resources by Id and experimentId");
		List<XnatAbstractresource> xnatAbstractresources = _resourceService.findResourceByExperimentAndScan(getSessionUser(), assessedId, scanId);
		if (xnatAbstractresources == null) {
			throw new NotFoundException("No resource with ID and experimentId  was found.");
		}
		return new ResponseEntity<>(xnatAbstractresources, HttpStatus.OK);
	}

	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  projectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatAbstractresource>> getResourceByProject(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId)throws Exception {
		log.debug("Controller Api- get resources by  projectId");
		List<XnatAbstractresource> xnatAbstractresources = _resourceService.findByProject(getSessionUser(), projectId);
		if (xnatAbstractresources == null) {
			throw new NotFoundException("No resource with projectId  was found.");
		}
		return new ResponseEntity<>(xnatAbstractresources, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  Id and projectId", response = XnatAbstractresource.class, responseContainer = "Single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/resources/{resourceId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET)
	public ResponseEntity<XnatAbstractresource> getResourceByIdAndProject(@ApiParam(value = "The ID of the resource.") @PathVariable(required = false) final Integer resourceId,
			@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId)throws Exception {
		log.debug("Controller Api- get resources by  Id and projectId");
		XnatAbstractresource xnatAbstractresource = _resourceService.findByIdAndProject(getSessionUser(), resourceId, projectId);
		if (xnatAbstractresource == null) {
			throw new NotFoundException("No resource with Id and projectId  was found.");
		}
		return new ResponseEntity<>(xnatAbstractresource, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  subjectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/subjects/{subjectId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatAbstractresource>> getResourceBySubject(@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId)throws Exception {
		log.debug("Controller Api- get resources by  subjectId");
		List<XnatAbstractresource> xnatAbstractresources = _resourceService.findBySubject(getSessionUser(), subjectId);
		if (xnatAbstractresources == null) {
			throw new NotFoundException("No resource with subjectId  was found.");
		}
		return new ResponseEntity<>(xnatAbstractresources, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  subjectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/subjects/{subjectId}/resources/{resourceId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET)
	public ResponseEntity<XnatAbstractresource> getResourceByIdAndSubject(@ApiParam(value = "The ID of the resource.") @PathVariable(required = false) final Integer resourceId,
			@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId)throws Exception {
		log.debug("Controller Api- get resources by  subjectId");
		XnatAbstractresource xnatAbstractresource = _resourceService.findByIdAndSubject(getSessionUser(), resourceId, subjectId);
		if (xnatAbstractresource == null) {
			throw new NotFoundException("No resource with subjectId  was found.");
		}
		return new ResponseEntity<>(xnatAbstractresource, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  projectId and subjectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatAbstractresource>> getResourceByProjectAndSubject(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId)throws Exception {
		log.debug("Controller Api- get resources by  projectId");
		List<XnatAbstractresource> xnatAbstractresources = _resourceService.findByProjectAndSubject(getSessionUser(), projectId, subjectId);
		if (xnatAbstractresources == null) {
			throw new NotFoundException("No resource with projectId and subjectId  was found.");
		}
		return new ResponseEntity<>(xnatAbstractresources, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  projectId and subjectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/resources/{resourceId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET)
	public ResponseEntity<XnatAbstractresource> getResourceByIdAndProjectAndSubject(@ApiParam(value = "The ID of the resource.") @PathVariable(required = false) final Integer resourceId,
			@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId)throws Exception {
		log.debug("Controller Api- get resources by  projectId");
		XnatAbstractresource xnatAbstractresource = _resourceService.findByIdAndProjectAndSubject(getSessionUser(), resourceId, projectId, subjectId);
		if (xnatAbstractresource == null) {
			throw new NotFoundException("No resource with projectId and subjectId  was found.");
		}
		return new ResponseEntity<>(xnatAbstractresource, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified ID and experimentId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/assessors/{assessedId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatAbstractresource>> getResourceByexperimentIdAndAssessed(
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
			@ApiParam(value = "The ID of the assessed.") @PathVariable(required = false) final String assessedId )
			throws Exception {
		log.debug("Controller Api- get resources by assessedId, resourceId and experimentId");
		List<XnatAbstractresource> xnatAbstractresources = _resourceService.findResourceByexperimentIdAndAssessedId(getSessionUser(), experimentId, assessedId);
		if (xnatAbstractresources == null) {
			throw new NotFoundException("No resource with ID and experimentId  was found.");
		}
		return new ResponseEntity<>(xnatAbstractresources, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified ID and experimentId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/assessors/{assessedId}/resources/{resourceId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET)
	public ResponseEntity<XnatAbstractresource> getResourceByexperimentIdAndAssessedIdAndresourceId(
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
			@ApiParam(value = "The ID of the assessed.") @PathVariable(required = false) final String assessedId,
			@ApiParam(value = "The ID of the resource.") @PathVariable(required = false) final Integer resourceId)
			throws Exception {
		log.debug("Controller Api- get resources by assessedId, resourceId and experimentId");
		XnatAbstractresource xnatAbstractresource = _resourceService.findResourceByexperimentIdAndAssessedIdAndResourceId(getSessionUser(), experimentId, assessedId, resourceId);
		if (xnatAbstractresource == null) {
			throw new NotFoundException("No resource with ID and experimentId  was found.");
		}
		return new ResponseEntity<>(xnatAbstractresource, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Gets the requested  resource", notes = "Returns the  resource with the specified  projectId and subjectId", response = XnatAbstractresource.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/assessors/{assessedId}/resources", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatAbstractresource>> getByIdAndProjectAndSubjectAndExperimentAndAssessors(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId,
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
			@ApiParam(value = "The ID of the assessed.") @PathVariable(required = false) final String assessedId)throws Exception {
		log.debug("Controller Api- get resources by  projectId");
		List<XnatAbstractresource> xnatAbstractresources = _resourceService.findByIdAndProjectAndSubjectAndExperimentAndAssessors(getSessionUser(),projectId, subjectId, experimentId, assessedId);
		if (xnatAbstractresources == null) {
			throw new NotFoundException("No resource with projectId and subjectId  was found.");
		}
		return new ResponseEntity<>(xnatAbstractresources, HttpStatus.OK);
	}
	
	
	 @ApiOperation(value = "Create a new resource", notes = "Creates the submitted resource.", response = XnatResourcecatalog.class)
	    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created resource."),
	                   @ApiResponse(code = 403, message = "The user doesn't have permission to create resource"),
	                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
	                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
	    @XapiRequestMapping(value = "/projects/{projectId}/resources",
	                        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
	                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
	                        method = POST)
	    public XnatResourcecatalog createResource(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
	    		@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId,
	    		@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
	    		@ApiParam(value = "The label of the resource.") @RequestParam(required = false) final String label,
				 @RequestBody final XnatResource xnatResource) throws Exception {
	        log.debug("Controller Api- Create resource: {}", projectId);
	        
	        if (StringUtils.isNotBlank(label) && !StringUtils.equals(xnatResource.getLabel(), label)) {
	            throw new DataFormatException("You specified the label " + label + " in your request but the resource is assigned to project " + projectId + ". These values must be the same.");
	        }
	        
	        List<XnatAbstractresource> xnatResourcecatalogs = _resourceService.findByProjectAndLabel(getSessionUser(), projectId, label);
	       
	        if(xnatResourcecatalogs.size()>0)
	        	throw new ResourceAlreadyExistsException("You specified the label in your request is alreay exists", label);
	        
	        return _resourceService.create(getSessionUser(),projectId, xnatResource );
	    }
	
	private final ResourceService _resourceService;

}
