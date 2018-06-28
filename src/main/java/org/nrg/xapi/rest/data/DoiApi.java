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
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.XFTItem;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.entities.Doi;
import org.nrg.xnat.services.system.DoiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(description = "XNAT Data Investigators API")
@XapiRestController
@RequestMapping(value = "/doi")
public class DoiApi extends AbstractXapiRestController {
    @Autowired
    public DoiApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final DoiService service) {
        super(userManagementService, roleHolder);
        _service = service;
    }

    @ApiOperation(value = "Get list of DOIs.", notes = "The DOIs function returns a list of all DOIs configured in the XNAT system.", response = Doi.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured DOIs."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Doi>> getDois() {
        return new ResponseEntity<>(_service.getDois(), HttpStatus.OK);
    }

    @ApiOperation(value = "Gets the requested DOI.", notes = "Returns the DOI object for a given DOI.", response = Doi.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested DOI object."),
                   @ApiResponse(code = 404, message = "The requested DOI wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "{doi}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Doi> getDoi(@PathVariable("doi") final long doi) throws NotFoundException {
        final Doi doiObject = _service.get(doi);
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
    @XapiRequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Doi> createDoi(@RequestBody final Doi doi) throws Exception {
        Doi created = _service.create(doi);
        return new ResponseEntity<>(created, HttpStatus.OK);
    }

    @ApiOperation(value = "Updates the requested DOI from the submitted attributes.", notes = "Returns the updated DOI.", response = Doi.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated DOI."),
                   @ApiResponse(code = 304, message = "The requested DOI is the same as the submitted DOI."),
                   @ApiResponse(code = 403, message = "Insufficient privileges to edit the requested DOI."),
                   @ApiResponse(code = 404, message = "The requested DOI wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "{doi}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Doi> updateDoi(@PathVariable("doi") final int doi, @RequestBody final Doi doiObject) throws Exception {
        final UserI user = getSessionUser();

        final Doi existing = _service.get(doiObject.getId());
        if (existing == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
        if (StringUtils.isNotBlank(doiObject.getDoi()) && !StringUtils.equals(doiObject.getDoi(), existing.getDoi())) {
            existing.setDoi(doiObject.getDoi());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(doiObject.getDescription()) && !StringUtils.equals(doiObject.getDescription(), existing.getDescription())) {
            existing.setDescription(doiObject.getDescription());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(doiObject.getKeywords()) && !StringUtils.equals(doiObject.getKeywords(), existing.getKeywords())) {
            existing.setKeywords(doiObject.getKeywords());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(doiObject.getContactName()) && !StringUtils.equals(doiObject.getContactName(), existing.getContactName())) {
            existing.setContactName(doiObject.getContactName());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(doiObject.getContactEmail()) && !StringUtils.equals(doiObject.getContactEmail(), existing.getContactEmail())) {
            existing.setContactEmail(doiObject.getContactEmail());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(doiObject.getDataAvailability()) && !StringUtils.equals(doiObject.getDataAvailability(), existing.getDataAvailability())) {
            existing.setDataAvailability(doiObject.getDataAvailability());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(doiObject.getDataUseTerms()) && !StringUtils.equals(doiObject.getDataUseTerms(), existing.getDataUseTerms())) {
            existing.setDataUseTerms(doiObject.getDataUseTerms());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(doiObject.getRelatedPublications()) && !StringUtils.equals(doiObject.getRelatedPublications(), existing.getRelatedPublications())) {
            existing.setRelatedPublications(doiObject.getRelatedPublications());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(doiObject.getLinks()) && !StringUtils.equals(doiObject.getLinks(), existing.getLinks())) {
            existing.setLinks(doiObject.getLinks());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(doiObject.getNotes()) && !StringUtils.equals(doiObject.getNotes(), existing.getNotes())) {
            existing.setNotes(doiObject.getNotes());
            isDirty = true;
        }
        if (isDirty) {
            _service.update(existing);
            return new ResponseEntity<>(existing, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    private static final Logger _log = LoggerFactory.getLogger(DoiApi.class);

    private final DoiService _service;
}
