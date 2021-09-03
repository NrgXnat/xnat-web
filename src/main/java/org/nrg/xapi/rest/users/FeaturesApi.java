package org.nrg.xapi.rest.users;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.SQLException;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.exception.DBPoolException;
import org.nrg.xnat.services.features.FeatureDefinitionService;
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

@Api("XNAT Feature Definition Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class FeaturesApi<T> extends AbstractXapiProjectRestController {
	
	@Autowired
    public FeaturesApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final FeatureDefinitionService<T> featureDefinitionService) {
        super(userManagementService, roleHolder);
        _featureDefinitionService = featureDefinitionService;
    }
	
	@ApiOperation(value = "Gets the Feature Definition", notes = "Returns the  Feature Definition", response = String.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested project."),
    	           @ApiResponse(code = 400, message = "The requested Feature Definition wasn't found."),
                   @ApiResponse(code = 404, message = "The requested Feature Definition wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/services/features", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public T getAllFeatureDefinitions(@ApiParam("The value of type") @RequestParam final String type,
    								  @ApiParam("The value of tags") @RequestParam final String [] tags,
    								  @ApiParam("The value of group") @RequestParam final String group) throws SQLException, DBPoolException {
		log.debug("User {} requested FeatureDefinition", getSessionUser().getUsername());
		return _featureDefinitionService.findAll(getSessionUser(), tags, type, group);
	}
	
	private final FeatureDefinitionService<T> _featureDefinitionService;
}
