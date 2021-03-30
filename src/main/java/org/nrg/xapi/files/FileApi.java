package org.nrg.xapi.files;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.helpers.resource.XnatResourceInfo;
import org.nrg.xnat.services.files.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
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
        
        final InputStreamResource resource = new InputStreamResource(file.getInputStream(), StringUtils.defaultIfBlank(requestRename, file.getOriginalFilename()));
        
        XnatResourceInfo xnatResourceInfo = getXnatResourceInfo(requestContent,requestFormat,requestTags, requestDesc,requestRename,resource,file);
       
       return _fileService.createResourceFile(getSessionUser(), xnatResourceInfo, projectId, subjectId,experimentId,assessorId, scanId, type, resourceId);
	}
	
	private XnatResourceInfo getXnatResourceInfo(String requestContent, String requestFormat, List<String> requestTags, String requestDesc, String requestRename, InputStreamResource resource, MultipartFile file) throws IllegalStateException, IOException {
		 XnatResourceInfo xnatResourceInfo = new XnatResourceInfo(getSessionUser(), new Date(), new Date());
        xnatResourceInfo.setContent(Objects.nonNull(requestContent)?requestContent :null);
        xnatResourceInfo.setFormat(Objects.nonNull(requestFormat)?requestFormat :null);
        xnatResourceInfo.setTags(Objects.nonNull(requestTags)?requestTags :null);
        xnatResourceInfo.setDescription(Objects.nonNull(requestDesc)?requestDesc :null);
        xnatResourceInfo.setName(Objects.nonNull(file.getOriginalFilename())?file.getOriginalFilename() :null);
        xnatResourceInfo.setFileSize(Objects.nonNull(file.getSize())?file.getSize() :null);
        xnatResourceInfo.setRename(Objects.nonNull(requestRename)?requestRename :null);
        xnatResourceInfo.setResource(Objects.nonNull(resource)?resource :null);
        File f = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        file.transferTo(f);
        xnatResourceInfo.setFile(f);
		return xnatResourceInfo;
	}

	private final FileService _fileService;
}
