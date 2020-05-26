/**
 * 
 */
package org.nrg.xnat.services.upload.csv.rest;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.upload.csv.CsvUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller for XNAT CSV Upload API
 *
 */
@Api("XNAT CSV Upload Template API")
@XapiRestController
@RequestMapping(value = "/csv")
public class CsvUploadApi extends AbstractXapiRestController {

	@Autowired
	protected CsvUploadApi(UserManagementServiceI userManagementService, RoleHolder roleHolder,
			CsvUploadService service) {
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
	@GetMapping(value = "/templates")
	public ResponseEntity<String> getAllTemplates() {
		return new ResponseEntity<>(_uploadService.listTemplates(), HttpStatus.OK);
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
	@PostMapping(value = "/templates")
	public ResponseEntity<String> addNewTemplate(String templateDefination) {
		return new ResponseEntity<>(_uploadService.addTemplate(templateDefination), HttpStatus.OK);
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
	@GetMapping(value = "/templates/projects/{projectId}")
	public ResponseEntity<String> getAllTemplatesForProject(@PathVariable String projectId) {
		return new ResponseEntity<>(_uploadService.listProjectTemplates(projectId), HttpStatus.OK);
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
	@GetMapping(value = "/templates/{id}")
	public ResponseEntity<String> getTemplateById(@PathVariable String id) {
		return new ResponseEntity<>(_uploadService.getSingleTemplate(id), HttpStatus.OK);
	}

	@ApiOperation(value = "Updates an existing template")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 400, message = "Not Found"),
			@ApiResponse(code = 403, message = "Permission Denied") })
	/**
	 * Modify an existing template found by its id based on JSON passed.
	 * 
	 * @param Id of template be modified
	 */
	@PutMapping(value = "/templates/{id}")
	public ResponseEntity<String> updateExistingTemplateById(@PathVariable String id) {
		return new ResponseEntity<>(_uploadService.updateTemplate(id), HttpStatus.OK);
	}

	@ApiOperation(value = "Deletes an existing template")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 400, message = "Not Found"),
			@ApiResponse(code = 403, message = "Permission Denied") })
	/**
	 * Remove an existing template found by its id.
	 * 
	 * @param Id of template be removed
	 */
	@DeleteMapping(value = "/templates/{id}")
	public ResponseEntity<String> deleteExistingTemplateById(@PathVariable String id) {
		return new ResponseEntity<>(_uploadService.deleteTemplate(id), HttpStatus.OK);
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
	@PostMapping(value = "/upload/projects/{projectId}/validate")
	public ResponseEntity<String> validateCsvData(@PathVariable String projectId) {
		return new ResponseEntity<>(_uploadService.validateData(projectId), HttpStatus.OK);
	}

	@ApiOperation(value = "Submits validated CSV data")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 400, message = "Not Found"),
			@ApiResponse(code = 403, message = "Permission Denied") })
	/**
	 * Submits Validated CSV data
	 * 
	 * @param projectId ID of project whose CSV data is to be submitted.
	 */
	@PostMapping(value = "/upload/projects/{projectId}/submit")
	public ResponseEntity<String> submitCsvData(@PathVariable String projectId) {
		return new ResponseEntity<>(_uploadService.submitData(projectId), HttpStatus.OK);
	}

	private final CsvUploadService _uploadService;
}
