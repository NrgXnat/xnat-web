package org.nrg.xapi.search;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.sql.SQLException;
import java.util.List;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.collections.DisplayFieldCollection.DisplayFieldNotFoundException;
import org.nrg.xdat.om.XdatSearch;
import org.nrg.xdat.om.XdatStoredSearch;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.ActionNameAbsent;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.JustificationAbsent;
import org.nrg.xnat.dto.search.DisplayVersionDto;
import org.nrg.xnat.dto.search.SearchElementDto;
import org.nrg.xnat.dto.search.XnatSearchElementDto;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
    
    @ApiOperation(value = "Gets the requested search saved", notes = "Returns the  cdat search saved", response = XdatStoredSearch.class, responseContainer = "list")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested XdatStoredSearch."),
                   @ApiResponse(code = 404, message = "The requested XdatStoredSearch wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/search/saved", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<XdatStoredSearch> getAllSavedSearches(@ApiParam(value = "The username seach value.") @RequestParam(name= "username", required = false )  final String username,
    												  @ApiParam(value = "The getAllBundles seach value.") @RequestParam(name= "allBundles", required = false )  final String allBundles,
    												  @ApiParam(value = "The includeTag seach value.") @RequestParam(name= "includeTag", required = false )  final String includeTag) throws NotFoundException {
        log.debug("User {} requested XdatStoredSearch ", getSessionUser().getUsername());
        return _searchService.findAllSavedSearch(getSessionUser(), username, allBundles,includeTag );
    }

    @ApiOperation(value = "Gets the requested search saved", notes = "Returns the  cdat search saved", response = XdatSearch.class, responseContainer = "list")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested XdatStoredSearch."),
    			   @ApiResponse(code = 403, message = "The user doesn't have permission to create XdatStoredSearch"),
                   @ApiResponse(code = 404, message = "The requested XdatStoredSearch wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/search/saved/{searchId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET)
    public XdatStoredSearch getSavedSearchBySearchId(@ApiParam(value = "The ID of the search saved.") @PathVariable  final String searchId,
    												 @ApiParam(value = "The dv seach value.") @RequestParam(name= "dv", required = false )  final String dv,
    												 @ApiParam(value = "The project seach value.") @RequestParam(name= "project", required = false )  final String project) throws NotFoundException, InsufficientPrivilegesException {
    	log.debug("User {} requested search with ID {}", getSessionUser().getUsername(), searchId);
    	return _searchService.findSavedSearchBySearchId(getSessionUser(), searchId,dv, project).orElseThrow(() -> new NotFoundException(XdatStoredSearch.SCHEMA_ELEMENT_NAME, searchId));
    }
    
    @ApiOperation(value = "Gets the requested search element", notes = "Returns the  cdat search element", response = XdatSearch.class, responseContainer = "list")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested search elements."),
                   @ApiResponse(code = 404, message = "The requested search elements wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/search/elements", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<SearchElementDto> getAllSearchElements(@RequestParam(required = false) final String secured,@RequestParam(required = false) final String readable,
    												   @RequestParam(required = false) final String used) throws NotFoundException {
    	log.debug("User {} requested search elements ", getSessionUser().getUsername());
    	return _searchService.findAllSearchElements(getSessionUser(),secured,readable,used);
    }
    
    @ApiOperation(value = "Gets the requested search element", notes = "Returns the  cdat search element", response = XdatSearch.class, responseContainer = "list")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested search elements."),
                   @ApiResponse(code = 404, message = "The requested search elements wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/search/elements/{elementName}", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public List<XnatSearchElementDto> getAllSearchElementByElementName(@ApiParam("The element name of the search element") @PathVariable final String elementName) throws NotFoundException  {
    	log.debug("User {} requested search elements with ELEMENT NAME {} ", getSessionUser().getUsername(), elementName);
    	return _searchService.findAllSearchElementsByElementName(getSessionUser(), elementName);
    }
    
    @ApiOperation(value = "Gets the requested search element", notes = "Returns the  cdat search element", response = XdatSearch.class, responseContainer = "list")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested SearchElementVersion."),
                   @ApiResponse(code = 404, message = "The requested SearchElementVersion wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/search/elements/{elementName}/versions", produces = MediaType.APPLICATION_JSON_VALUE, method = GET)
    public DisplayVersionDto getSearchElementVersionByElementName(@ApiParam("The element name of the search element") @PathVariable final String elementName) throws NotFoundException, DisplayFieldNotFoundException {
    	log.debug("User {} requested search elements with ELEMENT NAME {} with versions ", getSessionUser().getUsername(), elementName);
    	return _searchService.findSearchElementVersionByElementName(getSessionUser(), elementName).orElseThrow(() -> new NotFoundException(XdatStoredSearch.SCHEMA_ELEMENT_NAME, elementName));
    }
    
    @ApiOperation(value = "Gets the requested saved search", notes = "Returns the  xdat saved search", response = XdatSearch.class, responseContainer = "single")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested xdatSearch."),
    	 		   @ApiResponse(code = 400, message = "The requested projectId or searchId missing."),
                   @ApiResponse(code = 404, message = "The requested xdatSearch wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/searches/{searchId}", produces = MediaType.APPLICATION_XML_VALUE, method = GET)
    public XdatStoredSearch  getSavedSearchByProjectIdAndSearchId(@ApiParam(value = "The ID of the search saved.") @PathVariable  final String searchId,
    		@ApiParam(value = "The ID of the project.") @PathVariable final String projectId) throws DataFormatException, NotFoundException {
    	log.debug("User {} requested Saved Search with PROJECT ID {} and with SEARCH ID {} ", getSessionUser().getUsername(), projectId, searchId);
    	return _searchService.findSavedSearchByProjectIdAndSearchId(getSessionUser(), projectId, searchId).orElseThrow(() -> new NotFoundException(XdatStoredSearch.SCHEMA_ELEMENT_NAME, projectId));
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
    public XdatStoredSearch updateStoredSearch(@ApiParam("The ID of the search saved to be updated") @PathVariable final String searchId,
    									  @ApiParam("The ID of the search saved to be updated") @RequestParam(required = false) final Boolean saveAs,
    									  @ApiParam("The search saved to be updated.") @RequestBody final XdatStoredSearch xdatStoredSearch,
    									  @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
     									  @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
     									  @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
     									  @ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false)String eventAction,
     									  @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) throws InitializationException  {
        log.debug("Updating saved search with search ID {}",searchId );
        return _searchService.updateStoredSearch(getSessionUser(), xdatStoredSearch, searchId, saveAs,XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment ));
    }
    
    
    @ApiOperation(value = "Delete an existing search saved", notes = "Deletes the specified search saved.")
    @ApiResponses({@ApiResponse(code = 200, message = "Deleted the specified search saved."),
                   @ApiResponse(code = 403, message = "The user doesn't have permission to delete search saved in the specified search saved"),
                   @ApiResponse(code = 404, message = "The specified project or project doesn't exist"),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred")})
    @XapiRequestMapping(value = "/search/saved/{searchId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, method = DELETE)
    public void deleteSavedSearch(@ApiParam("The ID of the search saved to be deleted") @PathVariable final String searchId,
    						  @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
    						  @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
    						  @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
    						  @ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false)String eventAction,
    						  @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) throws SQLException {
        log.debug("Deleting saved search Id {}", searchId);
        _searchService.deleteSavedSearchBySearchId(getSessionUser(), searchId,XnatEventUtil.getXnatEventUtil(eventReason, eventId, eventType, eventAction, eventComment ));
    }
    
    @ApiOperation(value = "Delete the requested saved search", notes = "Returns the  xdat saved search")
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the requested xdatSearch."),
                   @ApiResponse(code = 404, message = "The requested xdatSearch wasn't found."),
                   @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "/projects/{projectId}/searches/{searchId}", produces = MediaType.APPLICATION_XML_VALUE, method = DELETE)
    public void  deleteSavedSearchByProjectIdAndSearchId(@ApiParam(value = "The ID of the search saved.") @PathVariable  final String searchId,
    													 @ApiParam(value = "The ID of the project.") @PathVariable  final String projectId,
    													 @ApiParam("The event reason  value ") @RequestParam(name = "eventReason", required = false)String eventReason,
    			    									 @ApiParam("The event id value ") @RequestParam(name = "eventId", required = false)String eventId,
    			    									 @ApiParam("The event type value ") @RequestParam(name = "eventType", required = false)String eventType,
    			    									 @ApiParam("The event  action value ") @RequestParam(name = "eventAction", required = false)String eventAction,
    			    									 @ApiParam("The event comment value ") @RequestParam(name = "eventComment", required = false)String eventComment) throws JustificationAbsent, ActionNameAbsent{
        log.debug("Deleting search ID {} for Project {}",searchId, projectId);
         _searchService.deleteSavedSearchByProjectIdAndSearchId(getSessionUser(), projectId, searchId);
    }
    
    private final SearchService _searchService;
}

