package org.nrg.xapi.importer;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.helpers.resource.XnatResourceInfo;
import org.nrg.xnat.services.importer.ImporterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT Import Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class ImporterApi extends AbstractXapiProjectRestController {

	@Autowired
	public ImporterApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final ImporterService importerService) {
		super(userManagementService, roleHolder);
		_importerService = importerService;
	}

	@ApiOperation(value = "Create a new resource file", notes = "Creates the submitted resource file.", response = Integer.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created project."),
    @ApiResponse(code = 403, message = "The user doesn't have permission to create projects"),
    @ApiResponse(code = 404, message = "The specified project doesn't exist"),
    @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
	@XapiRequestMapping(value = "/services/import",
			consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = POST)
	public List<String> importFile(@ApiParam("The resource file to be created.")  @RequestParam MultipartFile file, HttpServletRequest request) throws IOException, DataFormatException, ServerException, ClientException, NotFoundException {
		XnatResourceInfo xnatResourceInfo = getXnatResourceInfo(file);
		log.debug("User {} requested to import file", getSessionUser().getUsername());
		return _importerService.importFiles(getSessionUser(), request, xnatResourceInfo);
	}
	
	private XnatResourceInfo getXnatResourceInfo(MultipartFile file) throws IOException {
		return XnatResourceInfo.builder()
							   .username(getSessionUser().getUsername())
							   .created(new Date())
							   .name(file.getOriginalFilename())
							   .fileSize(file.getSize())
							   .multipartFile(file).build();
	}
			
	private final ImporterService _importerService;
}
