/*
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2021, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * @author: Mohana Ramaratnam (mohana@radiologics.com)
 * @since: 07-03-2021
 */

package org.nrg.xnat.customforms.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.framework.beans.XnatPluginBean;
import org.nrg.framework.beans.XnatPluginBeanManager;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.AuthorizedRoles;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.customforms.exceptions.CustomVariableNameClashException;
import org.nrg.xnat.customforms.exceptions.InsufficientPermissionsException;
import org.nrg.xnat.customforms.helpers.CustomFormHelper;
import org.nrg.xnat.customforms.customvariable.migration.service.CustomVariableMigrator;
import org.nrg.xnat.customforms.pojo.ClientPojo;
import org.nrg.xnat.customforms.pojo.XnatFormsIOEnv;
import org.nrg.xnat.customforms.pojo.formio.FormAppliesToPoJo;
import org.nrg.xnat.customforms.pojo.formio.PseudoConfiguration;
import org.nrg.xnat.customforms.pojo.formio.RowIdentifier;
import org.nrg.xnat.customforms.service.CustomFormManagerService;
import org.nrg.xnat.customforms.service.CustomFormPermissionsService;
import org.nrg.xnat.customforms.utils.CustomFormsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.nrg.xdat.security.helpers.AccessLevel.Role;

@XapiRestController
@RequestMapping(value = "/customforms")
@Api("Custom Forms FormsIO API")
@Slf4j
public class CustomFormsApi extends AbstractXapiRestController {

    private final CustomFormManagerService formNanagerService;
    private final CustomFormPermissionsService permissionsService;
    private final Map<String, XnatPluginBean> plugins;
    private final CustomVariableMigrator customVariableMigrator;

    @Autowired
    public CustomFormsApi(final UserManagementServiceI userManagementService,
                          final CustomFormManagerService formNanagerService,
                          final RoleHolder roleHolder,
                          final XnatPluginBeanManager manager,
                          final CustomFormPermissionsService permissionsService,
                          final CustomVariableMigrator customVariableMigrator
    ) {
        super(userManagementService, roleHolder);
        this.formNanagerService = formNanagerService;
        plugins = new HashMap<>(manager.getPluginBeans());
        this.permissionsService = permissionsService;
        this.customVariableMigrator = customVariableMigrator;
    }

    @ApiOperation(value = "Accepts a JSON", notes = "Accepts a JSON", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.PUT)
    public ResponseEntity<String> addCustomFormsToProtocolsAndProjects(final @RequestBody String jsonbody) {
        try {
            final UserI user = XDAT.getUserDetails();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(Include.NON_NULL);
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            objectMapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
            ClientPojo clientPojo = objectMapper.readValue(jsonbody, ClientPojo.class);
            List<String> problems = clientPojo.validate(user);
            if (problems.size() > 0) {
                String issues = "Rejected:" + String.join(" ; ", problems);
                return new ResponseEntity<>(issues, HttpStatus.BAD_REQUEST);
            }
            CustomFormHelper customFormSaver = new CustomFormHelper();
            String formId = customFormSaver.save(clientPojo, user);
            if (formId != null) {
                return new ResponseEntity<>(formId, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (CustomVariableNameClashException ce) {
            log.error("Name clash detected ", ce);
            return new ResponseEntity<>("Could not save form as there exist other forms at the same level with identical property name(s) :" + ce.getClashes(), HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            log.error("Possibly invalid row id ", e);
            return new ResponseEntity<>("Invalid:" + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Possibly Invalid JSON ", e);
            return new ResponseEntity<>("Invalid json rejected:" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Gets Custom Form JSON for a given entity", notes = "Gets Custom Form JSON for a given entity", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "/element",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            method = RequestMethod.GET)
    public ResponseEntity<String> getCustomForm(final @RequestParam String xsiType,
                                                final @RequestParam(required = false) String id,
                                                final @RequestParam Boolean appendPrevNextButtons,
                                                final @RequestParam(required = false) String projectId,
                                                final @RequestParam(required = false) String visitId,
                                                final @RequestParam(required = false) String subtype
    ) {
        try {
            final UserI user = XDAT.getUserDetails();
            final String customFormJson = formNanagerService.getCustomForm(user, xsiType, id, projectId, visitId, subtype, appendPrevNextButtons);
            if (null == customFormJson) {
                return new ResponseEntity<>("Custom Forms Not Found", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(customFormJson, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Possibly Custom Form Fetcher Class had issues ", e);
            return new ResponseEntity<>("Could not fetch custom forms:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Marks a form as enabled", notes = "Marks a form as enabled", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "/enable", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> enableCustomForm(final @RequestBody String jsonbody) {
        final UserI user = XDAT.getUserDetails();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<FormAppliesToPoJo> formAppliesToPoJos = Arrays.asList(objectMapper.readValue(jsonbody, FormAppliesToPoJo[].class));
            boolean success = true;
            for (FormAppliesToPoJo formByStatusPoJo : formAppliesToPoJos) {
                success = success && formNanagerService.enableForm(user, formByStatusPoJo.getIdCustomVariableFormAppliesTo());
            }
            if (success) {
                return new ResponseEntity<>("Form Enabled", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Form not enabled", HttpStatus.BAD_REQUEST);
            }
        } catch (InsufficientPermissionsException ie) {
            return new ResponseEntity<>("Not enough permissions to enable form", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.error("Could not enable form ", e);
            return new ResponseEntity<>("Custom Form could not be enabled:" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Add projects to an existing form", notes = "Add projects to an existing form", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "/add/{rowId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, restrictTo = Role)
    @AuthorizedRoles({CustomFormsConstants.ADMIN_ROLE_NAME, CustomFormsConstants.DATAFORM_MANAGER_ROLE})
    public ResponseEntity<String> addProjectsToForm(final @PathVariable String rowId, final @RequestBody String jsonbody) {
        final UserI user = XDAT.getUserDetails();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<String> projects = Arrays.asList(objectMapper.readValue(jsonbody, String[].class));
            RowIdentifier rowIdentifier = RowIdentifier.Unmarshall(rowId);
            boolean success = formNanagerService.addProjectsToForm(user, rowIdentifier, projects);
            if (success) {
                return new ResponseEntity<>("Projects added to form", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Projects could not be added to form", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Could not add project to form ", e);
            return new ResponseEntity<>("Could not add project to form:" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Opt out of a form", notes = "Opt out of a form", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "/optout/{formId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> optOutCustomForm(final @PathVariable String formId, final @RequestBody String jsonbody) {
        final UserI user = XDAT.getUserDetails();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> projectIds = Arrays.asList(objectMapper.readValue(jsonbody, String[].class));
            boolean success = formNanagerService.optOutOfForm(user, formId, projectIds);
            if (success) {
                return new ResponseEntity<>("Projects  have opted out", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to opt out", HttpStatus.BAD_REQUEST);
            }
        } catch (InsufficientPermissionsException ie) {
            return new ResponseEntity<>("Not enough permissions to opt out of form", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.error("Could not enable form ", e);
            return new ResponseEntity<>("Custom Form could not be opted out of:" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Opt in of a form", notes = "Opt in of a form", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "/optin/{formId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> optInCustomForm(final @PathVariable String formId, final @RequestBody String jsonbody) {
        final UserI user = XDAT.getUserDetails();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> projectIds = Arrays.asList(objectMapper.readValue(jsonbody, String[].class));
            boolean success = formNanagerService.optInForm(user, formId, projectIds);
            if (success) {
                return new ResponseEntity<>(String.join(",", projectIds) + " opted in", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to opt in", HttpStatus.BAD_REQUEST);
            }
        } catch (InsufficientPermissionsException ie) {
            return new ResponseEntity<>("Not enough permissions to opt in form", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.error("Could not enable form ", e);
            return new ResponseEntity<>("Custom Form could not be opted in :" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Set ZIndex of a form", notes = "Set ZIndex of a form", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "/formId/{formId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> modifyZIndex(final @PathVariable String formId, final @RequestParam Integer zIndex) {
        final UserI user = XDAT.getUserDetails();
        try {
            boolean success = formNanagerService.modifyZIndex(user, zIndex, formId);
            if (success) {
                return new ResponseEntity<>("ZIndex updated to " + zIndex, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to modify ZIndex", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Could not modify zIndex of  form " + formId, e);
            return new ResponseEntity<>("ZIndex of Custom Form could not be modified :" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @ApiOperation(value = "Promote a Project specific form to Site Repository", notes = "Promote a form to site repository", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "/promote", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, restrictTo = Role)
    @AuthorizedRoles({CustomFormsConstants.ADMIN_ROLE_NAME, CustomFormsConstants.DATAFORM_MANAGER_ROLE})
    public ResponseEntity<String> promoteform(final @RequestBody String jsonbody) {
        try {
            final UserI user = XDAT.getUserDetails();
            ObjectMapper objectMapper = new ObjectMapper();
            List<FormAppliesToPoJo> formAppliesToPoJos = Arrays.asList(objectMapper.readValue(jsonbody, FormAppliesToPoJo[].class));
            boolean success = formNanagerService.promoteForm(user, formAppliesToPoJos);
            if (success) {
                return new ResponseEntity<>("Form promoted to site repository", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to promote form to site repository. Possibly same form id not provided", HttpStatus.BAD_REQUEST);
            }
        } catch (NotFoundException ie) {
            return new ResponseEntity<>("Form  not found", HttpStatus.BAD_REQUEST);
        } catch (CustomVariableNameClashException ce) {
            log.error("Name clash detected ", ce);
            return new ResponseEntity<>("Could not promote form as there exist other forms at the same level with identical property name(s) :" + ce.getClashes(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Possibly failed to parse json " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Marks a form as disabled", notes = "Marks a form as disabled", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "/disable", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> disableCustomForm(final @RequestBody String jsonbody) {
        final UserI user = XDAT.getUserDetails();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<FormAppliesToPoJo> formAppliesToPoJos = Arrays.asList(objectMapper.readValue(jsonbody, FormAppliesToPoJo[].class));
            boolean success = true;
            for (FormAppliesToPoJo formByStatusPoJo : formAppliesToPoJos) {
                success = success && formNanagerService.disableForm(user, formByStatusPoJo.getIdCustomVariableFormAppliesTo());
            }
            if (success) {
                return new ResponseEntity<>("Form disabled", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Custom Form could not be disabled", HttpStatus.BAD_REQUEST);
            }
        } catch (InsufficientPermissionsException ie) {
            return new ResponseEntity<>("Not enough permissions to disable form", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.error("Could not disable form ", e);
            return new ResponseEntity<>("Custom Form could not be disabled:" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Deletes a form", notes = "Deletes a form is not backed up by data", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> deleteCustomForm(final @RequestBody String jsonbody) {
        final UserI user = XDAT.getUserDetails();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<FormAppliesToPoJo> formAppliesToPoJos = Arrays.asList(objectMapper.readValue(jsonbody, FormAppliesToPoJo[].class));
            List<String> deleteStatuses = new ArrayList<String>();
            for (FormAppliesToPoJo formAppliesToPoJo : formAppliesToPoJos) {
                String status = formNanagerService.deleteForm(user, formAppliesToPoJo.getIdCustomVariableFormAppliesTo());
                if (null != status) {
                    deleteStatuses.add(formAppliesToPoJo.getEntityId() == null ? "Site Wide: " + status : formAppliesToPoJo.getEntityId() + ": " + status);
                }
            }
            if (deleteStatuses.size() > 0) {
                return new ResponseEntity<>("Form " + String.join(",", deleteStatuses), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Custom Form could not be deleted", HttpStatus.BAD_REQUEST);
            }
        } catch (InsufficientPermissionsException ie) {
            return new ResponseEntity<>("Not enough permissions to disable form", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.error("Could not disable form ", e);
            return new ResponseEntity<>("Custom Form could not be disabled:" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //TODO: Are the configurations open to all?
    @ApiOperation(value = "Gets list of all site wide and projects which have custom forms", notes = "Gets Site wide and project Custom Form configurations", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public ResponseEntity<List<PseudoConfiguration>> getAllCustomFormConfigurations(
            final @RequestParam(required = false) String projectId) {
        try {
            final UserI user = XDAT.getUserDetails();
            CustomFormHelper customFormHelper = new CustomFormHelper();
            List<PseudoConfiguration> configurations = customFormHelper.getAllCustomFormConfigurations(user, projectId);
            if (null == configurations) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(configurations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Get the XNAT Deployment environment related to FormsIO", notes = "Get the XNAT Deployment environment related to FormsIO", response = String.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Unexpected error")})
    @XapiRequestMapping(value = "/env", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public ResponseEntity<XnatFormsIOEnv> getXnatEnvironmentForFormsIO(
            final @RequestParam(required = false) String projectId) {
        try {
            final UserI user = XDAT.getUserDetails();
            if (!permissionsService.isUserAdminOrDataManager(user) && !permissionsService.isUserProjectOwner(user, projectId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            boolean protocolsPluginDeployed = false;
            if (plugins.containsKey(CustomFormsConstants.PROTOCOLS_PLUGIN_IDENTIFIER)) {
                protocolsPluginDeployed = true;
            }
            XnatFormsIOEnv env = new XnatFormsIOEnv(protocolsPluginDeployed);
            return new ResponseEntity<>(env, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}