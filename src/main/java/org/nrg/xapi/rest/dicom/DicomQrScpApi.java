package org.nrg.xapi.rest.dicom;

import groovy.util.logging.Slf4j;
import io.swagger.annotations.*;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.nrg.xdat.security.helpers.AccessLevel.Authenticated;
import static org.nrg.xdat.security.helpers.AccessLevel.Admin;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Api("XNAT DICOM Q/R SCP management API")
@XapiRestController
@RequestMapping("dicom-qr-scp")
@Slf4j
public class DicomQrScpApi extends AbstractXapiRestController {
    public DicomQrScpApi(
            final UserManagementServiceI userManagementService,
            final RoleHolder roleHolder,
            final SiteConfigPreferences preferences
    ) {
        super(userManagementService, roleHolder);
        _preferences = preferences;
    }

    @ApiOperation(value = "Indicates whether the DIMSE C-FIND/C-GET SCP is enabled", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the status of the C-FIND/C-GET SCP."),
            @ApiResponse(code = 403, message = "Insufficient permissions to access the C-FIND/C-GET SCP settings."),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "site/allow", produces = APPLICATION_JSON_VALUE, method = GET, restrictTo = Authenticated)
    public boolean getAllowExternalDicomQueries() {
        return _preferences.getAllowExternalDicomQueries();
    }

    @ApiOperation(value = "Sets whether DIMSE C-FIND/C-GET SCP is enabled.")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully set DICOM C-FIND/C-GET SCP enabled status."),
            @ApiResponse(code = 403, message = "Not authorized to alter site DIMSE C-FIND/C-GET SCP settings."),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "site/allow/{allow}", method = PUT, restrictTo = Admin)
    public void setAllowExternalDicomQueries(final @PathVariable Boolean allow) {
        _preferences.setAllowExternalDicomQueries(allow);
    }

    @ApiOperation(value = "Indicates whether DIMSE C-FIND/C-GET SCP is enabled in all projects by default", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved whether C-FIND/C-GET SCP is enabled by default."),
            @ApiResponse(code = 403, message = "Insufficient permissions to access the C-FIND/C-GET SCP settings."),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "site/enableSitewide", produces = APPLICATION_JSON_VALUE, method = GET, restrictTo = Authenticated)
    public boolean getEnableSitewideDicomQueries() { return _preferences.getEnableSitewideDicomQueries(); }

    @ApiOperation(value = "Sets whether DIMSE C-FIND/C-GET SCP is enabled in all projects by default.")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully set DICOM C-FIND/C-GET SCP project default status."),
            @ApiResponse(code = 403, message = "Not authorized to alter site DIMSE C-FIND/C-GET SCP settings."),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "site/enableSitewide/{enable}", method = PUT, restrictTo = Admin)
    public void setEnableSitewideDicomQueries(final @PathVariable Boolean enable) {
        _preferences.setEnableSitewideDicomQueries(enable);
    }

    final SiteConfigPreferences _preferences;
}
