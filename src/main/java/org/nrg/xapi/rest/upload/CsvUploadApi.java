/**
 * 
 */
package org.nrg.xapi.rest.upload;

import static org.nrg.xdat.security.helpers.AccessLevel.Admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.Project;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.utils.FieldMapping;
import org.nrg.xnat.dto.TemplateData;
import org.nrg.xnat.dto.TemplateDto;
import org.nrg.xnat.dto.ValidationResult;
import org.nrg.xnat.entities.CsvTemplate;
import org.nrg.xnat.services.upload.csv.CsvUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for XNAT CSV Upload API
 *
 */
@Api("XNAT CSV Upload Template API")
@XapiRestController
@RequestMapping(value = "/csv")
@Slf4j
public class CsvUploadApi extends AbstractXapiRestController {

	@Autowired
	public CsvUploadApi(UserManagementServiceI userManagementService, RoleHolder roleHolder, CsvUploadService service) {
		super(userManagementService, roleHolder);
		_uploadService = service;
	}

	@ApiOperation(value = "Retrieves all templates for site.")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns list of templates for site."),
			@ApiResponse(code = 400, message = "Could not retrieve listings.") })
	/**
	 * Returns list of templates for Upload CSV
	 * 
	 * @return List of templates
	 */
	@XapiRequestMapping(value = "/templates", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Admin)
	public ResponseEntity<List<TemplateData>> getAllTemplates() {
		log.info("getAllTemplates called");
		return new ResponseEntity<>(_uploadService.getTemplates(), HttpStatus.OK);
	}

	@ApiOperation(value = "adds a template definition to your site.")
	@ApiResponses({ @ApiResponse(code = 201, message = "Template created successfully."),
			@ApiResponse(code = 400, message = "invalid input, object invalid"),
			@ApiResponse(code = 409, message = "An existing item already exists") })
	/**
	 * Add new template based on JSON passed
	 * 
	 * @param templateDefination JSON containing template object DTO
	 * 
	 */
	@XapiRequestMapping(value = "/templates", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST, restrictTo = Admin)
	public ResponseEntity<Object> addNewTemplate(
			@ApiParam(value = "Template to be added in JSON format.", required = true) @RequestBody final CsvTemplate templateDefination) {
		log.info("add new Template called");
		_uploadService.addTemplate(templateDefination);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@ApiOperation(value = "retrieves all templates for a project")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns list of templates for project."),
			@ApiResponse(code = 400, message = "Not Found"), @ApiResponse(code = 409, message = "Permission denied") })
	/**
	 * Returns list of templates for Upload CSV based on project id passed.
	 * 
	 * @param projectId ID of project whose related templates are to be returned
	 * @return List of templates
	 */
	@XapiRequestMapping(value = "/templates/projects/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Admin)
	public ResponseEntity<List<TemplateData>> getAllTemplatesForProject(
			@ApiParam(value = "Indicates the ID of the project whose templates are to be retrieved.", required = true) @PathVariable("projectId") @Project final String projectId) {
		log.info("getAllTemplatesByProjectId called");
		return new ResponseEntity<>(_uploadService.getTemplatesByProjectId(projectId), HttpStatus.OK);
	}

	@ApiOperation(value = "Gets an individual template by Id.")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns template specified by id."),
			@ApiResponse(code = 400, message = "Not Found"), @ApiResponse(code = 403, message = "Permission denied") })
	/**
	 * Returns template for Upload CSV based on id passed.
	 * 
	 * @param Id of template be returned
	 * @return Template
	 */
	@XapiRequestMapping(value = "/templates/{id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Admin)
	public ResponseEntity<TemplateDto> getTemplateById(
			@ApiParam(value = "Indicates the ID templates that is to be retrieved.", required = true) @PathVariable("id") @Project final String id)
			throws NotFoundException {
		log.info("getTemplateById called");

		TemplateDto dto = _uploadService.getTemplateById(id);

		return new ResponseEntity<>(dto, HttpStatus.OK);

	}

	@ApiOperation(value = "Updates an existing template")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 400, message = "Not Found"),
			@ApiResponse(code = 403, message = "Permission Denied") })
	/**
	 * Modify an existing template found by its id based on JSON passed.
	 * 
	 * @param Id of template be modified
	 */
	@XapiRequestMapping(value = "/templates/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT, restrictTo = Admin)
	public ResponseEntity<Object> updateExistingTemplateById(
			@ApiParam(value = "Indicates the ID of templates that is to be modified.", required = true) @PathVariable("id") @Project final String id,
			@ApiParam(value = "Template to be update in JSON format.", required = true) @RequestBody final CsvTemplate templateDefination) {
		log.info("updateExistingTemplateById called");
		_uploadService.updateTemplate(id, templateDefination);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@ApiOperation(value = "Deletes an existing template")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 400, message = "Not Found"),
			@ApiResponse(code = 403, message = "Permission Denied") })
	/**
	 * Remove an existing template found by its id.
	 * 
	 * @param Id of template be removed
	 */
	@XapiRequestMapping(value = "/templates/{id}", method = RequestMethod.DELETE, restrictTo = Admin)
	public ResponseEntity<Object> deleteExistingTemplateById(
			@ApiParam(value = "Indicates the ID of templates that is to be removed.", required = true) @PathVariable("id") @Project final String id) {
		log.info("deleteExistingTemplateById called");
		_uploadService.deleteTemplate(id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@ApiOperation(value = "Submits CSV data for validation")
	@ApiResponses({ @ApiResponse(code = 200, message = "Validation Results"),
			@ApiResponse(code = 400, message = "Project Not Found"),
			@ApiResponse(code = 403, message = "Permission Denied") })
	/**
	 * Validate passed CSV data
	 * 
	 * @param projectId ID of project whose CSV data is to be validated.
	 * 
	 * @return JSON containing validity status of data and errors found in the file.
	 * 
	 */
	@XapiRequestMapping(value = "/upload/projects/{projectId}/validate/{id}", method = RequestMethod.POST, restrictTo = Admin)
	public ResponseEntity<ValidationResult> validateCsvData(
			@ApiParam(value = "Indicates the ID templates that is to be retrieved.", required = true) @PathVariable("id") @Project final String id, 
			@ApiParam(value = "Indicates the ID of the project whose template is to be validated.", required = true) @PathVariable("projectId") @Project final String projectId,
			@RequestParam("file") MultipartFile file) {
		return new ResponseEntity<>(_uploadService.validateData(id,projectId, file), HttpStatus.OK);
	}

	@ApiOperation(value = "Submits validated CSV data")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 400, message = "Not Found"),
			@ApiResponse(code = 403, message = "Permission Denied") })
	/**
	 * Submits Validated CSV data
	 * 
	 * @param projectId ID of project whose CSV data is to be submitted.
	 */
	@XapiRequestMapping(value = "/upload/projects/{projectId}/submit/{id}", method = RequestMethod.POST, restrictTo = Admin)
	public ResponseEntity<List<Map<String,String>>> submitCsvData(
			@ApiParam(value = "Indicates the ID templates that is to be retrieved.", required = true) @PathVariable("id") @Project final String id, 
			@ApiParam(value = "Indicates the ID of the project whose validated template is to be submitted.", required = true)@PathVariable ("projectId") @Project final String projectId,
			@RequestParam("file") MultipartFile file) {
		return new ResponseEntity<>(_uploadService.submitData(id, projectId, file), HttpStatus.OK);
	}

	@ApiOperation(value = "retrieves all attributes for a root")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns list of attributes for a root."),
			@ApiResponse(code = 400, message = "Not Found"), @ApiResponse(code = 409, message = "Permission denied") })
	/**
	 * Returns list of attributes for root name passed.
	 * 
	 * @param rootName whose related attribute are to be returned
	 * @return List of Attributes
	 */
	@XapiRequestMapping(value = "/templates/root/{rootDataType}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Admin)
	public ResponseEntity<Map<String, ArrayList<Object>>> getAtrributesBasedOnRootDataType(
			@ApiParam(value = "Indicates the name of root whose attributes are to be retrieved.", required = true) @PathVariable("rootDataType") final String rootDataType) {
		log.info("getRoot called");

		Map<String, ArrayList<Object>> attributes = new Hashtable<>();
		String id = "" + Calendar.getInstance().getTimeInMillis();
		FieldMapping fm = new FieldMapping();
		fm.setElementName(rootDataType);
		fm.setTitle("Sample Template hard coded tests");
		fm.setID(id);
		try {
			attributes = _uploadService.getAttributes(fm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(attributes, HttpStatus.OK);
	}

	@ApiOperation(value = "download template for a root")
	@ApiResponses({ @ApiResponse(code = 200, message = "Download file associated with root name passed."),
			@ApiResponse(code = 400, message = "Not Found"), @ApiResponse(code = 409, message = "Permission denied") })
	/**
	 * Download file associated with root name passed.
	 * 
	 * @param id Indicates the id of template to be downloaded.
	 * @return File
	 */
	@XapiRequestMapping(value = "/templates/download/{id}", produces = "text/csv", method = RequestMethod.GET, restrictTo = Admin)
	public ResponseEntity<FileSystemResource> downloadTemplate(
			@ApiParam(value = "Indicates the id of template that is to be downloaded.", required = true) @PathVariable("id") final String id)
			throws IOException {
		log.info("template download called");

		TemplateDto dto = _uploadService.getTemplateById(id);

		File csvFile = _uploadService.downloadTemplate(dto);

		log.info(csvFile.getAbsolutePath());

		return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=" + dto.getXsiType() + ".csv")
				.contentLength(csvFile.length()).contentType(MediaType.parseMediaType("text/csv"))
				.body(new FileSystemResource(csvFile));

	}

	private final CsvUploadService _uploadService;
}
