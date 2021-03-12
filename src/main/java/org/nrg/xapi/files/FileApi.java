package org.nrg.xapi.files;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;


import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.files.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT File Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class FileApi extends AbstractXapiProjectRestController {

	@Autowired
	public FileApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final FileService fileService) {
		super(userManagementService, roleHolder);
		_fileService = fileService;
	}
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  projectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatResourcecatalog>> getByProject(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId)throws Exception {
		log.debug("Controller Api- get files by  projectId");
		List<XnatResourcecatalog> xnatResourcecatalogs = _fileService.findByProject(getSessionUser(), projectId);
		if (xnatResourcecatalogs == null) {
			throw new NotFoundException("No files with projectId  was found.");
		}
		return new ResponseEntity<>(xnatResourcecatalogs, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/subjects/{subjectId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatResourcecatalog>> getBySubject(@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId)throws Exception {
		log.debug("Controller Api- get files by  subjectId");
		List<XnatResourcecatalog> xnatResourcecatalogs = _fileService.findBySubject(getSessionUser(), subjectId);
		if (xnatResourcecatalogs == null) {
			throw new NotFoundException("No files with projectId  was found.");
		}
		return new ResponseEntity<>(xnatResourcecatalogs, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  projectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatResourcecatalog>> getByProjectAndSubject(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId)throws Exception {
		log.debug("Controller Api- get files by  projectId");
		List<XnatResourcecatalog> xnatResourcecatalogs = _fileService.findByProjectAndSubject(getSessionUser(), projectId, subjectId);
		if (xnatResourcecatalogs == null) {
			throw new NotFoundException("No files with projectId  was found.");
		}
		return new ResponseEntity<>(xnatResourcecatalogs, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/resources/{resourceId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatResourcecatalog>> getByProjectAndResource(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			@ApiParam(value = "The ID of the resource.") @PathVariable(required = false) final Integer resourceId)throws Exception {
		log.debug("Controller Api- get files by  projectId And resourceId ");
		List<XnatResourcecatalog> xnatResourcecatalogs = _fileService.findByProjectAndResource(getSessionUser(), projectId, resourceId);
		if (xnatResourcecatalogs == null) {
			throw new NotFoundException("No files with projectId  was found.");
		}
		return new ResponseEntity<>(xnatResourcecatalogs, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/subjects/{subjectId}/resources/{resourceId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatResourcecatalog>> getBySubjectAndResource(@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId,
			@ApiParam(value = "The ID of the resource.") @PathVariable(required = false) final Integer resourceId)throws Exception {
		log.debug("Controller Api- get files by  subjectId And resourceId ");
		List<XnatResourcecatalog> xnatResourcecatalogs = _fileService.findBySubjectAndResource(getSessionUser(), subjectId, resourceId);
		if (xnatResourcecatalogs == null) {
			throw new NotFoundException("No files with projectId  was found.");
		}
		return new ResponseEntity<>(xnatResourcecatalogs, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/assessors/{assessorId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatResourcecatalog>> getByExperimentAndAssessors(@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
			@ApiParam(value = "The ID of the assessorId.") @PathVariable(required = false) final String assessorId)throws Exception {
		log.debug("Controller Api- get files by  experimentId And assessorId ");
		List<XnatResourcecatalog> xnatResourcecatalogs = _fileService.findByExperimentAndAssessors(getSessionUser(), experimentId, assessorId);
		if (xnatResourcecatalogs == null) {
			throw new NotFoundException("No files with projectId  was found.");
		}
		return new ResponseEntity<>(xnatResourcecatalogs, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  files with the specified  projectId and subjectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested files."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/assessors/{assessedId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatResourcecatalog>> getByIdAndProjectAndSubjectAndExperimentAndAssessors(@ApiParam(value = "The ID of the project.") @PathVariable(required = false) final String projectId,
			@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId,
			@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
			@ApiParam(value = "The ID of the assessed.") @PathVariable(required = false) final String assessedId)throws Exception {
		log.debug("Controller Api- get resources by  projectId");
		List<XnatResourcecatalog> xnatResourcecatalogs = _fileService.findByIdAndProjectAndSubjectAndExperimentAndAssessors(getSessionUser(),projectId, subjectId, experimentId, assessedId);
		if (xnatResourcecatalogs == null) {
			throw new NotFoundException("No resource with projectId and subjectId  was found.");
		}
		return new ResponseEntity<>(xnatResourcecatalogs, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  experimentId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested files."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatResourcecatalog>> getByExperiment(@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId)throws Exception {
		log.debug("Controller Api- get files by  experimentId");
		List<XnatResourcecatalog> xnatResourcecatalogs = _fileService.findByExperiment(getSessionUser(), experimentId);
		if (xnatResourcecatalogs == null) {
			throw new NotFoundException("No files with projectId  was found.");
		}
		return new ResponseEntity<>(xnatResourcecatalogs, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  experimentId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested files."),
	@ApiResponse(code = 404, message = "The requested resource wasn't found."),
	@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/resources/{resourceId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public ResponseEntity<List<XnatResourcecatalog>> getByExperimentAndResource(@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
			@ApiParam(value = "The ID of the resource.") @PathVariable(required = false) final Integer resourceId)throws Exception {
		log.debug("Controller Api- get files by  experimentId");
		List<XnatResourcecatalog> xnatResourcecatalogs = _fileService.findByExperimentAndResource(getSessionUser(), experimentId, resourceId);
		if (xnatResourcecatalogs == null) {
			throw new NotFoundException("No files with projectId  was found.");
		}
		return new ResponseEntity<>(xnatResourcecatalogs, HttpStatus.OK);
	}
	
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
	    public void deleteProject(@ApiParam("The ID of the resource file to be deleted") @PathVariable(required = false) final String projectId,
	    		@ApiParam(value = "The ID of the subject.") @PathVariable(required = false) final String subjectId,
	    		@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
	    		@ApiParam(value = "The ID of the assessor.") @PathVariable(required = false) final String assessorId,
	    		@ApiParam(value = "The ID of the scans.") @PathVariable(required = false) final String scanId,
	    		@ApiParam(value = "The label of the type.") @RequestParam(required = false) final String type,
	    		@ApiParam("The ID of the project") @PathVariable(required = false) final String  resourceId) throws Exception {
	        log.debug("Controller Api- Delete project {}", projectId);
	        _fileService.deleteResourceFile(getSessionUser(), projectId,subjectId,experimentId,assessorId,scanId,type, resourceId);
	    }
	
	@ApiOperation(value = "Create a new resource file", notes = "Creates the submitted resource file.", response = void.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created project."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to create projects"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = {"/projects/{projectId}/resources/{resourceId}/files"},
    
                        consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
                        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                        method = POST)
    public void createResourceFile(@ApiParam("The resource file to be created.")  @RequestParam MultipartFile file,
    		@ApiParam("The ID of the project.") @PathVariable(required = false) final String  projectId,
    		@ApiParam("The ID of the project") @PathVariable(required = false) final String  resourceId,
    		@ApiParam("The file description.") @RequestParam(name= "rename", required = false) final String requestRename,
    		@ApiParam("The file description.") @RequestParam(name= "description", required = false) final String requestDesc,
    		@ApiParam("The file format.") @RequestParam(name= "format",required = false) final String requestFormat,
    		@ApiParam("Thefile content.") @RequestParam(name= "content", required = false) final String requestContent,
    		@ApiParam("The file tags.") @RequestParam(name= "tags",required = false) final String []  requestTags) 
    		throws Exception {
        log.debug("Controller Api- file project: {}", projectId);
       
         _fileService.createResourceFile(getSessionUser(),file , projectId, resourceId, requestRename, requestDesc, requestFormat, requestContent, requestTags);
    }
	
	private final FileService _fileService;
}
