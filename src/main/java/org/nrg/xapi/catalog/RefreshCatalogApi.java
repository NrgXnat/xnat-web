package org.nrg.xapi.catalog;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Arrays;
import java.util.List;

import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.catalog.RefreshCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT catalog refresh Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class RefreshCatalogApi extends AbstractXapiProjectRestController {
    

	@Autowired
    public RefreshCatalogApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final RefreshCatalogService refreshCatalogService) {
        super(userManagementService, roleHolder);
        _refreshCatalogService = refreshCatalogService;
    }

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
	    	_refreshCatalogService.createCatalogRefresh( getSessionUser(),resources,append,checksum, delete,populateStats,options);
	    }
	
	private final RefreshCatalogService _refreshCatalogService;
}
