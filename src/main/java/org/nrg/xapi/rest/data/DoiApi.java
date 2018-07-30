/*
 * web: org.nrg.xapi.rest.data.InvestigatorsApi
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xapi.rest.data;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.XFTItem;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.exception.XftItemException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.entities.Doi;
import org.nrg.xnat.entities.DoiCredentials;
import org.nrg.xnat.services.system.DoiCredentialsService;
import org.nrg.xnat.services.system.DoiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@Api(description = "XNAT Data Investigators API")
@XapiRestController
@RequestMapping(value = "/doi")
public class DoiApi extends AbstractXapiRestController {
    @Autowired
    public DoiApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final DoiService service, final DoiCredentialsService credentialsService) {
        super(userManagementService, roleHolder);
        _service = service;
        _credentialsService = credentialsService;
    }

    @ApiOperation(value = "Get list of DOIs.", notes = "The DOIs function returns a list of all DOIs configured in the XNAT system.", response = Doi.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured DOIs."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "identifier", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Doi>> getDois() {
        return new ResponseEntity<>(_service.getDois(), HttpStatus.OK);
    }

    @ApiOperation(value = "Gets the requested DOI.", notes = "Returns the DOI object for a given DOI.", response = Doi.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested DOI object."),
                   @ApiResponse(code = 404, message = "The requested DOI wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "identifier/{id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Doi> getDoi(@PathVariable("id") final long id) throws NotFoundException {
        final Doi doiObject = _service.get(id);
        if (doiObject == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(doiObject, HttpStatus.OK);
    }

    @ApiOperation(value = "Creates a new DOI object from the submitted attributes.", notes = "Returns the newly created DOI with the submitted attributes.", response = Doi.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created DOI."),
                   @ApiResponse(code = 403, message = "Insufficient privileges to create the submitted DOI."),
                   @ApiResponse(code = 404, message = "The requested DOI wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "identifier", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Doi> createDoi(@RequestBody final Doi doi) throws Exception {
        doi.setXnatUsername(getSessionUser().getUsername());
        Doi created = _service.create(doi);
        return new ResponseEntity<>(created, HttpStatus.OK);
    }

    @ApiOperation(value = "Updates the requested DOI from the submitted attributes.", notes = "Returns the updated DOI.", response = Doi.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated DOI."),
                   @ApiResponse(code = 304, message = "The requested DOI is the same as the submitted DOI."),
                   @ApiResponse(code = 403, message = "Insufficient privileges to edit the requested DOI."),
                   @ApiResponse(code = 404, message = "The requested DOI wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "identifier/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Doi> updateDoi(@PathVariable("id") final int id, @RequestBody final Doi doiObject) throws Exception {
        final UserI user = getSessionUser();

        final Doi existing = _service.get(doiObject.getId());
        if (existing == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(!Roles.isSiteAdmin(user) && existing!=null && StringUtils.isNotBlank(existing.getXnatUsername())){
            if(!StringUtils.equals(existing.getXnatUsername(),user.getUsername()) || !StringUtils.equals(doiObject.getXnatUsername(),user.getUsername())){
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        boolean isDirty = false;
        // Only update fields that are actually included in the submitted data and differ from the original source.
        if (StringUtils.isNotBlank(doiObject.getProjectId()) && !StringUtils.equals(doiObject.getProjectId(), existing.getProjectId())) {
            existing.setProjectId(doiObject.getProjectId());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(doiObject.getObjectId()) && !StringUtils.equals(doiObject.getObjectId(), existing.getObjectId())) {
            existing.setObjectId(doiObject.getObjectId());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(doiObject.getXsiType()) && !StringUtils.equals(doiObject.getXsiType(), existing.getXsiType())) {
            existing.setXsiType(doiObject.getXsiType());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(doiObject.getDoiUrl()) && !StringUtils.equals(doiObject.getDoiUrl(), existing.getDoiUrl())) {
            existing.setDoiUrl(doiObject.getDoiUrl());
            isDirty = true;
        }
        if (doiObject.getIssuerId()!=existing.getIssuerId()) {
            existing.setIssuerId(doiObject.getIssuerId());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(doiObject.getXnatUsername()) && !StringUtils.equals(doiObject.getXnatUsername(), existing.getXnatUsername())) {
            existing.setXnatUsername(doiObject.getXnatUsername());
            isDirty = true;
        }
        if (isDirty) {
            _service.update(existing);
            return new ResponseEntity<>(existing, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @ApiOperation(value = "Deletes the requested DOI.", notes = "Returns true if the requested DOI was successfully deleted. Returns false otherwise.", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns true to indicate the requested DOI was successfully deleted."),
            @ApiResponse(code = 403, message = "The user doesn't have permission to delete DOI."),
            @ApiResponse(code = 404, message = "The requested DOI wasn't found."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "identifier/{id}", produces = APPLICATION_JSON_VALUE, method = DELETE)
    @ResponseBody
    public ResponseEntity<Boolean> deleteDoi(@PathVariable("id") final int id) throws org.nrg.xapi.exceptions.NotFoundException, InsufficientPrivilegesException, XftItemException, NotFoundException {
        final UserI user = getSessionUser();

        _service.deleteDoi(id, user);
        return ResponseEntity.ok(true);
    }

    @ApiOperation(value = "Get list of DOI credentials.", notes = "The DOI credentials function returns a list of all DOI credentials configured in the XNAT system.", response = DoiCredentials.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured DOI credentials."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "credentials", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<DoiCredentials>> getDoiCredentialsList() {
        final UserI user = getSessionUser();
        if(Roles.isSiteAdmin(user)){
            return new ResponseEntity<>(_credentialsService.getDoiCredentials(), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(_credentialsService.getDoiCredentialsForUsername(user.getUsername()), HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Gets the requested DOI credentials.", notes = "Returns the DOI credentials object for a given DOI.", response = DoiCredentials.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested DOI credentials object."),
            @ApiResponse(code = 404, message = "The requested DOI credentials object wasn't found."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "credentials/{credentialsId}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<DoiCredentials> getDoiCredentials(@PathVariable("credentialsId") final long credentialsId) throws NotFoundException {
        final UserI user = getSessionUser();
        final DoiCredentials doiCredentialsObject = _credentialsService.get(credentialsId);
        if (doiCredentialsObject == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(!Roles.isSiteAdmin(user) && !StringUtils.equals(doiCredentialsObject.getXnatUsername(),user.getUsername())){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(doiCredentialsObject, HttpStatus.OK);
    }

    @ApiOperation(value = "Creates a new DOI credentials object from the submitted attributes.", notes = "Returns the newly created DOI credentials with the submitted attributes. Currently only DataCite format credentials are supported.", response = DoiCredentials.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created DOI credentials."),
            @ApiResponse(code = 403, message = "Insufficient privileges to create the submitted DOI credentials."),
            @ApiResponse(code = 404, message = "The requested DOI credentials weren't found."),
            @ApiResponse(code = 422, message = "Cannot currently process non DataCite credentials. The issuerDoiFormat must be DataCite."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "credentials", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<DoiCredentials> createCredentials(@RequestBody final DoiCredentials credentials) throws Exception {
        final UserI user = getSessionUser();
        String sessionUsername = user.getUsername();
        if(!Roles.isSiteAdmin(user) && credentials!=null && StringUtils.isNotBlank(credentials.getXnatUsername())){
            if(!StringUtils.equals(credentials.getXnatUsername(),sessionUsername)){
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        if(!StringUtils.equalsIgnoreCase(credentials.getIssuerDoiFormat(),"DataCite")){
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        credentials.setXnatUsername(sessionUsername);
        DoiCredentials created = _credentialsService.create(credentials);
        return new ResponseEntity<>(created, HttpStatus.OK);
    }

    @ApiOperation(value = "Updates the requested DOI credentials from the submitted attributes.", notes = "Returns the updated DOI credentials. Currently only DataCite format credentials are supported.", response = DoiCredentials.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated DOI credentials."),
            @ApiResponse(code = 304, message = "The requested DOI is the same as the submitted DOI credentials."),
            @ApiResponse(code = 403, message = "Insufficient privileges to edit the requested DOI credentials."),
            @ApiResponse(code = 404, message = "The requested DOI credentials weren't found."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "credentials/{credentialsId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<DoiCredentials> updateCredentials(@PathVariable("credentialsId") final int credentialsId, @RequestBody final DoiCredentials doiCredentialsObject) throws Exception {
        final UserI user = getSessionUser();

        final DoiCredentials existing = _credentialsService.get(doiCredentialsObject.getId());

        if(!Roles.isSiteAdmin(user) && existing!=null && StringUtils.isNotBlank(existing.getXnatUsername())){
            if(!StringUtils.equals(existing.getXnatUsername(),user.getUsername()) || !StringUtils.equals(doiCredentialsObject.getXnatUsername(),user.getUsername())){
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }

        if(!StringUtils.equalsIgnoreCase(doiCredentialsObject.getIssuerDoiFormat(),"DataCite")){
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (existing == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        boolean isDirty = false;
        // Only update fields that are actually included in the submitted data and differ from the original source.
        if (StringUtils.isNotBlank(doiCredentialsObject.getXnatUsername()) && !StringUtils.equals(doiCredentialsObject.getXnatUsername(), existing.getXnatUsername())) {
            existing.setXnatUsername(doiCredentialsObject.getXnatUsername());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(doiCredentialsObject.getIssuerLabel()) && !StringUtils.equals(doiCredentialsObject.getIssuerLabel(), existing.getIssuerLabel())) {
            existing.setIssuerLabel(doiCredentialsObject.getIssuerLabel());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(doiCredentialsObject.getIssuerDescription()) && !StringUtils.equals(doiCredentialsObject.getIssuerDescription(), existing.getIssuerDescription())) {
            existing.setIssuerDescription(doiCredentialsObject.getIssuerDescription());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(doiCredentialsObject.getIssuerLogin()) && !StringUtils.equals(doiCredentialsObject.getIssuerLogin(), existing.getIssuerLogin())) {
            existing.setIssuerLogin(doiCredentialsObject.getIssuerLogin());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(doiCredentialsObject.getIssuerSite()) && !StringUtils.equals(doiCredentialsObject.getIssuerSite(), existing.getIssuerSite())) {
            existing.setIssuerSite(doiCredentialsObject.getIssuerSite());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(doiCredentialsObject.getIssuerDoiFormat()) && !StringUtils.equals(doiCredentialsObject.getIssuerDoiFormat(), existing.getIssuerDoiFormat())) {
            existing.setIssuerDoiFormat(doiCredentialsObject.getIssuerDoiFormat());
            isDirty = true;
        }
        if (isDirty) {
            _credentialsService.update(existing);
            return new ResponseEntity<>(existing, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @ApiOperation(value = "Deletes the requested DOI credentials.", notes = "Returns true if the requested DOI credentials object was successfully deleted. Returns false otherwise.", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns true to indicate the requested DOI credentials object was successfully deleted."),
            @ApiResponse(code = 403, message = "The user doesn't have permission to delete DOI credentials."),
            @ApiResponse(code = 404, message = "The requested DOI credentials object wasn't found."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "credentials/{credentialsId}", produces = APPLICATION_JSON_VALUE, method = DELETE)
    @ResponseBody
    public ResponseEntity<Boolean> deleteCredentials(@PathVariable("credentialsId") final int credentialsId) throws org.nrg.xapi.exceptions.NotFoundException, InsufficientPrivilegesException, XftItemException, NotFoundException {
        final UserI user = getSessionUser();

        _credentialsService.deleteCredentials(credentialsId, user);
        return ResponseEntity.ok(true);
    }

    private final DoiService _service;
    private final DoiCredentialsService _credentialsService;
}
