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
import org.nrg.xapi.rest.ProjectId;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.entities.CustomVariable;
import org.nrg.xdat.entities.CustomVariableMapping;
import org.nrg.xdat.entities.CustomVariableSet;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.services.PermissionsServiceI;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.services.CustomVariableMappingService;
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

import static org.nrg.xdat.security.helpers.AccessLevel.*;

@Api(description = "XNAT Custom Variables API")
@XapiRestController
@RequestMapping(value = "/customvariables")
public class CustomVariablesApi extends AbstractXapiRestController {
    @Autowired
    public CustomVariablesApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final CustomVariableSetService setService, final CustomVariableService variableService, final CustomVariableMappingService mappingService, final PermissionsServiceI permissionsService) {
        super(userManagementService, roleHolder);
        _setService = setService;
        _variableService = variableService;
        _permissionsService = permissionsService;
        _mappingService = mappingService;
    }

    @ApiOperation(value = "Get list of custom variable sets.", notes = "This method returns all custom variable sets on the XNAT system that the user has access to. It will not return custom variables restricted to projects the user does not have access to.", response = CustomVariableSet.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "A list of custom variable sets."),
            @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
            @ApiResponse(code = 403, message = "You do not have sufficient permissions to access the list of custom variable sets."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "setlist", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Authenticated)
    @ResponseBody
    public ResponseEntity<List<CustomVariableSet>> getSetList() {
        UserI user = getSessionUser();
        if(Roles.isSiteAdmin(user)){
            return new ResponseEntity<List<CustomVariableSet>>(_setService.getAll(), HttpStatus.OK);
        }
        else {
            List<String> projectIdsUserHasAccessTo = _permissionsService.getUserReadableProjects(user);
            List<String> setNames = _mappingService.getSetsForProjects(projectIdsUserHasAccessTo);
            return new ResponseEntity<List<CustomVariableSet>>(_setService.getSet(setNames), HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Get list of custom variable sets for data with a given xsiType.", notes = "This method returns all custom variable sets on the XNAT system that the user has access to for data with a given xsiType. It will not return custom variables restricted to projects the user does not have access to.", response = CustomVariableSet.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "A list of custom variable sets."),
            @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
            @ApiResponse(code = 403, message = "You do not have sufficient permissions to access the list of custom variable sets."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "setlist/type/{xsiType}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Authenticated)
    @ResponseBody
    public ResponseEntity<List<CustomVariableSet>> getSetListByType(@ApiParam("The xsiType to get the custom variable sets for.") @PathVariable final String xsiType) {
        UserI user = getSessionUser();
        if(Roles.isSiteAdmin(user)){
            return new ResponseEntity<List<CustomVariableSet>>(_setService.getAllForType(xsiType), HttpStatus.OK);
        }
        else {
            List<String> projectIdsUserHasAccessTo = _permissionsService.getUserReadableProjects(user);
            List<String> setNames = _mappingService.getSetsForProjects(projectIdsUserHasAccessTo);
            return new ResponseEntity<List<CustomVariableSet>>(_setService.getForTypeFromSetOfSets(xsiType, setNames), HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Get list of custom variable sets for a given project.", notes = "This method returns all custom variable sets on the XNAT system for a given project that the user has access to. It will not return custom variables restricted to projects the user does not have access to.", response = CustomVariableSet.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "A list of custom variable sets."),
            @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
            @ApiResponse(code = 403, message = "You do not have sufficient permissions to access the list of custom variable sets."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "setlist/project/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<CustomVariableSet>> getSetListByProject(@ApiParam("The projectId to get the custom variable sets for.") @PathVariable("projectId") @ProjectId final String projectId) throws Exception {
        UserI user = getSessionUser();
        List<String> setNames = _mappingService.getSetsForProject(projectId);
        return new ResponseEntity<List<CustomVariableSet>>(_setService.getSet(setNames), HttpStatus.OK);
    }

    @ApiOperation(value = "Get list of custom variable sets for a given project and type.", notes = "This method returns all custom variable sets on the XNAT system for a given project that the user has access to and for a given type. It will not return custom variables restricted to projects the user does not have access to.", response = CustomVariableSet.class, responseContainer = "List")
    @ApiResponses({@ApiResponse(code = 200, message = "A list of custom variable sets."),
            @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
            @ApiResponse(code = 403, message = "You do not have sufficient permissions to access the list of custom variable sets."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "setlist/project/{projectId}/type/{xsiType}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<List<CustomVariableSet>> getSetListByProjectAndType(@ApiParam("The projectId to get the custom variable sets for.") @PathVariable("projectId") @ProjectId final String projectId, @ApiParam("The xsiType to get the custom variable sets for.") @PathVariable final String xsiType) throws Exception {
        UserI user = getSessionUser();
        List<String> setNames = _mappingService.getSetsForProject(projectId);
        return new ResponseEntity<List<CustomVariableSet>>(_setService.getForTypeFromSetOfSets(xsiType, setNames), HttpStatus.OK);
    }

    @ApiOperation(value = "Get custom variable set.", notes = "This method returns the custom variable set with the specified name.", response = CustomVariableSet.class)
    @ApiResponses({@ApiResponse(code = 200, message = "A custom variable set."),
            @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
            @ApiResponse(code = 403, message = "You do not have sufficient permissions to access custom variable set."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "variableset/{setName}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Authenticated)
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

    @ApiOperation(value = "Get whether there is already a custom variable set for the given xsiType on the given project with the given name.",
            notes = "This method returns the custom variable set with the specified name.", response = CustomVariableSet.class)
    @ApiResponses({@ApiResponse(code = 200, message = "A custom variable set."),
            @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
            @ApiResponse(code = 403, message = "You do not have sufficient permissions to check for the existence of the custom variable set."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "setexists/project/{projectId}/type/{xsiType}/variableset/{setName}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Read)
    @ResponseBody
    public ResponseEntity<Boolean> getSet(@ApiParam("The projectId to get the custom variable sets for.") @PathVariable("projectId") @ProjectId final String projectId, @ApiParam("The xsiType to get the custom variable sets for.") @PathVariable final String xsiType, @ApiParam("The name of the custom variable set to get.") @PathVariable final String setName) throws Exception {
        UserI user = getSessionUser();
        CustomVariableSet set = _setService.getSet(setName);
        if(set!=null && StringUtils.equals(set.getDatatype(),xsiType)){
            if(_mappingService.getSetsForProject(projectId).contains(setName)){
                return new ResponseEntity<Boolean>(true, HttpStatus.OK);
            }
        }
        return new ResponseEntity<Boolean>(false, HttpStatus.OK);
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
            @ApiResponse(code = 409, message = "The custom variable set contained a variable whose name is already being used for that data type on that project."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "variableset", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST, restrictTo = Authenticated)
    @ResponseBody
    public ResponseEntity<CustomVariableSet> createSet(@RequestBody final VariableSetModel setModel) throws Exception {
        UserI user = getSessionUser();
        String owningProject = setModel.getOwningProjectId();
        if(Permissions.canEditProject(user, owningProject)) {
            if(_setService.getSet(setModel.getName())!=null){
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            CustomVariableSet newSet = new CustomVariableSet();
            newSet.setName(setModel.getName());
            newSet.setDatatype(setModel.getDatatype());
            newSet.setDescription(setModel.getDescription());
            newSet.setProjectSpecific(setModel.isProjectSpecific());
            newSet.setOwningProjectId(owningProject);
            List<VariableModel> modelVariables = setModel.getVariables();
            List<CustomVariable> newSetVariables = new ArrayList<>();
            for(VariableModel modelVariable: modelVariables) {
                String varName =modelVariable.getName();
                String type = modelVariable.getType();
                CustomVariable newVariable = new CustomVariable();
                newVariable.setName(varName);
                newVariable.setType(type);
                newVariable.setRequired(modelVariable.getRequired());
                newVariable.setPossibleValues(modelVariable.getPossibleValues());
                if(_setService.doesVariableNameExistForTypeAndProject(varName, setModel.getDatatype(), owningProject)){
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
                newSetVariables.add(newVariable);
            }

            List<CustomVariable> justCreatedNewSetVariables = new ArrayList<>();
            for(CustomVariable newVar:newSetVariables){
                justCreatedNewSetVariables.add(_variableService.create(newVar));
            }

            newSet.setVariables(justCreatedNewSetVariables);
            _setService.create(newSet);
            CustomVariableMapping mapping = new CustomVariableMapping(setModel.getOwningProjectId(), setModel.getName());
            _mappingService.createMappingIfDoesntExist(setModel.getOwningProjectId(), setModel.getName());
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
            @ApiResponse(code = 409, message = "The custom variable set contained a variable whose name is already being used for that data type on that project."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "variableset/{setName}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT, restrictTo = Authenticated)
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
        // Only update fields that differ from the original source.
        String owningProject = setModel.getOwningProjectId();
        if(!Permissions.canEditProject(user, owningProject)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        boolean nameChanged = false;
        if (!StringUtils.equals(setModel.getName(), existing.getName())) {
            if (_setService.getSet(setModel.getName()) != null) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            nameChanged = true;
        }
        if (setModel.getVariables()!=null) {
            List<CustomVariable> existingVariables = existing.getVariables();

            List<VariableModel> modelVariables = setModel.getVariables();
            List<CustomVariable> newSetVariables = new ArrayList<>();
            for(VariableModel modelVariable: modelVariables) {
                String varName =modelVariable.getName();
                CustomVariable newVariable = new CustomVariable();
                newVariable.setName(varName);
                newVariable.setType(modelVariable.getType());
                newVariable.setRequired(modelVariable.getRequired());
                newVariable.setPossibleValues(modelVariable.getPossibleValues());
                if((nameChanged) && (_setService.doesVariableNameExistForTypeAndProject(varName, setModel.getDatatype(), owningProject) ||
                        _setService.doesVariableNameExistForTypeAndSetsProjects(varName, setModel.getDatatype(), existing))){
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
                newSetVariables.add(newVariable);
            }
            if(existingVariables!=null){
                for(CustomVariable existVar : existingVariables){
                    _variableService.delete(existVar);
                }
            }
            List<CustomVariable> justCreatedNewSetVariables = new ArrayList<>();
            for(CustomVariable newVar:newSetVariables){
                justCreatedNewSetVariables.add(_variableService.create(newVar));
            }
            existing.setVariables(justCreatedNewSetVariables);
            isDirty = true;
        }
        if (nameChanged) {
            existing.setName(setModel.getName());
            isDirty = true;
        }
        if (!StringUtils.equals(setModel.getDatatype(), existing.getDatatype())) {
            existing.setDatatype(setModel.getDatatype());
            isDirty = true;
        }
        if (!StringUtils.equals(setModel.getDescription(), existing.getDescription())) {
            existing.setDescription(setModel.getDescription());
            isDirty = true;
        }
        if (setModel.isProjectSpecific()!=existing.isProjectSpecific()) {
            existing.setProjectSpecific(setModel.isProjectSpecific());
            isDirty = true;
        }
        if (!StringUtils.equals(owningProject, existing.getOwningProjectId())) {
            existing.setDatatype(owningProject);
            _mappingService.createMappingIfDoesntExist(owningProject, setModel.getName());
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
    @XapiRequestMapping(value = "variableset/{setName}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE, restrictTo = Authenticated)
    @ResponseBody
    public ResponseEntity<Boolean> deleteCustomVariableSet(@PathVariable("setName") final String setName) throws Exception {
        final UserI user = getSessionUser();
        final CustomVariableSet existing = _setService.getSet(setName);
        if (existing == null) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        if(Roles.isSiteAdmin(user) || _setService.getUserEditableSet(user, setName)!=null){
            CustomVariableSet set = _setService.getSet(setName);
            for(CustomVariable var: set.getVariables()){
                _variableService.delete(var);
            }
            _setService.delete(_setService.getSet(setName));
            _mappingService.deleteMappingsForSet(setName);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
    }

    @ApiOperation(value = "Deletes the requested custom variable.", notes = "Returns true if the requested custom variable was successfully deleted. Returns false otherwise.", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns true to indicate the requested custom variable was successfully deleted."),
            @ApiResponse(code = 404, message = "The requested custom variable wasn't found."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "variableset/{setName}/variable/{variableName}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE, restrictTo = Authenticated)
    @ResponseBody
    public ResponseEntity<Boolean> deleteCustomVariable(@PathVariable("setName") final String setName, @ApiParam("The name of the custom variable.") @PathVariable final String variableName) throws Exception {
        final UserI user = getSessionUser();
        final CustomVariableSet existing = _setService.getSet(setName);
        if (existing == null) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        if(Roles.isSiteAdmin(user) || _setService.getUserEditableSet(user, setName)!=null){
            CustomVariableSet set = _setService.getSet(setName);
            CustomVariable variableToDelete = _variableService.getVariableFromSet(set.getId(),variableName);
            _variableService.delete(variableToDelete);

            List<CustomVariable> vars = set.getVariables();
            vars.remove(variableToDelete);
            set.setVariables(vars);
            _setService.update(set);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
    }

    @ApiOperation(value = "Get custom variable.", notes = "This method returns the custom variable with the specified name from the specified custom variable set.", response = CustomVariable.class)
    @ApiResponses({@ApiResponse(code = 200, message = "A custom variable."),
            @ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
            @ApiResponse(code = 403, message = "You do not have sufficient permissions to access custom variable."),
            @ApiResponse(code = 500, message = "An unexpected error occurred.")})
    @XapiRequestMapping(value = "variableset/{setName}/variable/{variableName}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET, restrictTo = Authenticated)
    @ResponseBody
    public ResponseEntity<CustomVariable> variableGet(@ApiParam("The name of the custom variable set to get variable from.") @PathVariable final String setName, @ApiParam("The name of the custom variable.") @PathVariable final String variableName) {
        UserI user = getSessionUser();
        CustomVariableSet set = _setService.getSet(setName);
        if(set==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else if(Roles.isSiteAdmin(user)){
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
            @ApiResponse(code = 409, message = "The custom variable set contained a variable whose name is already being used for that data type on that project."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "variableset/{setName}/variable/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST, restrictTo = Authenticated)
    @ResponseBody
    public ResponseEntity<CustomVariable> createVariable(@ApiParam("The name of the custom variable set to get variable from.") @PathVariable final String setName, @RequestBody final VariableModel modelVariable) throws Exception {
        UserI user = getSessionUser();
        CustomVariableSet set = _setService.getSet(setName);
        if(set==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(Permissions.canEditProject(user, set.getOwningProjectId())) {
            CustomVariable newVariable = new CustomVariable();
            String varName =modelVariable.getName();
            String type = modelVariable.getType();

            if(_setService.doesVariableNameExistForTypeAndSetsProjects(varName, type, set)){
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            newVariable.setName(varName);
            newVariable.setType(type);
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

    @ApiOperation(value = "Updates a custom variable with the submitted attributes.", notes = "Returns the updated custom variable with the submitted attributes.", response = CustomVariable.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns the updated custom variable."),
            @ApiResponse(code = 403, message = "Insufficient privileges to create the submitted custom variable."),
            @ApiResponse(code = 404, message = "The requested custom variable wasn't found."),
            @ApiResponse(code = 409, message = "The custom variable set contained a variable whose name is already being used for that data type on that project."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "variableset/{setName}/variable/{variableName}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT, restrictTo = Authenticated)
    @ResponseBody
    public ResponseEntity<CustomVariable> updateVariable(@ApiParam("The name of the custom variable set to get variable from.") @PathVariable final String setName, @ApiParam("The name of the custom variable.") @PathVariable final String variableName, @RequestBody final VariableModel modelVariable) throws Exception {
        UserI user = getSessionUser();
        CustomVariableSet set = _setService.getSet(setName);
        if(set==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(Permissions.canEditProject(user, set.getOwningProjectId())) {
            CustomVariable existingVariable = _variableService.getVariableFromSet(set.getId(),variableName);
            String varName =modelVariable.getName();

            if(_setService.doesVariableNameExistForTypeAndSetsProjects(varName, set.getDatatype(), set)){
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }

            if (StringUtils.isNotBlank(varName) && !StringUtils.equals(varName, existingVariable.getName())) {
                existingVariable.setName(varName);
            }
            if (StringUtils.isNotBlank(modelVariable.getType()) && !StringUtils.equals(modelVariable.getType(), existingVariable.getType())) {
                existingVariable.setType(modelVariable.getType());
            }
            if (StringUtils.isNotBlank(modelVariable.getRequired()) && !StringUtils.equals(modelVariable.getRequired(), existingVariable.getRequired())) {
                existingVariable.setRequired(modelVariable.getRequired());
            }
            if (modelVariable.getPossibleValues()!=null && !modelVariable.getPossibleValues().equals(existingVariable.getPossibleValues())) {
                existingVariable.setPossibleValues(modelVariable.getPossibleValues());
            }
            _variableService.update(existingVariable);
            return new ResponseEntity<>(existingVariable, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @ApiOperation(value = "Adds an existing custom variable set to a new project.", notes = "Returns whether the custom variable set was added.", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns boolean representing whether the custom variable set was added."),
            @ApiResponse(code = 403, message = "Insufficient privileges to add the custom variable set."),
            @ApiResponse(code = 404, message = "The requested custom variable wasn't found."),
            @ApiResponse(code = 409, message = "The custom variable set contained a variable whose name is already being used for that data type on that project."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "variableset/{setName}/addToProject/{projectId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST, restrictTo = Edit)
    @ResponseBody
    public ResponseEntity<Boolean> addToProject(@ApiParam("The name of the custom variable set to get variable from.") @PathVariable final String setName, @ApiParam("The projectId to add the custom variable set to.") @PathVariable("projectId") @ProjectId final String projectId) throws Exception {
        UserI user = getSessionUser();

        CustomVariableSet set = _setService.getUserViewableSet(user,setName);
        if(set==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else {
            if(!set.isProjectSpecific()){
                if(_setService.doAnyOfTheseVariablesExistForTypeAndProject(set.getVariables(), set.getDatatype(), projectId)){
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }

                boolean created = _mappingService.createMappingIfDoesntExist(projectId,setName);
                return new ResponseEntity<>(created, HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
    }

    @ApiOperation(value = "Copies an existing custom variable set to a new project.", notes = "Returns whether the custom variable set was copied.", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns boolean representing whether the custom variable set was copied."),
            @ApiResponse(code = 403, message = "Insufficient privileges to copy the custom variable set."),
            @ApiResponse(code = 404, message = "The requested custom variable wasn't found."),
            @ApiResponse(code = 409, message = "The custom variable set contained a variable whose name is already being used for that data type on that project."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "variableset/{setName}/copyToProject/{projectId}/newname/{newSetName}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST, restrictTo = Edit)
    @ResponseBody
    public ResponseEntity<Boolean> copyToProject(@ApiParam("The name of the custom variable set to get variable from.") @PathVariable final String setName, @ApiParam("The projectId to copy the custom variable set to.") @PathVariable("projectId") @ProjectId final String projectId, @ApiParam("The name that the new custom variable set will be given.") @PathVariable final String newSetName) throws Exception {
        UserI user = getSessionUser();

        CustomVariableSet set = _setService.getUserViewableSet(user,setName);
        if(set==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else {
            List<CustomVariable> existingVariables = set.getVariables();
            List<CustomVariable> newSetVariables = new ArrayList<>();
            for(CustomVariable existingVariable: existingVariables) {
                CustomVariable newVariable = new CustomVariable();
                newVariable.setName(existingVariable.getName());
                newVariable.setType(existingVariable.getType());
                newVariable.setRequired(existingVariable.getRequired());
                newVariable.setPossibleValues(existingVariable.getPossibleValues());
                newSetVariables.add(newVariable);
            }
            if(_setService.doAnyOfTheseVariablesExistForTypeAndProject(newSetVariables, set.getDatatype(), projectId)){
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            List<CustomVariable> justCreatedNewSetVariables = new ArrayList<>();
            for(CustomVariable newVar:newSetVariables){
                justCreatedNewSetVariables.add(_variableService.create(newVar));
            }
            CustomVariableSet newSet = new CustomVariableSet(newSetName,set.getDatatype(),set.getDescription(),set.isProjectSpecific(),justCreatedNewSetVariables,projectId);
            _setService.create(newSet);

            boolean created = _mappingService.createMappingIfDoesntExist(projectId,newSetName);
            return new ResponseEntity<>(created, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Removes an existing custom variable set from a project. If this project is the owning project, the custom variable set will be deleted.", notes = "Returns whether the custom variable set was removed.", response = Boolean.class)
    @ApiResponses({@ApiResponse(code = 200, message = "Returns boolean representing whether the custom variable set was removed."),
            @ApiResponse(code = 403, message = "Insufficient privileges to remove the custom variable set."),
            @ApiResponse(code = 404, message = "The requested custom variable wasn't found."),
            @ApiResponse(code = 500, message = "An unexpected or unknown error occurred.")})
    @XapiRequestMapping(value = "variableset/{setName}/removeFromProject/{projectId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST, restrictTo = Edit)
    @ResponseBody
    public ResponseEntity<Boolean> removeFromProject(@ApiParam("The name of the custom variable set to get variable from.") @PathVariable final String setName, @ApiParam("The projectId to remove the custom variable set from.") @PathVariable("projectId") @ProjectId final String projectId) throws Exception {
        UserI user = getSessionUser();

        CustomVariableSet set = _setService.getUserViewableSet(user,setName);
        if(set==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else {
            if(StringUtils.equals(set.getOwningProjectId(),projectId)){
                //If removing it from the owning project, the custom variable set will be deleted to prevent it from becoming an orphan.
                for(CustomVariable var: set.getVariables()){
                    _variableService.delete(var);
                }
                _setService.delete(set);
                _mappingService.deleteMappingsForSet(setName);
                return new ResponseEntity<>(true, HttpStatus.OK);
            }
            else {
                boolean removed = _mappingService.deleteMappingIfExists(projectId, setName);
                return new ResponseEntity<>(removed, HttpStatus.OK);
            }
        }
    }

    private static final Logger _log = LoggerFactory.getLogger(CustomVariablesApi.class);

    private CustomVariableSetService _setService;
    private CustomVariableService _variableService;
    private CustomVariableMappingService _mappingService;
    private PermissionsServiceI _permissionsService;
}