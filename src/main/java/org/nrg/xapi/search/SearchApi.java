package org.nrg.xapi.search;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.sql.SQLException;
import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.collections.DisplayFieldCollection.DisplayFieldNotFoundException;
import org.nrg.xdat.om.XdatSearch;
import org.nrg.xdat.om.XdatStoredSearch;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.exception.DBPoolException;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.FieldNotFoundException;
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xnat.dto.search.DisplayVersionDto;
import org.nrg.xnat.dto.search.SearchElementDto;
import org.nrg.xnat.dto.search.VersionDto;
import org.nrg.xnat.dto.search.XnatSearchElementDto;
import org.nrg.xnat.services.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT Search Resource Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class SearchApi extends AbstractXapiProjectRestController {
	
    @Autowired
    public SearchApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final SearchService searchService) {
        super(userManagementService, roleHolder);
        _searchService = searchService;
    }
    
    @ApiOperation(value = "Gets the requested search saved", notes = "Returns the  cdat search saved", response = XdatSearch.class, responseContainer = "list")
    @ApiResponses({@ApiResponse(code = 200, message = "nullReturns the requested xdatSearch."),
                   @ApiResponse(code = 404, message = "The requested xdatSearch wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/search/saved", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public ResponseEntity<List<XdatStoredSearch>> getAllSavedSearch() throws Exception {
        log.debug("Controller Api- get xdatSearch saved {}");
        List<XdatStoredSearch> xdatSearchs = _searchService.findAllSavedSearch(getSessionUser());
        if (xdatSearchs == null) {
            throw new NotFoundException("No ProjectAccessRequest with projectId {}" + " was found.");
        }
        return new ResponseEntity<>(xdatSearchs, HttpStatus.OK);
    }
    
    @ApiOperation(value = "Gets the requested search saved", notes = "Returns the  cdat search saved", response = XdatSearch.class, responseContainer = "list")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested xdatSearch."),
                   @ApiResponse(code = 404, message = "The requested xdatSearch wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/search/saved/{searchId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET)
    public ResponseEntity<XdatStoredSearch> getSavedSearchBySearchId(@ApiParam(value = "The ID of the search saved.") @PathVariable  final String searchId) throws Exception {
        log.debug("Controller Api- get xdatSearch saved {}");
        XdatStoredSearch xdatSearchs = _searchService.findSavedSearchBySearchId(getSessionUser(), searchId);
        if (xdatSearchs == null) {
            throw new NotFoundException("No ProjectAccessRequest with projectId {}" + " was found.");
        }
        return new ResponseEntity<>(xdatSearchs, HttpStatus.OK);
    }
    
    
    @ApiOperation(value = "Delete an existing search saved", notes = "Deletes the specified search saved.")
    @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified search saved."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to delete search saved in the specified search saved"),
                   @ApiResponse(code = 404, message = "The specified project or project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/search/saved/{searchId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE)
    public void deleteProject(@ApiParam("The ID of the search saved to be deleted") @PathVariable final String searchId) throws Exception {
        log.debug("Controller Api- Delete search saved {}", searchId);
        _searchService.deleteSavedSearchBySearchId(getSessionUser(), searchId);
    }
    
    @ApiOperation(value = "Update an existing search saved", notes = "Updates the submitted search saved.", response = XnatProjectdata.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated search saved."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to edit search saved in the specified search saved"),
                   @ApiResponse(code = 404, message = "The specified project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/search/saved/{searchId}",
                        consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
                        produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
                        method = PUT)
    public XdatStoredSearch updateProject(@ApiParam("The ID of the search saved to be updated") @PathVariable final String searchId,
    		@ApiParam("The ID of the search saved to be updated") @RequestParam(required = false) final Boolean saveAs,
            @ApiParam("The search saved to be updated.") @RequestBody final XdatStoredSearch xdatStoredSearch) throws Exception {
        log.debug("Controller Api- Update search saved {}",searchId );
        return _searchService.updateStoredSearch(getSessionUser(), xdatStoredSearch, searchId, saveAs);
    }
    
    @ApiOperation(value = "Gets the requested search element", notes = "Returns the  cdat search element", response = XdatSearch.class, responseContainer = "list")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested xdatSearch."),
                   @ApiResponse(code = 404, message = "The requested xdatSearch wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/search/elements", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public ResponseEntity<List<SearchElementDto>> getAllSearchElement(@RequestParam(required = false) final String secured,
    		@RequestParam(required = false) final String readable,
    		@RequestParam(required = false) final String used) throws Exception {
        log.debug("Controller Api- get xdatSearch element saved {}");
        List<SearchElementDto> xdatSearchs = _searchService.findAllSearchElements(getSessionUser(),secured,readable,used);
        if (xdatSearchs == null) {
            throw new NotFoundException("No ProjectAccessRequest with projectId {}" + " was found.");
        }
        return new ResponseEntity<>(xdatSearchs, HttpStatus.OK);
    }
    
    @ApiOperation(value = "Gets the requested search element", notes = "Returns the  cdat search element", response = XdatSearch.class, responseContainer = "list")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested xdatSearch."),
                   @ApiResponse(code = 404, message = "The requested xdatSearch wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/search/element/{elementName}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public ResponseEntity<List<XnatSearchElementDto>> getAllSearchElementByElementName(@ApiParam("The element name of the search element") @PathVariable final String elementName) throws Exception {
        log.debug("Controller Api- get xdatSearch element saved {}");
        List<XnatSearchElementDto> xdatSearchs = _searchService.findSearchElementByElementName(getSessionUser(), elementName);
        if (xdatSearchs == null) {
            throw new NotFoundException("No ProjectAccessRequest with projectId {}" + " was found.");
        }
        return new ResponseEntity<>(xdatSearchs, HttpStatus.OK);
    }
    
    @ApiOperation(value = "Gets the requested search element", notes = "Returns the  cdat search element", response = XdatSearch.class, responseContainer = "list")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested xdatSearch."),
                   @ApiResponse(code = 404, message = "The requested xdatSearch wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/search/element/{elementName}/versions", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public ResponseEntity<DisplayVersionDto> findSearchElementVersionByElementName(@ApiParam("The element name of the search element") @PathVariable final String elementName) throws NotFoundException, XFTInitException, ElementNotFoundException, FieldNotFoundException, SQLException, DBPoolException, DisplayFieldNotFoundException {
        log.debug("Controller Api- get xdatSearch element saved {}");
        DisplayVersionDto xdatSearchs = _searchService.findSearchElementVersionByElementName(getSessionUser(), elementName);
        if (xdatSearchs == null) {
            throw new NotFoundException("No ProjectAccessRequest with projectId {}" + " was found.");
        }
        return new ResponseEntity<>(xdatSearchs, HttpStatus.OK);
    }
    
    @ApiOperation(value = "Gets the requested saved search", notes = "Returns the  xdat saved search", response = XdatSearch.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested xdatSearch."),
    	 		   @ApiResponse(code = 400, message = "The requested projectId or searchId missing."),
                   @ApiResponse(code = 404, message = "The requested xdatSearch wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/searches/{searchId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET)
    public ResponseEntity<XdatStoredSearch> findSavedSearchByProjectIdAndSearchId(@ApiParam(value = "The ID of the search saved.") @PathVariable  final String searchId,
    		@ApiParam(value = "The ID of the project.") @PathVariable final String projectId) throws DataFormatException, NotFoundException {
        log.debug("Controller Api- get xdatSearch element saved {}");
        XdatStoredSearch xdatSearchs = _searchService.findSavedSearchByProjectIdAndSearchId(getSessionUser(), projectId, searchId);
        if (xdatSearchs == null) {
            throw new NotFoundException("No ProjectAccessRequest with projectId {}" + " was found.");
        }
        return new ResponseEntity<>(xdatSearchs, HttpStatus.OK);
    }
    
    
    @ApiOperation(value = "Delete the requested saved search", notes = "Returns the  xdat saved search")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested xdatSearch."),
                   @ApiResponse(code = 404, message = "The requested xdatSearch wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/searches/{searchId}", produces = MediaType.APPLICATION_XML_VALUE, method = DELETE)
    public void  deleteSavedSearchByProjectIdAndSearchId(@ApiParam(value = "The ID of the search saved.") @PathVariable  final String searchId,
    		@ApiParam(value = "The ID of the project.") @PathVariable  final String projectId) throws Exception {
        log.debug("Controller Api- get xdatSearch element saved {}");
         _searchService.deleteSavedSearchByProjectIdAndSearchId(getSessionUser(), projectId, searchId);
    }
    
    private SearchService _searchService;
}

