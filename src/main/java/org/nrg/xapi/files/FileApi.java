package org.nrg.xapi.files;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.helpers.resource.XnatResourceInfo;
import org.nrg.xnat.services.files.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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
					@ApiResponse(code = 400, message = "The requested projectId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatResourcecatalog> getByProjectId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId) throws NotFoundException, DataFormatException{
		log.debug("User {} requested  resource catalog with ProjectId {}", getSessionUser().getUsername(), projectId);
		return _fileService.findByProjectId(getSessionUser(), projectId).orElseThrow(() -> new NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, projectId));
	}
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested subjectId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog  wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/subjects/{subjectId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatResourcecatalog> getBySubjectId(@ApiParam(value = "The ID of the subject.") @PathVariable  final String subjectId) throws NotFoundException, DataFormatException{
		log.debug("User {} requested  resource catalog with subjectId {}", getSessionUser().getUsername(), subjectId);
	  return _fileService.findBySubjectId(getSessionUser(), subjectId).orElseThrow(() -> new NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, subjectId));
	}
	
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  projectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either projectId or subjectId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatResourcecatalog> getByProjectIdAndSubjectId(@ApiParam(value = "The ID of the project.") @PathVariable  final String projectId,
																@ApiParam(value = "The ID of the subject.") @PathVariable  final String subjectId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested  resource catalog with ProjectID {} and subjectId {}", getSessionUser().getUsername(), projectId);
		return _fileService.findByProjectIdAndSubjectId(getSessionUser(), projectId, subjectId).orElseThrow(() -> new NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, projectId));
	}
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either projectId or resourceId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/resources/{resourceId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatResourcecatalog> getByProjectIdAndResourceId(@ApiParam(value = "The ID of the project.") @PathVariable  final String projectId,
																 @ApiParam(value = "The ID of the resource.") @PathVariable  final Integer resourceId) throws NotFoundException, DataFormatException{
		log.debug("User {} requested  resource catalog with ProjectID {}", getSessionUser().getUsername(), projectId);
		return _fileService.findByProjectIdAndResourceId(getSessionUser(), projectId, resourceId).orElseThrow(() -> new NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, resourceId));
	}
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either subjectId or resourceId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/subjects/{subjectId}/resources/{resourceId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatResourcecatalog> getBySubjectIdAndResourceId(@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId,
																 @ApiParam(value = "The ID of the resource.") @PathVariable final Integer resourceId) throws NotFoundException, DataFormatException{
		log.debug("User {} requested  resource catalog with subjectId {} and resourceId {}", getSessionUser().getUsername(),subjectId, resourceId);
		return _fileService.findBySubjectIdAndResourceId(getSessionUser(), subjectId, resourceId).orElseThrow(() -> new NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, resourceId));
	}
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  subjectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested resource."),
					@ApiResponse(code = 400, message = "The requested either experimentId or assessorId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/assessors/{assessorId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatResourcecatalog> getByExperimentIdAndAssessorId(@ApiParam(value = "The ID of the experiment.") @PathVariable(required = false) final String experimentId,
																	@ApiParam(value = "The ID of the assessorId.") @PathVariable(required = false) final String assessorId) throws NotFoundException, DataFormatException{
		log.debug("User {} requested  resource catalog with experimentId {} and with assessorId {}", getSessionUser().getUsername(),experimentId, assessorId);
		return _fileService.findByExperimentIdAndAssessorId(getSessionUser(), experimentId, assessorId).orElseThrow(() -> new NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, assessorId));
	}
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  files with the specified  projectId and subjectId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested files."),
					@ApiResponse(code = 400, message = "The requested either projectId or subjectId or experimentId or assessorId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/projects/{projectId}/subjects/{subjectId}/experiments/{experimentId}/assessors/{assessedId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatResourcecatalog> getByProjectIdAndSubjectIdAndExperimentIdAndAssessorId(@ApiParam(value = "The ID of the project.") @PathVariable final String projectId,
																							@ApiParam(value = "The ID of the subject.") @PathVariable final String subjectId,
																							@ApiParam(value = "The ID of the experiment.") @PathVariable final String experimentId,
																							@ApiParam(value = "The ID of the assessed.") @PathVariable final String assessedId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested  resource catalog with projectId {}, with subjectId {} , with experimentId {} and with assessedId {} ", getSessionUser().getUsername(), projectId, subjectId, experimentId, assessedId);
		return _fileService.findByProjectIdAndSubjectIdAndExperimentIdAndAssessorId(getSessionUser(),projectId, subjectId, experimentId, assessedId).orElseThrow(() -> new NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, assessedId));
	}
	
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  experimentId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested files."),
					@ApiResponse(code = 400, message = "The requested experimentId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatResourcecatalog> getByExperiment(@ApiParam(value = "The ID of the experiment.") @PathVariable  final String experimentId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested  resource catalog with experimentId {}", getSessionUser().getUsername(), experimentId);
		return _fileService.findByExperimentId(getSessionUser(), experimentId).orElseThrow(() -> new NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, experimentId));
	}
	
	@ApiOperation(value = "Gets the requested  files", notes = "Returns the  resource with the specified  experimentId", response = XnatResourcecatalog.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested files."),
					@ApiResponse(code = 400, message = "The requested either experimentId or resourceId wasn't found."),
					@ApiResponse(code = 404, message = "The requested resource catalog wasn't found."),
					@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = "/experiments/{experimentId}/resources/{resourceId}/files", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public List<XnatResourcecatalog> getByExperimentIdAndResourceId(@ApiParam(value = "The ID of the experiment.") @PathVariable  final String experimentId,
																	@ApiParam(value = "The ID of the resource.") @PathVariable final Integer resourceId) throws NotFoundException, DataFormatException {
		log.debug("User {} requested  resource catalog with experimentId {} and resourceId {} ", getSessionUser().getUsername(), experimentId, resourceId);
		return _fileService.findByExperimentIdAndResourceId(getSessionUser(), experimentId, resourceId).orElseThrow(() -> new NotFoundException(XnatResourcecatalog.SCHEMA_ELEMENT_NAME, resourceId));
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
    		@ApiParam("The file description.") @RequestParam(name= "rename", required = false) final String requestRename,
    		@ApiParam("The file description.") @RequestParam(name= "description", required = false) final String requestDesc,
    		@ApiParam("The file format.") @RequestParam(name= "format",required = false) final String requestFormat,
    		@ApiParam("Thefile content.") @RequestParam(name= "content", required = false) final String requestContent,
    		@ApiParam("The file tags.") @RequestParam(name= "tags",required = false) final List<String>  requestTags) 
    		throws Exception {
        log.debug("Controller Api- file project: {}", projectId);
        
        XnatResourceInfo xnatResourceInfo = getXnatResourceInfo(requestContent,requestFormat,requestTags, requestDesc,requestRename,file);

       return _fileService.createResourceFile(getSessionUser(), xnatResourceInfo, projectId, subjectId,experimentId,assessorId, scanId, type, resourceId);
	}
	
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

	private final FileService _fileService;
}
