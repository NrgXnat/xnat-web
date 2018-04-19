/*
 * web: org.nrg.xapi.rest.data.CatalogApi
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xapi.rest.data;

import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.model.customVariables.VariableModel;
import org.nrg.xapi.model.customVariables.VariableSetModel;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.entities.CustomVariable;
import org.nrg.xdat.entities.CustomVariableSet;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.services.PermissionsServiceI;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.services.CustomVariableService;
import org.nrg.xdat.services.CustomVariableSetService;
import org.nrg.xft.security.UserI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.nrg.xdat.security.helpers.AccessLevel.Authenticated;
import static org.nrg.xdat.security.helpers.AccessLevel.User;

@Api(description = "XNAT Custom Variables API")
@XapiRestController
@RequestMapping(value = "/customvariables")
public class CustomVariablesApi extends AbstractXapiRestController {
    @Autowired
    public CustomVariablesApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final CustomVariableSetService setService, final CustomVariableService variableService, final PermissionsServiceI permissionsService) {
        super(userManagementService, roleHolder);
        _setService = setService;
        _variableService = variableService;
        _permissionsService = permissionsService;
    }

    @ApiOperation(value = "Get list of custom variable sets.", notes = "This method returns all custom variable sets on the XNAT system that the user has access to. It will not return custom variables restricted to projects the user does not have access to.", response = CustomVariableSet.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "A list of custom variable sets."),
            @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
            @ApiResponse(code = 403, message = "You do not have sufficient permissions to access the list of custom variable sets."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "setlist", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = User)
    @ResponseBody
    public ResponseEntity<List<CustomVariableSet>> getSetList() {
        UserI user = getSessionUser();
        if(Roles.isSiteAdmin(user)){
            return new ResponseEntity<List<CustomVariableSet>>(_setService.getAll(), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<List<CustomVariableSet>>(_setService.getUserViewableSets(user), HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Get list of custom variable sets for data with a given xsiType.", notes = "This method returns all custom variable sets on the XNAT system that the user has access to for data with a given xsiType. It will not return custom variables restricted to projects the user does not have access to.", response = CustomVariableSet.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "A list of custom variable sets."),
            @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
            @ApiResponse(code = 403, message = "You do not have sufficient permissions to access the list of custom variable sets."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "setlist/type/{xsiType}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = User)
    @ResponseBody
    public ResponseEntity<List<CustomVariableSet>> getSetListByType(@ApiParam("The xsiType to get the custom variable sets for.") @PathVariable final String xsiType) {
        UserI user = getSessionUser();
        if(Roles.isSiteAdmin(user)){
            return new ResponseEntity<List<CustomVariableSet>>(_setService.getAllForType(xsiType), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<List<CustomVariableSet>>(_setService.getUserViewableSetsForType(user, xsiType), HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Get custom variable set.", notes = "This method returns the custom variable set with the specified name.", response = CustomVariableSet.class)
    @ApiResponses({@ApiResponse(code = 200, message = "A custom variable set."),
            @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
            @ApiResponse(code = 403, message = "You do not have sufficient permissions to access custom variable set."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "variableset/{setName}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = User)
    @ResponseBody
    public ResponseEntity<CustomVariableSet> getSet(@ApiParam("The name of the custom variable set to get.") @PathVariable final String setName) {
        UserI user = getSessionUser();
        if(Roles.isSiteAdmin(user)){
            return new ResponseEntity<CustomVariableSet>(_setService.getSet(setName), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<CustomVariableSet>(_setService.getUserViewableSet(user, setName), HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Get whether the user can read the custom variable set.", notes = "This method returns whether user can read the custom variable set with the specified name.", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Whether the user can read the custom variable set."),
            @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "variableset/{setName}/canread", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Authenticated)
    @ResponseBody
    public ResponseEntity<Boolean> getSetReadPermissions(@ApiParam("The name of the custom variable set to check read permissions for.") @PathVariable final String setName) {
        UserI user = getSessionUser();
        CustomVariableSet set = _setService.getSet(setName);
        if(set==null){
            return new ResponseEntity<Boolean>(false, HttpStatus.OK);
        }
        else if(Roles.isSiteAdmin(user)){
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        }
        else if(_setService.getUserViewableSet(user, setName)!=null){
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<Boolean>(false, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Get whether the user can edit the custom variable set.", notes = "This method returns whether user can edit the custom variable set with the specified name.", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Whether the user can edit the custom variable set."),
            @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "variableset/{setName}/canedit", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Authenticated)
    @ResponseBody
    public ResponseEntity<Boolean> getSetEditPermissions(@ApiParam("The name of the custom variable set to check edit permissions for.") @PathVariable final String setName) {
        UserI user = getSessionUser();
        CustomVariableSet set = _setService.getSet(setName);
        if(set==null){
            return new ResponseEntity<Boolean>(false, HttpStatus.OK);
        }
        else if(Roles.isSiteAdmin(user)){
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        }
        else if(_setService.getUserEditableSet(user, setName)!=null){
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<Boolean>(false, HttpStatus.OK);
        }
    }





    @ApiOperation(value = "Creates a new custom variable set from the submitted attributes.", notes = "Returns the newly created custom variable set with the submitted attributes.", response = CustomVariableSet.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created custom variable set."),
            @ApiResponse(code = 403, message = "Insufficient privileges to create the submitted custom variable set."),
            @ApiResponse(code = 404, message = "The requested custom variable set wasn't found."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "variableset", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<CustomVariableSet> createSet(@RequestBody final VariableSetModel setModel) throws Exception {
        UserI user = getSessionUser();
        if(Permissions.canEditProject(user, setModel.getOwningProjectId())) {
            CustomVariableSet newSet = new CustomVariableSet();
            newSet.setName(setModel.getName());
            newSet.setDatatype(setModel.getDatatype());
            newSet.setDescription(setModel.getDescription());
            newSet.setProjectSpecific(setModel.isProjectSpecific());
            newSet.setOwningProjectId(setModel.getOwningProjectId());
            List<VariableModel> modelVariables = setModel.getVariables();
            List<CustomVariable> newSetVariables = new ArrayList<>();
            for(VariableModel modelVariable: modelVariables) {
                CustomVariable newVariable = new CustomVariable();
                newVariable.setName(modelVariable.getName());
                newVariable.setType(modelVariable.getType());
                newVariable.setRequired(modelVariable.getRequired());
                newVariable.setPossibleValues(modelVariable.getPossibleValues());
                newSetVariables.add(_variableService.create(newVariable));
            }
            newSet.setVariables(newSetVariables);
            _setService.create(newSet);
            return new ResponseEntity<>(newSet, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @ApiOperation(value = "Updates the requested custom variable set from the submitted attributes.", notes = "Returns the updated custom variable set.", response = CustomVariableSet.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated custom variable set."),
            @ApiResponse(code = 304, message = "The requested custom variable set is the same as the submitted custom variable set."),
            @ApiResponse(code = 403, message = "Insufficient privileges to edit the requested custom variable set."),
            @ApiResponse(code = 404, message = "The requested custom variable set wasn't found."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "variableset/{setName}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<CustomVariableSet> updateSet(@PathVariable("setName") final String setName, @RequestBody final VariableSetModel setModel) throws Exception {
        final UserI user = getSessionUser();

        final CustomVariableSet existing = _setService.getSet(setName);
        if (existing == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(!Roles.isSiteAdmin(user) && _setService.getUserEditableSet(user, setName)==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        boolean isDirty = false;
        // Only update fields that are actually included in the submitted data and differ from the original source.
        if (StringUtils.isNotBlank(setModel.getName()) && !StringUtils.equals(setModel.getName(), existing.getName())) {
            existing.setName(setModel.getName());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(setModel.getDatatype()) && !StringUtils.equals(setModel.getDatatype(), existing.getDatatype())) {
            existing.setDatatype(setModel.getDatatype());
            isDirty = true;
        }
        if (StringUtils.isNotBlank(setModel.getDescription()) && !StringUtils.equals(setModel.getDescription(), existing.getDescription())) {
            existing.setDescription(setModel.getDescription());
            isDirty = true;
        }
        if (setModel.getVariables()!=null) {
            List<CustomVariable> existingVariables = existing.getVariables();
            if(existingVariables!=null){
                for(CustomVariable existVar : existingVariables){
                    _variableService.delete(existVar);
                }
            }

            List<VariableModel> modelVariables = setModel.getVariables();
            List<CustomVariable> newSetVariables = new ArrayList<>();
            for(VariableModel modelVariable: modelVariables) {
                CustomVariable newVariable = new CustomVariable();
                newVariable.setName(modelVariable.getName());
                newVariable.setType(modelVariable.getType());
                newVariable.setRequired(modelVariable.getRequired());
                newVariable.setPossibleValues(modelVariable.getPossibleValues());
                newSetVariables.add(_variableService.create(newVariable));
            }

            existing.setVariables(newSetVariables);
            isDirty = true;
        }
        if (setModel.isProjectSpecific()!=existing.isProjectSpecific()) {
            existing.setProjectSpecific(setModel.isProjectSpecific());
            isDirty = true;
        }

        if (isDirty) {
            _setService.update(existing);
            return new ResponseEntity<>(existing, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @ApiOperation(value = "Deletes the requested custom variable set.", notes = "Returns true if the requested custom variable set was successfully deleted. Returns false otherwise.", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns true to indicate the requested custom variable set was successfully deleted."),
            @ApiResponse(code = 404, message = "The requested custom variable set wasn't found."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "variableset/{setName}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Boolean> deleteCustomVariableSet(@PathVariable("setName") final String setName) throws Exception {
        final UserI user = getSessionUser();
        final CustomVariableSet existing = _setService.getSet(setName);
        if (existing == null) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        if(Roles.isSiteAdmin(user) || _setService.getUserEditableSet(user, setName)!=null){
            _setService.delete(_setService.getSet(setName));
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
    }









    @ApiOperation(value = "Get custom variable.", notes = "This method returns the custom variable with the specified name from the specified custom variable set.", response = CustomVariable.class)
    @ApiResponses({@ApiResponse(code = 200, message = "A custom variable."),
            @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
            @ApiResponse(code = 403, message = "You do not have sufficient permissions to access custom variable."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "variableset/{setName}/variable/{variableName}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = User)
    @ResponseBody
    public ResponseEntity<CustomVariable> variableGet(@ApiParam("The name of the custom variable set to get variable from.") @PathVariable final String setName, @ApiParam("The name of the custom variable.") @PathVariable final String variableName) {
        UserI user = getSessionUser();
        CustomVariableSet set = _setService.getSet(setName);
        if(Roles.isSiteAdmin(user)){
            return new ResponseEntity<CustomVariable>(_variableService.getVariableFromSet(set.getId(), variableName), HttpStatus.OK);
        }
        else if(!set.isProjectSpecific() || _permissionsService.getUserReadableProjects(user).contains(set.getOwningProjectId())) {
                return new ResponseEntity<CustomVariable>(_variableService.getVariableFromSet(set.getId(), variableName), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @ApiOperation(value = "Creates a new custom variable from the submitted attributes.", notes = "Returns the newly created custom variable with the submitted attributes.", response = CustomVariable.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the newly created custom variable."),
            @ApiResponse(code = 403, message = "Insufficient privileges to create the submitted custom variable."),
            @ApiResponse(code = 404, message = "The requested custom variable wasn't found."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "variableset/{setName}/variable/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<CustomVariable> createVariable(@ApiParam("The name of the custom variable set to get variable from.") @PathVariable final String setName, @RequestBody final VariableModel modelVariable) throws Exception {
        UserI user = getSessionUser();
        CustomVariableSet set = _setService.getSet(setName);
        if(Permissions.canEditProject(user, set.getOwningProjectId())) {
            CustomVariable newVariable = new CustomVariable();
            newVariable.setName(modelVariable.getName());
            newVariable.setType(modelVariable.getType());
            newVariable.setRequired(modelVariable.getRequired());
            newVariable.setPossibleValues(modelVariable.getPossibleValues());

            CustomVariable createdVariable = _variableService.create(newVariable);
            List<CustomVariable> vars = set.getVariables();
            vars.add(createdVariable);
            set.setVariables(vars);
            _setService.update(set);
            return new ResponseEntity<>(createdVariable, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

//    @ApiOperation(value = "Updates the requested custom variable from the submitted attributes.", notes = "Returns the updated custom variable.", response = CustomVariable.class)
//    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated custom variable."),
//            @ApiResponse(code = 304, message = "The requested custom variable set is the same as the submitted custom variable."),
//            @ApiResponse(code = 403, message = "Insufficient privileges to edit the requested custom variable."),
//            @ApiResponse(code = 404, message = "The requested custom variable wasn't found."),
//            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
//    @XapiRequestMapping(value = "variableset/{setName}/variable/{variableName}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT)
//    @ResponseBody
//    public ResponseEntity<CustomVariable> updateVariable(@PathVariable("setName") final String setName, @ApiParam("The name of the custom variable.") @PathVariable final String variableName, @RequestBody final CustomVariable customVariable) throws Exception {
//        final UserI user = getSessionUser();
//
//        final CustomVariableSet existingSet = _service.getSet(setName);
//        if (existingSet == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        if(!Roles.isSiteAdmin(user) && _service.getUserEditableSet(user, setName)==null){
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//
//        List<CustomVariable> variableList = existingSet.getVariables();
//        CustomVariable existing = null;
//        for(CustomVariable tempVar: variableList){
//            if(StringUtils.equals(tempVar.getName(), variableName)){
//                existing = tempVar;
//            }
//        }
//        if (existing == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        boolean isDirty = false;
//        // Only update fields that are actually included in the submitted data and differ from the original source.
//        if (StringUtils.isNotBlank(customVariable.getName()) && !StringUtils.equals(customVariable.getName(), existing.getName())) {
//            existing.setName(customVariable.getName());
//            isDirty = true;
//        }
//        if (StringUtils.isNotBlank(customVariable.getType()) && !StringUtils.equals(customVariable.getType(), existing.getType())) {
//            existing.setType(customVariable.getType());
//            isDirty = true;
//        }
//        if (StringUtils.isNotBlank(customVariable.getRequired()) && !StringUtils.equals(customVariable.getRequired(), existing.getRequired())) {
//            existing.setRequired(customVariable.getRequired());
//            isDirty = true;
//        }
//        if (customVariable.getPossibleValues()!=null && !customVariable.getPossibleValues().equals(existing.getPossibleValues())) {
//            existing.setPossibleValues(customVariable.getPossibleValues());
//            isDirty = true;
//        }
//
//        if (isDirty) {
//            _service.update(existing);
//            return new ResponseEntity<>(existing, HttpStatus.OK);
//        }
//
//        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
//    }
//
//    @ApiOperation(value = "Deletes the requested custom variable set.", notes = "Returns true if the requested custom variable set was successfully deleted. Returns false otherwise.", response = Boolean.class)
//    @ApiResponses({@ApiResponse(code = 200, message = "Returns true to indicate the requested custom variable set was successfully deleted."),
//            @ApiResponse(code = 404, message = "The requested custom variable set wasn't found."),
//            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
//    @XapiRequestMapping(value = "variableset/{setName}/variable/{variableName}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
//    @ResponseBody
//    public ResponseEntity<Boolean> deleteVariable(@PathVariable("setName") final String setName, @ApiParam("The name of the custom variable.") @PathVariable final String variableName) throws Exception {
//        final UserI user = getSessionUser();
//        final CustomVariableSet existing = _service.getSet(setName);
//        if (existing == null) {
//            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
//        }
//        if(Roles.isSiteAdmin(user) || _service.getUserEditableSet(user, setName)!=null){
//            _service.delete(_service.getSet(setName));
//            return new ResponseEntity<>(true, HttpStatus.OK);
//        }
//        return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
//    }
//
//





    private static final Logger _log = LoggerFactory.getLogger(CustomVariablesApi.class);
//
//    private final CatalogService        _service;
//    private final SiteConfigPreferences _preferences;

    private CustomVariableSetService _setService;
    private CustomVariableService _variableService;
    private PermissionsServiceI _permissionsService;
}