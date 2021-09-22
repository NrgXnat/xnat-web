package org.nrg.xapi.rest.users;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotAuthenticatedException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.services.AliasTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT Alias Token Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class AliasTokenApi<T>  extends AbstractXapiProjectRestController {

	@Autowired
    public AliasTokenApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder,final AliasTokenService service) {
        super(userManagementService, roleHolder);
        _service= service;
    }
	
	@SuppressWarnings("unchecked")
	@ApiOperation(value = "Gets the requested  Alias Token", notes = "Returns the  Alias Token with the specified PROJECT ID", response = List.class, responseContainer = "single")
	@ApiResponses({ @ApiResponse(code = 200, message = "Returns the requested Alias Token."),
			@ApiResponse(code = 400, message = "The requested Alias Token wasn't found."),
			@ApiResponse(code = 401, message = "The requested Alias Token not authorized."),
			@ApiResponse(code = 403, message = "The requested Alias Token refuses to authorize it."),
			@ApiResponse(code = 404, message = "The requested Alias Token wasn't found."),
			@ApiResponse(code = 500, message = "An unexpected or unknown error occurred.") })
	@XapiRequestMapping(value = {"/services/tokens/{operation}","/services/tokens/{operation}/user/{username}",
								 "/services/tokens/{operation}/{token}","/services/tokens/{operation}/{token}/{secret}"}, produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
	public T getByOperation(@ApiParam(value = "The operation value.") @PathVariable final String operation,
							@ApiParam(value = "The token value.") @PathVariable(required = false) final String token,
							@ApiParam(value = "The secret value.") @PathVariable(required = false) final String secret,
							@ApiParam(value = "The user name value.") @PathVariable(required = false) final String username) throws DataFormatException, NotFoundException, NotAuthenticatedException, InsufficientPrivilegesException{
		log.debug("User {} requested configs", getSessionUser().getUsername());
		return (T) _service.findAliasTokenByOperationOrUserNameOrTokenOrSecret(getSessionUser(), operation, token, secret, username);
	}
	private final AliasTokenService _service;
}
