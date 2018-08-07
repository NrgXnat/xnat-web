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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.preferences.SiteConfigPreferences;
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
import org.nrg.xnat.helpers.uri.archive.DoiCreationHelper;
import org.nrg.xnat.services.system.DoiCredentialsService;
import org.nrg.xnat.services.system.DoiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.nrg.xdat.security.helpers.AccessLevel.Admin;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@Api(description = "XNAT Data Investigators API")
@XapiRestController
@Slf4j
@RequestMapping(value = "/doi")
public class DoiApi extends AbstractXapiRestController {
    @Autowired
    public DoiApi(final SiteConfigPreferences preferences, final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final DoiService service, final DoiCredentialsService credentialsService) {
        super(userManagementService, roleHolder);
        _preferences = preferences;
        _service = service;
        _credentialsService = credentialsService;
    }

    @ApiOperation(value = "Get list of all DOIs.", notes = "The DOIs function returns a list of all DOIs configured in the XNAT system.", response = Doi.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured DOIs."),
                   @ApiResponse(code = 403, message = "Insufficient privileges to get DOIs for all users."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "identifiers/all", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Admin)
    @ResponseBody
    public ResponseEntity<List<Doi>> getAllDois() {
        return new ResponseEntity<>(_service.getDois(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get list of your DOIs.", notes = "The DOIs function returns a list of all your DOIs configured in the XNAT system.", response = Doi.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of your currently configured DOIs."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "identifiers", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Doi>> getYourDois() {
        final UserI user = getSessionUser();
        return new ResponseEntity<>(_service.getDoisForUsername(user.getUsername()), HttpStatus.OK);
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

    @ApiOperation(value = "Creates a new DOI object from the submitted attributes (or updates an existing one).", notes = "Returns the DOI with the submitted attributes.", response = Doi.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the DOI."),
                   @ApiResponse(code = 403, message = "Insufficient privileges to create or edit the submitted DOI."),
                   @ApiResponse(code = 404, message = "The requested DOI wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "identifier", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<Doi> createOrUpdateDoi(@RequestBody final DoiCreationHelper doiCreationHelper) throws Exception {
        boolean updatingExisting = false;
        UserI user = getSessionUser();

        Doi newDoi = new Doi(doiCreationHelper);
        Doi doiObject = null;
        String issuerPassword = doiCreationHelper.getIssuerPassword();
        String xml = doiCreationHelper.getMetadataXml();
        List<Doi> existingDois = _service.getDoisForObjectAndProjectAndType(doiCreationHelper.getObjectId(), doiCreationHelper.getProjectId(), doiCreationHelper.getXsiType());

        //Create or update internal XNAT representation of DOI
        if(existingDois.size()>0){
            //There is already a DOI for this object and project. Update it as needed (if the user has permissions to).
            doiObject = existingDois.get(0);
            if(Roles.isSiteAdmin(user) || StringUtils.equals(user.getUsername(),doiObject.getXnatUsername())){
                //User has permission to update the DOI
                //Only update fields that are actually included in the submitted data and differ from the original source.
                //Once a DOI has been created, the project, object, and xsiType should not be changed.
//                if (StringUtils.isNotBlank(newDoi.getProjectId()) && !StringUtils.equals(newDoi.getProjectId(), doiObject.getProjectId())) {
//                    doiObject.setProjectId(newDoi.getProjectId());
//                    isDirty = true;
//                }
//                if (StringUtils.isNotBlank(newDoi.getObjectId()) && !StringUtils.equals(newDoi.getObjectId(), doiObject.getObjectId())) {
//                    doiObject.setObjectId(newDoi.getObjectId());
//                    isDirty = true;
//                }
//                if (StringUtils.isNotBlank(newDoi.getXsiType()) && !StringUtils.equals(newDoi.getXsiType(), doiObject.getXsiType())) {
//                    doiObject.setXsiType(newDoi.getXsiType());
//                    isDirty = true;
//                }
                if (StringUtils.isNotBlank(newDoi.getDoi()) && !StringUtils.equals(newDoi.getDoi(), doiObject.getDoi())) {
                    doiObject.setDoi(newDoi.getDoi());
                    updatingExisting = true;
                }
                if (newDoi.getIssuerId()!=doiObject.getIssuerId()) {
                    doiObject.setIssuerId(newDoi.getIssuerId());
                    updatingExisting = true;
                }
                if (StringUtils.isNotBlank(newDoi.getXnatUsername()) && !StringUtils.equals(newDoi.getXnatUsername(), doiObject.getXnatUsername())) {
                    doiObject.setXnatUsername(newDoi.getXnatUsername());
                    updatingExisting = true;
                }
                if (StringUtils.isNotBlank(newDoi.getMetadataXml()) && !StringUtils.equals(newDoi.getMetadataXml(), doiObject.getMetadataXml())) {
                    doiObject.setMetadataXml(newDoi.getMetadataXml());
                    updatingExisting = true;
                }
                if (updatingExisting) {
                    _service.update(doiObject);

                }
            }
            else{
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

        }
        else{
            newDoi.setXnatUsername(getSessionUser().getUsername());
            doiObject = _service.create(newDoi);
        }

        //Create or update actual DOI
        DoiCredentials doiCredentials = _credentialsService.get(doiObject.getIssuerId());
        String metadataUrl = doiCredentials.getIssuerSite()+"/metadata";
        String doiString = doiObject.getDoi();
        String doiCreationUrl = doiCredentials.getIssuerSite()+"/doi/"+doiString;

        HttpPost post = new HttpPost(metadataUrl);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(doiCredentials.getIssuerLogin(), issuerPassword);
        credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), creds);
        post.addHeader("Content-Type","application/xml");
        post.setEntity(new StringEntity(xml));
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).build();
        // send the post request for the metadata
        HttpResponse response = client.execute(post);
        if(response.getStatusLine().getStatusCode()==201) {
            if(updatingExisting){
                //The DOI is already pointing at the XNAT URL for this data, so we only needed to update the metadata.
                return new ResponseEntity<>(doiObject, HttpStatus.OK);
            }
            else {
                HttpPut put = new HttpPut(doiCreationUrl);
                put.addHeader("Content-Type", "application/xml");
                put.setEntity(new StringEntity("doi=" + doiString + "\nurl="+_preferences.getSiteUrl()+"/doi/" + doiObject.getId()));
                CloseableHttpClient client2 = HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).build();
                // send the put request
                HttpResponse response2 = client2.execute(put);
                if (response2.getStatusLine().getStatusCode() == 201) {
                    return new ResponseEntity<>(doiObject, HttpStatus.OK);
                }
            }
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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

    @ApiOperation(value = "Get list of all DOI credentials.", notes = "The DOI credentials function returns a list of all DOI credentials configured in the XNAT system.", response = DoiCredentials.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of the currently configured DOI credentials."),
            @ApiResponse(code = 403, message = "Insufficient privileges to get DOI credentials for all users."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "credentialslist/all", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Admin)
    @ResponseBody
    public ResponseEntity<List<DoiCredentials>> getAllDoiCredentials() {
        final UserI user = getSessionUser();
        return new ResponseEntity<>(_credentialsService.getDoiCredentials(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get list of your DOI credentials.", notes = "The DOI credentials function returns a list of all your DOI credentials configured in the XNAT system.", response = DoiCredentials.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a list of all of your currently configured DOI credentials."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "credentialslist", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<DoiCredentials>> getYourDoiCredentials() {
        final UserI user = getSessionUser();
        return new ResponseEntity<>(_credentialsService.getDoiCredentialsForUsername(user.getUsername()), HttpStatus.OK);
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

    @ApiOperation(value = "Gets the metadata for the requested DOI.", notes = "Returns the metadata for a given DOI.", response = String.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested DOI metadata."),
            @ApiResponse(code = 404, message = "The requested DOI wasn't found."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "metadata/{id}", produces = MediaType.TEXT_XML_VALUE, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getDoiMetadata(@PathVariable("id") final long id) throws NotFoundException {
        final Doi doiObject = _service.get(id);
        if (doiObject == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String doiMetadata = doiObject.getMetadataXml();
        try {
            String doiString = doiObject.getDoi();
            if(!StringUtils.startsWith(doiString,"10.5072/")) {
                //DOI is not from the test account

                String doiCreationUrl = "https://doi.org/"+doiString;
                HttpGet get = new HttpGet(doiCreationUrl);
                get.addHeader("Accept", "application/vnd.datacite.datacite+xml; q=0.5");
                CloseableHttpClient client = HttpClientBuilder.create().build();
                try {
                    // send the get request
                    CloseableHttpResponse response = client.execute(get);
                    try {
                        if (response.getStatusLine().getStatusCode() == 200) {
                            doiMetadata = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
                        }
                    } finally {
                        response.close();
                    }
                } finally {
                    client.close();
                }
            }
        }
        catch(Exception e){
            log.error("Failed to get DOI metadata.",e);
        }

        return new ResponseEntity<>(doiMetadata, HttpStatus.OK);
    }

    @ApiOperation(value = "Gets a random String that could be used for creating a DOI.", notes = "Returns a new random text String.", response = String.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a new random String."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "random", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getRandomIdentifier() throws NotFoundException {
        String possibleCharacters = "0123456789ABCDEFGHJKMNPQRSTVWXYZ";
        Random rand = new Random();
        String resultingIdentifier = "";
        resultingIdentifier+=possibleCharacters.charAt(rand.nextInt(possibleCharacters.length()));
        resultingIdentifier+=possibleCharacters.charAt(rand.nextInt(possibleCharacters.length()));
        resultingIdentifier+=possibleCharacters.charAt(rand.nextInt(possibleCharacters.length()));
        resultingIdentifier+=possibleCharacters.charAt(rand.nextInt(possibleCharacters.length()));
        resultingIdentifier+="-";
        resultingIdentifier+=possibleCharacters.charAt(rand.nextInt(possibleCharacters.length()));
        resultingIdentifier+=possibleCharacters.charAt(rand.nextInt(possibleCharacters.length()));
        resultingIdentifier+=possibleCharacters.charAt(rand.nextInt(possibleCharacters.length()));
        resultingIdentifier+=possibleCharacters.charAt(rand.nextInt(possibleCharacters.length()));

        return new ResponseEntity<>(resultingIdentifier, HttpStatus.OK);
    }

    @ApiOperation(value = "Gets a random String that could be used for creating a DOI.", notes = "Returns a new random DOI that has not yet been used on this XNAT with this prefix. You should not include a slash in your prefix.", response = String.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns a new random String."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "random/{prefix}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getRandomIdentifier(@PathVariable("prefix") final String prefix) throws NotFoundException {
        String possibleCharacters = "0123456789ABCDEFGHJKMNPQRSTVWXYZ"; //Douglas Crockford's Base32
        Random rand = new Random();
        boolean hasFoundUnusedDoi = false;
        String resultingIdentifier = "";
        while (!hasFoundUnusedDoi) {
            resultingIdentifier = prefix;
            resultingIdentifier += "/";
            resultingIdentifier += possibleCharacters.charAt(rand.nextInt(possibleCharacters.length()));
            resultingIdentifier += possibleCharacters.charAt(rand.nextInt(possibleCharacters.length()));
            resultingIdentifier += possibleCharacters.charAt(rand.nextInt(possibleCharacters.length()));
            resultingIdentifier += possibleCharacters.charAt(rand.nextInt(possibleCharacters.length()));
            resultingIdentifier += "-";
            resultingIdentifier += possibleCharacters.charAt(rand.nextInt(possibleCharacters.length()));
            resultingIdentifier += possibleCharacters.charAt(rand.nextInt(possibleCharacters.length()));
            resultingIdentifier += possibleCharacters.charAt(rand.nextInt(possibleCharacters.length()));
            resultingIdentifier += possibleCharacters.charAt(rand.nextInt(possibleCharacters.length()));
            List<Doi> existingDoisWithThatDoiString = _service.getDoisForDoiString(resultingIdentifier);
            if(existingDoisWithThatDoiString==null || existingDoisWithThatDoiString.size()==0){
                hasFoundUnusedDoi = true;
            }
        }
        return new ResponseEntity<>(resultingIdentifier, HttpStatus.OK);
    }

    private final SiteConfigPreferences _preferences;
    private final DoiService _service;
    private final DoiCredentialsService _credentialsService;
}
