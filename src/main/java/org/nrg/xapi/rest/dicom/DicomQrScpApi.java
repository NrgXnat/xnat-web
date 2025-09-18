package org.nrg.xapi.rest.dicom;

import groovy.util.logging.Slf4j;
import io.swagger.annotations.*;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.entities.ProjectDimseConfig;
import org.nrg.xnat.exceptions.InvalidScpAvailabilityException;
import org.nrg.xnat.services.dicom.ProjectDimseConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;

import static org.nrg.xdat.security.helpers.AccessLevel.Authenticated;
import static org.nrg.xdat.security.helpers.AccessLevel.Admin;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Api("XNAT DICOM DIMSE QR SCP management API")
@XapiRestController
@RequestMapping("dicom-qr-scp")
@Slf4j
public class DicomQrScpApi extends AbstractXapiRestController {
    public DicomQrScpApi(
            final UserManagementServiceI userManagementService,
            final RoleHolder roleHolder,
            final SiteConfigPreferences preferences,
            final ProjectDimseConfigService projectDimseConfigService
            ) {
        super(userManagementService, roleHolder);
        _preferences = preferences;
        _projectDimseConfigService = projectDimseConfigService;
    }

    @ApiOperation(value = "Indicates whether the DIMSE QR SCP is enabled", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved the status of the DIMSE QR SCP."),
            @ApiResponse(code = 403, message = "Insufficient permissions to access the DIMSE QR SCP settings."),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "site/allow", produces = APPLICATION_JSON_VALUE, method = GET, restrictTo = Authenticated)
    public boolean getAllowExternalDicomQueries() {
        return _preferences.getAllowExternalDicomQueries();
    }

    @ApiOperation(value = "Sets whether DIMSE C-FIND/C-GET SCP is enabled.")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully set DIMSE QR SCP enabled status."),
            @ApiResponse(code = 403, message = "Not authorized to alter site DIMSE QR SCP settings."),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "site/allow/{allow}", method = PUT, restrictTo = Admin)
    public void setAllowExternalDicomQueries(final @PathVariable Boolean allow) {
        _preferences.setAllowExternalDicomQueries(allow);
    }

    @ApiOperation(value = "Indicates whether DIMSE QR SCP is enabled in all projects by default", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved whether DIMSE QR SCP is enabled by default."),
            @ApiResponse(code = 403, message = "Insufficient permissions to access the DIMSE QR SCP settings."),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "site/enableSitewide", produces = APPLICATION_JSON_VALUE, method = GET, restrictTo = Authenticated)
    public boolean getEnableSitewideDicomQueries() { return _preferences.getEnableSitewideDicomQueries(); }

    @ApiOperation(value = "Sets whether DIMSE QR SCP is enabled in all projects by default.")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully set DIMSE QR SCP project default status."),
            @ApiResponse(code = 403, message = "Not authorized to alter site DIMSE QR SCP settings."),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "site/enableSitewide/{enable}", method = PUT, restrictTo = Admin)
    public void setEnableSitewideDicomQueries(final @PathVariable Boolean enable) {
        _preferences.setEnableSitewideDicomQueries(enable);
    }

    @ApiOperation(value = "Indicates whether project data is available for DIMSE QR requests", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully retrieved project availability for DIMSE QR requests."),
            @ApiResponse(code = 403, message = "Insufficient permissions to access the project DIMSE QR SCP settings."),
            @ApiResponse(code = 500, message = "Unexpected error")
    })
    @XapiRequestMapping(value = "project/{projectId}/availability")
    public Boolean getProjectDimseQrAvailable(final @PathVariable String projectId) throws NotFoundException {
        final ProjectDimseConfigService.Availability available = _projectDimseConfigService.getProjectQrAvailability(projectId);
        return null == available ? null : available.asBoolean();
    }

    @ApiOperation(value = "Sets whether project data is available for DIMSE QR requests.")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully set DIMSE QR SCP project availability."),
            @ApiResponse(code = 400, message = "Invalid value for DIMSE QR SCP project availability."),
            @ApiResponse(code = 403, message = "Not authorized to alter site DIMSE QR SCP settings."),
            @ApiResponse(code = 500, message = "Unexpected error")
    })
    @XapiRequestMapping(value = "project/{projectId}/availability/{available}", method = PUT, restrictTo = Admin)
    public void setProjectDimseQrAvailable(
            final @PathVariable String projectId, final @PathVariable String available
    ) throws NotFoundException, InvalidScpAvailabilityException {
        _projectDimseConfigService.updateProjectQrAvailability(projectId, available);
    }

    @ApiOperation(value = "Gets IDs for all projects for which DIMSE QR access is enabled.")
    @XapiRequestMapping(value = "projects/available", method = PUT, restrictTo = Authenticated)
    public Collection<String> getAvailableProjects() {
        return _projectDimseConfigService.getQrAvailableProjects(getSessionUser());
    }

    @ApiOperation(value = "Deletes DIMSE QR project configuration for the named project.")
    @ApiResponses({@ApiResponse(code = 200, message = "Successfully set DIMSE QR SCP project availability."),
            @ApiResponse(code = 404, message = "No DIMSE QR SCP configuration was found for the named project."),
            @ApiResponse(code = 403, message = "Not authorized to alter site DIMSE QR SCP settings."),
            @ApiResponse(code = 500, message = "Unexpected error")
    })
    @XapiRequestMapping(value = "project/{projectId}/config", method = DELETE, restrictTo = Admin)
    public void deleteProjectDimseQrConfig(final @PathVariable String projectId) throws NotFoundException {
        _projectDimseConfigService.deleteProjectDimseConfig(projectId);
    }

    final SiteConfigPreferences _preferences;
    final ProjectDimseConfigService _projectDimseConfigService;
}
