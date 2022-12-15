package org.nrg.xnat.customforms.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javassist.NotFoundException;
import javassist.tools.web.BadHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.NonUniqueObjectException;
import org.nrg.framework.constants.Scope;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.customforms.events.CustomFormEvent;
import org.nrg.xnat.customforms.exceptions.CustomFormFetcherNotFoundException;
import org.nrg.xnat.customforms.exceptions.CustomVariableNameClashException;
import org.nrg.xnat.customforms.exceptions.InsufficientPermissionsException;
import org.nrg.xnat.customforms.interfaces.CustomFormFetcherI;
import org.nrg.xnat.customforms.manager.DefaultCustomFormManager;
import org.nrg.xnat.customforms.pojo.ComponentPojo;
import org.nrg.xnat.customforms.pojo.UserOptionsPojo;
import org.nrg.xnat.customforms.pojo.formio.FormAppliesToPoJo;
import org.nrg.xnat.customforms.pojo.formio.RowIdentifier;
import org.nrg.xnat.customforms.utils.CustomFormsConstants;
import org.nrg.xnat.customforms.utils.FormsIOJsonUtils;
import org.nrg.xnat.entities.CustomVariableAppliesTo;
import org.nrg.xnat.entities.CustomVariableForm;
import org.nrg.xnat.entities.CustomVariableFormAppliesTo;
import org.nrg.xnat.utils.WorkflowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.nrg.xnat.customforms.events.CustomFormEventI.*;
import static org.nrg.xnat.customforms.utils.CustomFormsConstants.*;


@Service
@Slf4j
public class CustomFormManagerService {

    private final CustomVariableAppliesToService selectionService;
    private final CustomVariableFormService formService;
    private final CustomVariableFormAppliesToService customVariableFormAppliesToService;
    private final ObjectSaverService objectSaver;
    private final CustomFormPermissionsService customFormPermissionsService;
    private final DataLocateService dataLocateService;
    private final DefaultCustomFormManager defaultCustomFormManager;


    @Autowired
    public CustomFormManagerService(final CustomVariableAppliesToService selectionService,
                                    final CustomVariableFormService formService,
                                    final CustomVariableFormAppliesToService customVariableFormAppliesToService,
                                    final ObjectSaverService objectSaver,
                                    final CustomFormPermissionsService customFormPermissionsService,
                                    final DataLocateService dataLocateService,
                                    final DefaultCustomFormManager defaultCustomFormManager) {
        this.selectionService = selectionService;
        this.formService = formService;
        this.customVariableFormAppliesToService = customVariableFormAppliesToService;
        this.objectSaver = objectSaver;
        this.customFormPermissionsService = customFormPermissionsService;
        this.dataLocateService = dataLocateService;
        this.defaultCustomFormManager = defaultCustomFormManager;
    }

    /**
     * Enables a form
     * @param user - the user requesting
     * @param formAppliesToId - the row identifier for the join table
     * @return - boolean - true if succeeds
     * @throws InsufficientPermissionsException
     */
    public boolean enableForm(final UserI user, final String formAppliesToId) throws InsufficientPermissionsException {
        boolean enabled = false;
        try {
            RowIdentifier rowId = RowIdentifier.Unmarshall(formAppliesToId);
            CustomVariableFormAppliesTo customVariableFormAppliesTo = customVariableFormAppliesToService.findByRowIdentifier(rowId);
            if (StringUtils.equals(CustomFormsConstants.OPTED_OUT_STATUS_STRING, customVariableFormAppliesTo.getStatus())) {
                return true;
            }
            if (customVariableFormAppliesTo != null && customFormPermissionsService.isUserAuthorized(user, customVariableFormAppliesTo)) {
                customVariableFormAppliesTo.setStatus(CustomFormsConstants.ENABLED_STATUS_STRING);
                objectSaver.saveCustomVariableFormAppliesTo(customVariableFormAppliesTo);
                createWorkFlowEntry(user, customVariableFormAppliesTo, rowId.getFormId(), "Form Enabled");
                enabled = true;
                triggerEvent(customVariableFormAppliesTo.getCustomVariableForm(),
                        customVariableFormAppliesTo.getCustomVariableAppliesTo().getDataType(), UPDATE);
            }
        } catch (Exception e) {
            if (e instanceof InsufficientPermissionsException) {
                throw e;
            }
        }
        return enabled;
    }


    /**
     * Disables a form
     * @param user - the user requesting
     * @param formAppliesId - the row identifier for the join table
     * @return boolean - success status
     * @throws Exception
     */

    public boolean disableForm(final UserI user, final String formAppliesId) throws Exception {
        boolean disabled = false;
        try {
            RowIdentifier rowId = RowIdentifier.Unmarshall(formAppliesId);
            CustomVariableFormAppliesTo customVariableFormAppliesTo = customVariableFormAppliesToService.findByRowIdentifier(rowId);
            if (StringUtils.equals(CustomFormsConstants.OPTED_OUT_STATUS_STRING, customVariableFormAppliesTo.getStatus())) {
                return true;
            }
            if (customVariableFormAppliesTo != null && customFormPermissionsService.isUserAuthorized(user, customVariableFormAppliesTo)) {
                customVariableFormAppliesTo.setStatus(CustomFormsConstants.DISABLED_STATUS_STRING);
                objectSaver.saveCustomVariableFormAppliesTo(customVariableFormAppliesTo);
                createWorkFlowEntry(user, customVariableFormAppliesTo, rowId.getFormId(), "Form disabled");
                triggerEvent(customVariableFormAppliesTo.getCustomVariableForm(),
                        customVariableFormAppliesTo.getCustomVariableAppliesTo().getDataType(), UPDATE);
                disabled = true;
            }
        } catch (Exception e) {
            log.error("Disable request for " + formAppliesId + " encountered exception", e);
            throw e;
        }
        return disabled;
    }

    private void triggerEvent(final CustomVariableForm form, final String dataType, final String status) {
        UUID formUUID = form.getFormUuid();
        if (null != formUUID) {
            final CustomFormEvent.Builder builder = CustomFormEvent.builder()
                    .xsiType(dataType)
                    .uuid(formUUID.toString())
                    .action(status);
            XDAT.triggerEvent(builder.build());
        }

    }

    /**
     * MOdifies ZIndex of  a form
     * @param user - the user requesting
     * @param formIdStr - the form Id for which the zIndex is to be modified
     * @return boolean - success status
     * @throws Exception
     */

    public boolean modifyZIndex(final UserI user, final Integer zIndex, final String formIdStr) throws Exception {
        boolean modified = false;
        try {
            long formId = Long.parseLong(formIdStr);
            List<CustomVariableFormAppliesTo> customVariableFormAppliesTos = customVariableFormAppliesToService.findByFormId(formId);
            if (customVariableFormAppliesTos != null && !customVariableFormAppliesTos.isEmpty()) {
                boolean isAuthorized = false;
                if (customFormPermissionsService.isUserAdminOrDataManager(user)) {
                    isAuthorized = true;
                }else {
                    if (customVariableFormAppliesTos.size() == 1) {
                        CustomVariableFormAppliesTo customVariableFormAppliesTo = customVariableFormAppliesTos.get(0);
                        CustomVariableAppliesTo appliesTo  = customVariableFormAppliesTo.getCustomVariableAppliesTo();
                        if (appliesTo.getScope().equals(Scope.Project)) {
                            String projId = appliesTo.getEntityId();
                            if (customFormPermissionsService.isUserProjectOwner(user, projId)) {
                                isAuthorized = true;
                            }
                        }
                    }
                }
                if (isAuthorized) {
                    CustomVariableFormAppliesTo customVariableFormAppliesTo = customVariableFormAppliesTos.get(0);
                    CustomVariableForm form = customVariableFormAppliesTo.getCustomVariableForm();
                    form.setzIndex(zIndex);
                    formService.saveOrUpdate(form);
                    createWorkFlowEntry(user, customVariableFormAppliesTo, formId, "Form ZIndex modified");
                    modified = true;
                }else {
                    throw new InsufficientPermissionsException("User not authorized");
                }
            }
        } catch (Exception e) {
            log.error("ZIndex Modification request for " + formIdStr + " encountered exception", e);
            throw e;
        }
        return modified;
    }


    /**
     * Delete a form. If there is any entity which has used the form to save data, the form is only disabled
     * @param user - the user who requests
     * @param formAppliesId  - the row identifier for the join table
     * @return String - the status - Delete or Disabled
     * @throws Exception
     */

    public String deleteForm(final UserI user, final String formAppliesId) throws Exception {
        String status = null;
        try {
            RowIdentifier rowId = RowIdentifier.Unmarshall(formAppliesId);
            CustomVariableFormAppliesTo customVariableFormAppliesTo = customVariableFormAppliesToService.findByRowIdentifier(rowId);
            if (null !=customVariableFormAppliesTo && customFormPermissionsService.isUserAuthorized(user, customVariableFormAppliesTo)) {
                //If data does not exist, delete the form.
                //Else disable the form
                boolean dataHasBeenStoredForTheForm = dataLocateService.hasDataBeenAcquired(customVariableFormAppliesTo);
                if (dataHasBeenStoredForTheForm) {
                    customVariableFormAppliesTo.setStatus(CustomFormsConstants.DISABLED_STATUS_STRING);
                    objectSaver.saveCustomVariableFormAppliesTo(customVariableFormAppliesTo);
                    status = CustomFormsConstants.DISABLED_STATUS_STRING;
                    triggerEvent(customVariableFormAppliesTo.getCustomVariableForm(),
                            customVariableFormAppliesTo.getCustomVariableAppliesTo().getDataType(), UPDATE);
                } else {
                    deleteSafely(rowId, customVariableFormAppliesTo);
                    deleteForm(rowId);
                    status = CustomFormsConstants.DELETED;
                    //Delete all project entries which have opted out of this form
                    deleteOptedOutSafely(rowId);
                    triggerEvent(customVariableFormAppliesTo.getCustomVariableForm(),
                            customVariableFormAppliesTo.getCustomVariableAppliesTo().getDataType(), DELETE);
                }
                createWorkFlowEntry(user, customVariableFormAppliesTo, rowId.getFormId(), String.format("Form %s STATUS: %s",rowId.getFormId(), status));
            }
        } catch (Exception e) {
            log.error("Disable request for " + formAppliesId + " encountered exception", e);
            throw e;
        }
        return status;
    }


    /**
     * Add projects to a form
     * @param user - the user who requests
     * @param rowIdentifier - the row identifier for the join table
     * @param projects - list of project ids that need to be added
     * @return - boolean - success status
     * @throws Exception
     */
    public boolean optProjectsIntoForm(final UserI user, final RowIdentifier rowIdentifier, final List<String> projects) throws Exception {
        CustomVariableFormAppliesTo formAppliesTo = customVariableFormAppliesToService.findByRowIdentifier(rowIdentifier);
        boolean savedAll = true;
        final String formStatus = formAppliesTo.getStatus();
        if (formAppliesTo == null) {
            throw new NotFoundException("Form with id: " + rowIdentifier + " not found");
        }
        boolean projectsExist = true;
        for (String project : projects) {
            XnatProjectdata projectdata = XnatProjectdata.getXnatProjectdatasById(project, user, false);
            if (projectdata == null) {
                projectsExist = false;
                break;
            }
        }
        if (!projectsExist) {
            throw new NotFoundException("All projects not found");
        }
        final String dataType = formAppliesTo.getCustomVariableAppliesTo().getDataType();
        final String protocol = formAppliesTo.getCustomVariableAppliesTo().getProtocol();
        final String visit = formAppliesTo.getCustomVariableAppliesTo().getVisit();
        final String subType = formAppliesTo.getCustomVariableAppliesTo().getSubType();
        UserOptionsPojo userOptionsPojo = new UserOptionsPojo(dataType, protocol, visit, subType);
        for (String project : projects) {
            List<CustomVariableAppliesTo> appliesTos = selectionService.filterByStatusFindByScopeEntityIdDataTypeProtocolVisitSubtype(
                    Scope.Project, project, dataType, protocol, visit, subType, null
            );
            if (appliesTos != null && appliesTos.size() > 0) {
                CustomVariableAppliesTo appliesTo = appliesTos.get(0);
                if (appliesTo.getCustomVariableFormAppliesTos().size() > 0) {
                    CustomVariableFormAppliesTo removalRow = appliesTo.getCustomVariableFormAppliesTos().get(0);
                    deleteSafely(removalRow.getRowIdentifier(), removalRow);
                }
            } else {
                CustomVariableAppliesTo customVariableAppliesTo = getCustomVariableAppliesTo(userOptionsPojo, project);
                objectSaver.saveOnlyAppliesToAndAssign(customVariableAppliesTo, formAppliesTo.getCustomVariableForm(), user, formStatus);
                savedAll = true;
            }
            createWorkFlowEntry(user, project, rowIdentifier.getFormId(), "Form Added");
        }
        return savedAll;
    }


    /**
     * Promote a form from a project wide status to a site wide status
     * @param user - the user who requests the operation
     * @param formAppliesToPoJos - list of the row identifier for the join table
     * @return - boolean - success status of the promote operation
     * @throws NotFoundException
     */
    public boolean promoteForm(final UserI user, List<FormAppliesToPoJo> formAppliesToPoJos) throws NotFoundException, IOException, CustomVariableNameClashException {
        if (formAppliesToPoJos != null && formAppliesToPoJos.size() == 1) {
            return promoteForm(user, formAppliesToPoJos.get(0).getIdCustomVariableFormAppliesTo());
        }
        int first = 1;
        Long formId = null;
        boolean sameFormIdCheckFailed = false;
        for (FormAppliesToPoJo formAppliesToPoJo : formAppliesToPoJos) {
            RowIdentifier rowId = RowIdentifier.Unmarshall(formAppliesToPoJo.getIdCustomVariableFormAppliesTo());
            if (first == 1) {
                formId = rowId.getFormId();
                first = 0;
            } else {
                if (rowId.getFormId() != formId) {
                    sameFormIdCheckFailed = true;
                }
            }
            if (sameFormIdCheckFailed) {
                break;
            }
        }
        if (sameFormIdCheckFailed) {
            return false;
        } else {
            first = 1;
            boolean firstPromoted = false;
            for (FormAppliesToPoJo formAppliesToPoJo : formAppliesToPoJos) {
                if (first == 1) {
                    firstPromoted = promoteForm(user, formAppliesToPoJo.getIdCustomVariableFormAppliesTo());
                    first = 0;
                } else {
                    if (firstPromoted) {
                        //Now just delete all the other form associations
                        RowIdentifier rowIdentifier = RowIdentifier.Unmarshall(formAppliesToPoJo.getIdCustomVariableFormAppliesTo());
                        CustomVariableFormAppliesTo customVariableFormAppliesTo = customVariableFormAppliesToService.findByRowIdentifier(rowIdentifier);
                        if (customVariableFormAppliesTo != null) {
                            createWorkFlowEntry(user, customVariableFormAppliesTo, rowIdentifier.getFormId(),"Form promoted and detached " );
                            deleteSafely(rowIdentifier, customVariableFormAppliesTo);
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Promte a form
     * @param user - user who requests
     * @param formAppliesToId - the row identifier for the join table
     * @return boolean - success status
     * @throws NotFoundException
     */

    public boolean promoteForm(final UserI user, final String formAppliesToId) throws NotFoundException, IOException, CustomVariableNameClashException {
        boolean promoted = false;
        RowIdentifier rowId = RowIdentifier.Unmarshall(formAppliesToId);
        CustomVariableFormAppliesTo customVariableFormAppliesTo = customVariableFormAppliesToService.findByRowIdentifier(rowId);
        if (customVariableFormAppliesTo != null) {
            List<CustomVariableFormAppliesTo> formsToCheckForClash = new ArrayList<CustomVariableFormAppliesTo>();
            UserOptionsPojo userOptionsPojo = UserOptionsPojo.Get(customVariableFormAppliesTo.getCustomVariableAppliesTo());
            List<CustomVariableFormAppliesTo> siteForms = customVariableFormAppliesToService.findAllFormsByExclusion(userOptionsPojo, rowId.getFormId());
            if (siteForms != null) {
                formsToCheckForClash.addAll(siteForms);
            }
            JsonNode proposed = customVariableFormAppliesTo.getCustomVariableForm().getFormIOJsonDefinition();
            checkDefinitionsForClashes(formsToCheckForClash, proposed);
            CustomVariableAppliesTo appliesTo = customVariableFormAppliesTo.getCustomVariableAppliesTo();
            if (appliesTo.getEntityId() != null) {
                //Replace the Scope from project to Site
                appliesTo = customVariableFormAppliesTo.getCustomVariableAppliesTo();
                appliesTo.setScope(Scope.Site);
                appliesTo.setEntityId(null);
                objectSaver.saveCustomVariableAppliesTo(appliesTo);
                createWorkFlowEntry(user, customVariableFormAppliesTo, rowId.getFormId(),"Form promoted  " );
                promoted = true;
            }
        } else {
            throw new NotFoundException("Form with id" + formAppliesToId + " not found");
        }
        return promoted;
    }

    /**
     * Opt out of a form
     * @param user - user who requests
     * @param formAppliesToId - the row identifier for the join table
     * @param projectIds - List of project ids that are opting out
     * @return - boolean - success status
     * @throws InsufficientPermissionsException
     * @throws IllegalArgumentException
     */

    public boolean optOutOfForm(final UserI user, final String formAppliesToId, final List<String> projectIds) throws InsufficientPermissionsException, IllegalArgumentException {
        boolean successStatus = false;
        try {
            RowIdentifier rowId = RowIdentifier.Unmarshall(formAppliesToId);
            long formId = rowId.getFormId();
            boolean userAuthorized = true;
            for (String projectId : projectIds ) {
                XnatProjectdata projectdata = XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
                userAuthorized = userAuthorized &&  customFormPermissionsService.isUserAdminOrDataManager(user) || customFormPermissionsService.isUserProjectOwner(user, projectdata.getId());
            }
            if (userAuthorized) {
                    CustomVariableFormAppliesTo customVariableFormAppliesTo = customVariableFormAppliesToService.findByRowIdentifier(rowId);
                    //Is the form already associated with the project? If so, just change the status
                    if (null != customVariableFormAppliesTo) {
                        CustomVariableAppliesTo appliesTo = customVariableFormAppliesTo.getCustomVariableAppliesTo();
                        if (null != appliesTo) {
                            if (appliesTo.getScope().equals(Scope.Site)) {
                                //Look for any project specific form with the same formId
                                for (String projectId : projectIds ) {
                                    CustomVariableFormAppliesTo formAppliesToProject = customVariableFormAppliesToService.findForProject(projectId, formId);
                                    if (formAppliesToProject != null) {
                                        if (formAppliesToProject.getStatus().equals(CustomFormsConstants.OPTED_OUT_STATUS_STRING)) {
                                            continue;
                                        }
                                        formAppliesToProject.setStatus(CustomFormsConstants.OPTED_OUT_STATUS_STRING);
                                        objectSaver.saveCustomVariableFormAppliesTo(formAppliesToProject);
                                        createWorkFlowEntry(user, customVariableFormAppliesTo, formId,"Form opted out" );
                                        continue;
                                    }else {
                                        List<CustomVariableAppliesTo> projectAppliesTos =  selectionService.filterByPossibleStatusFindByScopeEntityIdDataTypeProtocolVisitSubtype(
                                                Scope.Project, projectId, appliesTo.getDataType(), appliesTo.getProtocol(),
                                                appliesTo.getVisit(), appliesTo.getSubType(),null
                                        );
                                        if (projectAppliesTos != null && projectAppliesTos.size() > 0) {
                                            CustomVariableAppliesTo projectAppliesTo = projectAppliesTos.get(0);
                                            createNew(formId, user, projectAppliesTo, CustomFormsConstants.OPTED_OUT_STATUS_STRING);
                                        }else {
                                            //Create a new formSelection
                                            createNew(formId, user, UserOptionsPojo.Get(appliesTo), projectId, CustomFormsConstants.OPTED_OUT_STATUS_STRING);
                                        }
                                        createWorkFlowEntry(user, projectId, formId,"Form opted out" );
                                    }
                                }
                                return true;
                            } else {
                                // A form that is shared between projects
                                //Just remove the association between the form and the project
                                //Be safe - get the correct association
                                List<CustomVariableAppliesTo> projectAppliesTos =  selectionService.filterByPossibleStatusFindByScopeEntityIdDataTypeProtocolVisitSubtype(
                                        Scope.Project, null, appliesTo.getDataType(), appliesTo.getProtocol(),
                                        appliesTo.getVisit(), appliesTo.getSubType(),null
                                );
                                //If the form is associated to only one project. OptOut is not allowed
                                if (projectAppliesTos != null && projectAppliesTos.size() == 1) {
                                    throw new IllegalArgumentException("Opt Out operation is not allowed for a form associated with single project");
                                }
                                UserOptionsPojo optionsPojo = UserOptionsPojo.Get(appliesTo);
                                List<CustomVariableFormAppliesTo> customVariableFormAppliesTos = customVariableFormAppliesToService.findAllSpecificProjectForm(optionsPojo, projectIds, formId);
                                if (null != customVariableFormAppliesTos) {
                                    for (CustomVariableFormAppliesTo c: customVariableFormAppliesTos) {
                                        deleteSafely(c.getRowIdentifier(), c);
                                        createWorkFlowEntry(user, c, formId,"Form opted out and detached" );
                                    }
                                    setFormAsSiteWideDisabled(user,formId, optionsPojo);
                                    return true;
                                }
                            }
                        }
                    } else {
                        throw new IllegalArgumentException("Form identified by " + formAppliesToId + " not found");
                    }
                }else {
                    throw new InsufficientPermissionsException("User " + user.getUsername() + " does not have sufficient permissions to perform the Opt Out operation");
                }

        } catch (Exception e) {
            log.error("Could not find the form to opt out of", e);
            throw e;
        }
        return successStatus;
    }

    private void setFormAsSiteWideDisabled(final UserI user, final long formId, final UserOptionsPojo userOptionsPojo) {
        //Apply the form Site Wide and set its status to disabled
        List<CustomVariableFormAppliesTo> forms = customVariableFormAppliesToService.findByFormId(formId);
        if (forms == null || forms.size() < 1) {
            //Create a site wide form
            createNew(formId, user, userOptionsPojo, null, CustomFormsConstants.DISABLED_STATUS_STRING);
            EventDetails eventDetails = EventUtils.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, EventUtils.TYPE.PROCESS, EventUtils.MODIFY_VIA_WEB_SERVICE, "Site wide custom Form Associated for " + userOptionsPojo.getDataType() + " DISABLED", "");
            try {
                final PersistentWorkflowI workflow = PersistentWorkflowUtils.buildAdminWorkflow(user, "custom_form", Long.toString(formId), eventDetails);
                final EventMetaI eventInfo = workflow.buildEvent();
                WorkflowUtils.complete(workflow, eventInfo);
            }catch(Exception e) {
                log.error("Could not save workflow for custom form creation at the site level" , e);
            }
        }

    }

    private void deleteSafely(final RowIdentifier rowId, final CustomVariableFormAppliesTo customVariableFormAppliesTo) {
        try {
            customVariableFormAppliesToService.delete(customVariableFormAppliesTo);
        } catch (NonUniqueObjectException e) {
            formService.evict(customVariableFormAppliesTo.getCustomVariableForm());
            selectionService.evict(customVariableFormAppliesTo.getCustomVariableAppliesTo());
            customVariableFormAppliesToService.delete(customVariableFormAppliesTo);
        }
        //Is there any other form associated with the appliesTo?
        List<CustomVariableFormAppliesTo> appliesTos = customVariableFormAppliesToService.findByAppliesToId(rowId.getAppliesToId());
        if (appliesTos == null || appliesTos.size() < 1) {
            selectionService.delete(rowId.getAppliesToId());
        }
    }

    private void deleteForm(final RowIdentifier rowId) {
        List<CustomVariableFormAppliesTo> forms = customVariableFormAppliesToService.findByFormId(rowId.getFormId());
        if (forms == null || forms.size() < 1) {
            formService.delete(rowId.getFormId());
        }
    }


    private void deleteOptedOutSafely(final RowIdentifier rowId) {
        List<CustomVariableFormAppliesTo> optedOutForms = customVariableFormAppliesToService.findOptedOutByFormId(rowId.getFormId());
        if (optedOutForms != null && optedOutForms.size() > 0) {
            for (CustomVariableFormAppliesTo optedOut : optedOutForms) {
                RowIdentifier optedOutRow = optedOut.getRowIdentifier();
                deleteSafely(optedOutRow, optedOut);
            }
        }
    }

    /**
     * Save the form
     * @param user - user who requests to save the form
     * @param userOptionsPojo - the options selected by the user in the UI
     * @param entityIds - the list of project ids
     * @param jsonContent - the JSON representation of the form
     * @param existingFormPrimaryKey - the primary key of the form in case its an edit of a form
     * @throws IOException
     * @throws CustomVariableNameClashException
     */

    public String save(final UserI user,
                     final UserOptionsPojo userOptionsPojo,
                     final List<ComponentPojo> entityIds,
                     final String jsonContent,
                     final RowIdentifier existingFormPrimaryKey
    ) throws IOException, CustomVariableNameClashException {
        if (existingFormPrimaryKey == null) {
            throw new NullPointerException("Row Identifier can not be null");
        }
        List<CustomVariableFormAppliesTo> formsToCheckForClash = new ArrayList<CustomVariableFormAppliesTo>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode proposed = objectMapper.readTree(jsonContent);
        //Is it a new form?
        boolean newForm = existingFormPrimaryKey.getFormId() == -1;
        if (newForm) {
            final UUID formUUID = UUID.randomUUID();
            CustomVariableForm form = new CustomVariableForm();
            JsonNode containerizedNode = appendContainerToForm(formUUID, proposed);
            form.setFormIOJsonDefinition(containerizedNode);
            form.setzIndex(userOptionsPojo.getZIndex());
            form.setFormUuid(formUUID);
            if (entityIds != null && entityIds.size() > 0) {
                entityIds.forEach(entityId -> {
                    String projectId = toProjectId(entityId);
                    List<CustomVariableAppliesTo> customVariableAppliesTo = selectionService.filterByStatusFindByScopeEntityIdDataTypeProtocolVisitSubtype(
                            Scope.Project, projectId, userOptionsPojo.getDataType(), userOptionsPojo.getProtocol(),
                            userOptionsPojo.getVisit(), userOptionsPojo.getSubType(), null, true);
                    if (customVariableAppliesTo != null && customVariableAppliesTo.size() > 0) {
                        CustomVariableAppliesTo projectAppliesTo = customVariableAppliesTo.get(0);
                        objectSaver.saveOnlyFormAndAssign(projectAppliesTo, form, user, CustomFormsConstants.ENABLED_STATUS_STRING);
                    } else {
                        createNew(form, user, userOptionsPojo, projectId, CustomFormsConstants.ENABLED_STATUS_STRING);
                    }
                    try {
                        EventDetails eventDetails = EventUtils.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, EventUtils.TYPE.PROCESS, EventUtils.MODIFY_VIA_WEB_SERVICE, "Custom Form " + form.getFormUuid() + " created  for " + userOptionsPojo.getDataType() + " in " + projectId, "");
                        final PersistentWorkflowI workflow  = PersistentWorkflowUtils.getOrCreateWorkflowData(null, user, "custom_form",Long.toString(form.getId()), projectId, eventDetails);
                        final EventMetaI          eventMeta = workflow.buildEvent();
                        WorkflowUtils.complete(workflow, eventMeta);
                    }catch(Exception e) {
                        log.error("Could not save workflow", e);
                    }
                });
            } else {
                List<CustomVariableAppliesTo> customVariableAppliesTo = selectionService.filterByStatusFindByScopeEntityIdDataTypeProtocolVisitSubtype(
                        Scope.Site, null, userOptionsPojo.getDataType(), userOptionsPojo.getProtocol(),
                        userOptionsPojo.getVisit(), userOptionsPojo.getSubType(), null, true);
                if (customVariableAppliesTo != null && customVariableAppliesTo.size() > 0) {
                    CustomVariableAppliesTo projectAppliesTo = customVariableAppliesTo.get(0);
                    objectSaver.saveOnlyFormAndAssign(projectAppliesTo, form, user, CustomFormsConstants.ENABLED_STATUS_STRING);
                } else {
                    createNew(form, user, userOptionsPojo, null, CustomFormsConstants.ENABLED_STATUS_STRING);
                }
                EventDetails eventDetails = EventUtils.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, EventUtils.TYPE.PROCESS, EventUtils.MODIFY_VIA_WEB_SERVICE, "Site wide custom Form created for " + userOptionsPojo.getDataType() , "");
                try {
                    final PersistentWorkflowI workflow = PersistentWorkflowUtils.buildAdminWorkflow(user, "custom_form", Long.toString(form.getId()), eventDetails);
                    final EventMetaI eventInfo = workflow.buildEvent();
                    WorkflowUtils.complete(workflow, eventInfo);
                }catch(Exception e) {
                    log.error("Could not save workflow for custom form creation at the site level" , e);
                }
            }
            triggerEvent(form, userOptionsPojo.getDataType(), CREATE);
            return form.getFormUuid().toString();
        } else {
            //Not a new form
            CustomVariableForm form = formService.findById(existingFormPrimaryKey.getFormId());
            if (form != null) {
                form.setFormIOJsonDefinition(proposed);
                form.setzIndex(userOptionsPojo.getZIndex());
                if (entityIds != null && entityIds.size() > 0) {
                    entityIds.forEach(entityId -> {
                        String projectId = toProjectId(entityId);
                        List<CustomVariableAppliesTo> customVariableAppliesTo = selectionService.filterByStatusFindByScopeEntityIdDataTypeProtocolVisitSubtype(
                                Scope.Project, projectId, userOptionsPojo.getDataType(), userOptionsPojo.getProtocol(),
                                userOptionsPojo.getVisit(), userOptionsPojo.getSubType(), null, true);
                        if (customVariableAppliesTo != null && customVariableAppliesTo.size() > 0) {
                            CustomVariableAppliesTo projectAppliesTo = customVariableAppliesTo.get(0);
                            objectSaver.saveOnlyFormAndAssign(projectAppliesTo, form, user, CustomFormsConstants.ENABLED_STATUS_STRING);
                        } else {
                            createNew(form, user, userOptionsPojo, projectId, CustomFormsConstants.ENABLED_STATUS_STRING);
                        }
                        try {
                            EventDetails eventDetails = EventUtils.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, EventUtils.TYPE.PROCESS, EventUtils.MODIFY_VIA_WEB_SERVICE, "Custom Form " + form.getFormUuid().toString() + " edited  for " + userOptionsPojo.getDataType() + " in " + projectId, "");
                            final PersistentWorkflowI workflow  = PersistentWorkflowUtils.getOrCreateWorkflowData(null, user, "custom_form",Long.toString(form.getId()), projectId, eventDetails);
                            final EventMetaI          eventMeta = workflow.buildEvent();
                            WorkflowUtils.complete(workflow, eventMeta);
                        }catch(Exception e) {
                            log.error("Could not save workflow", e);
                        }
                    });
                } else {
                    List<CustomVariableAppliesTo> customVariableAppliesTo = selectionService.filterByStatusFindByScopeEntityIdDataTypeProtocolVisitSubtype(
                            Scope.Site, null, userOptionsPojo.getDataType(), userOptionsPojo.getProtocol(),
                            userOptionsPojo.getVisit(), userOptionsPojo.getSubType(), null, true);
                    if (customVariableAppliesTo != null && customVariableAppliesTo.size() > 0) {
                        CustomVariableAppliesTo appliesTo = customVariableAppliesTo.get(0);
                        objectSaver.saveOnlyFormAndAssign(appliesTo, form, user, CustomFormsConstants.ENABLED_STATUS_STRING);
                    } else {
                        createNew(form, user, userOptionsPojo, null, CustomFormsConstants.ENABLED_STATUS_STRING);
                    }
                    EventDetails eventDetails = EventUtils.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, EventUtils.TYPE.PROCESS, EventUtils.MODIFY_VIA_WEB_SERVICE, "Site wide custom Form " + form.getFormUuid().toString() +" edited. Associated with " + userOptionsPojo.getDataType() , "");
                    try {
                        final PersistentWorkflowI workflow = PersistentWorkflowUtils.buildAdminWorkflow(user, "custom_form", Long.toString(form.getId()), eventDetails);
                        final EventMetaI eventInfo = workflow.buildEvent();
                        WorkflowUtils.complete(workflow, eventInfo);
                    }catch(Exception e) {
                        log.error("Could not save workflow for custom form creation at the site level" , e);
                    }
                }
                triggerEvent(form, userOptionsPojo.getDataType(), UPDATE);
                return form.getFormUuid().toString();
            }
            return null;
        }
    }

    /**
     * Gets the Concatenated Custom Form for a xsiType
     * @param user: User who requests the forms
     * @param xsiType: The XsiType of the data
     * @param id: The Id of the data for which form is required
     * @param projectId: The project ID to which the data belongs
     * @param visitId: The Visit Id
     * @param subtype: The SubType of the data
    * @return String: The concatenated forms; returns null if no forms exist
     */
    @Nullable
    public String getCustomForm(final UserI user, final String xsiType, final String id, final String projectId,
                                final String visitId, final String subtype, final boolean appendPreviousNextButtons) throws Exception {
        String customFormJson = null;
        CustomFormFetcherI fetcher = defaultCustomFormManager.getCustomFormFetcherByTypeAnnotation(CustomFormsConstants.PROTOCOL_PLUGIN_AWARE);
        if (null == fetcher) {
            //Looks like this instance has no protocol plugin; resolve to default
            fetcher = defaultCustomFormManager.getCustomFormFetcherByTypeAnnotation(CustomFormsConstants.PROTOCOL_UNAWARE);
            if (fetcher != null) {
                customFormJson = fetcher.getCustomForm(user, xsiType, id, projectId, visitId, subtype, appendPreviousNextButtons);
            }
        } else {
            //This will be a protocol aware form
            customFormJson = fetcher.getCustomForm(user, xsiType, id, projectId, visitId, subtype, appendPreviousNextButtons);
        }
        return customFormJson;
    }

    public boolean checkCustomFormForData(final RowIdentifier rowId) throws Exception {
        CustomVariableFormAppliesTo formAppliesTo = customVariableFormAppliesToService.findByRowIdentifier(rowId);
        return dataLocateService.hasDataBeenAcquired(formAppliesTo);
    }

    private List<String> toProjectIds(final List<ComponentPojo> entityIds) {
        List<String> projectIds = new ArrayList<String>();
        if (entityIds == null || entityIds.isEmpty()) {
            return null;
        }
        entityIds.forEach(entityId -> {
            projectIds.add(toProjectId(entityId));
        });
        return projectIds;
    }

    private String toProjectId(final ComponentPojo entityId) {
        if (entityId == null) {
            return null;
        }
        String encodedProject = entityId.getValue();
        String[] tokens = encodedProject.split(CustomFormsConstants.DELIMITER);
        String projectId = null;
        if (tokens != null && tokens.length == 1) {
            projectId = tokens[0];
        } else {
            projectId = tokens[1];
        }
        return projectId;
    }

    private void checkDefinitionsForClashes(
            final List<CustomVariableFormAppliesTo> formAppliesTos,
            final JsonNode proposed
    ) throws IOException, CustomVariableNameClashException {
        if (formAppliesTos != null && formAppliesTos.size() > 0) {
            for (CustomVariableFormAppliesTo formAppliesTo : formAppliesTos) {
                CustomVariableForm form = formAppliesTo.getCustomVariableForm();
                if (null != form) {
                    JsonNode inDatabase = form.getFormIOJsonDefinition();
                    FormsIOJsonUtils formsIOJsonUtils = new FormsIOJsonUtils();
                    formsIOJsonUtils.checkForNameClash(inDatabase, proposed);
                }
            }
        }
    }

    private void createNew(final long formId, final UserI user,
                           final UserOptionsPojo userOptions,
                           final String entityId, final String status) {
        CustomVariableAppliesTo customVariableAppliesTo = getCustomVariableAppliesTo(userOptions, entityId);
        CustomVariableForm form = formService.findById(formId);
        objectSaver.saveOnlyAppliesToAndAssign(customVariableAppliesTo, form, user, status);
    }

    private boolean createNew(final long formId, final UserI user,
                           final CustomVariableAppliesTo customVariableAppliesTo,
                           final String status) {
        CustomVariableForm form = formService.findById(formId);
        return objectSaver.saveOnlyAppliesToAndAssign(customVariableAppliesTo, form, user, status);
    }


    public void createNew(final CustomVariableForm form, final UserI user,
                           final UserOptionsPojo userOptions,
                           final String entityId, final String status) {
        CustomVariableAppliesTo customVariableAppliesTo = getCustomVariableAppliesTo(userOptions, entityId);
        objectSaver.saveAll(customVariableAppliesTo, form, user, status);
    }


    private void createWorkFlowEntry(final UserI user, final CustomVariableFormAppliesTo customVariableFormAppliesTo, final long formId, final String reason ) {
        Scope scope = customVariableFormAppliesTo.getCustomVariableAppliesTo().getScope();
        String dataType =  CustomFormsConstants.CUSTOM_FORM_DATATYPE_FOR_WRKFLOW;
        String id = Long.toString(formId);
        EventDetails eventDetails = EventUtils.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, EventUtils.TYPE.PROCESS, EventUtils.MODIFY_VIA_WEB_SERVICE, reason, "FormId: " + formId);
        try {
            if(scope.equals(Scope.Project) ) {
                final String projectId = customVariableFormAppliesTo.getCustomVariableAppliesTo().getEntityId();
                final PersistentWorkflowI workflow  = PersistentWorkflowUtils.getOrCreateWorkflowData(null, user, dataType,id, projectId, eventDetails);
                final EventMetaI          eventMeta = workflow.buildEvent();
                WorkflowUtils.complete(workflow, eventMeta);
            }else {
                final PersistentWorkflowI workflow  = PersistentWorkflowUtils.buildAdminWorkflow( user, dataType,id, eventDetails);
                final EventMetaI          eventMeta = workflow.buildEvent();
                WorkflowUtils.complete(workflow, eventMeta);
            }
        }catch(Exception e) {
            log.error("Could not save workflow for custom form creation at the site level" , e);
        }
    }

    private void createWorkFlowEntry(final UserI user, final String projectId, final long formId, final String reason ) {
        String dataType =  CustomFormsConstants.CUSTOM_FORM_DATATYPE_FOR_WRKFLOW;
        String id = Long.toString(formId);
        EventDetails eventDetails = EventUtils.newEventInstance(EventUtils.CATEGORY.SIDE_ADMIN, EventUtils.TYPE.PROCESS, EventUtils.MODIFY_VIA_WEB_SERVICE, reason, "FormId: " + formId);
        try {
            if(projectId != null ) {
                final PersistentWorkflowI workflow  = PersistentWorkflowUtils.getOrCreateWorkflowData(null, user, dataType,id, projectId, eventDetails);
                final EventMetaI          eventMeta = workflow.buildEvent();
                WorkflowUtils.complete(workflow, eventMeta);
            }else {
                final PersistentWorkflowI workflow  = PersistentWorkflowUtils.buildAdminWorkflow( user, dataType,id, eventDetails);
                final EventMetaI          eventMeta = workflow.buildEvent();
                WorkflowUtils.complete(workflow, eventMeta);
            }
        }catch(Exception e) {
            log.error("Could not save workflow for custom form creation at the site level" , e);
        }
    }


    private CustomVariableAppliesTo getCustomVariableAppliesTo(final UserOptionsPojo userOptions,
                                                               final String entityId) {
        CustomVariableAppliesTo customVariableAppliesTo = new CustomVariableAppliesTo();
        customVariableAppliesTo.setDataType(userOptions.getDataType());
        if (null != userOptions.getProtocol()) customVariableAppliesTo.setProtocol(userOptions.getProtocol());
        if (null != userOptions.getVisit()) customVariableAppliesTo.setVisit(userOptions.getVisit());
        if (null != userOptions.getSubType()) customVariableAppliesTo.setSubType(userOptions.getSubType());
        if (null != userOptions.getScanType()) customVariableAppliesTo.setScanType(userOptions.getScanType());
        if (entityId != null) {
            customVariableAppliesTo.setScope(Scope.Project);
            customVariableAppliesTo.setEntityId(entityId);
        } else {
            customVariableAppliesTo.setScope(Scope.Site);
        }
        return customVariableAppliesTo;
    }

    private JsonNode appendContainerToForm(UUID formUUID, JsonNode formDefinition) throws NullPointerException {
        //Extract the title, display and setting from the form created
        //Add a container layer whose key is the UUID
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode containerizedParentNode = objectMapper.createObjectNode();
        JsonNode titleNode = formDefinition.path(TITLE_KEY);
        JsonNode displayType = formDefinition.path(DISPLAY_KEY);
        final String title = titleNode.asText();
        final String display = displayType.asText();
        final JsonNode settingsNode = formDefinition.path(SETTINGS_KEY);
        containerizedParentNode.put("display", display);
        containerizedParentNode.put("title", title);
        containerizedParentNode.set("settings", settingsNode);
        ArrayNode containerizedParentComponentsNode = objectMapper.createArrayNode();
        ObjectNode containerdNode = objectMapper.createObjectNode();
        containerdNode.put(COMPONENTS_KEY_FIELD, formUUID.toString());
        containerdNode.put(COMPONENTS_TYPE_FIELD, CONTAINER_KEY);
        containerdNode.put("input", true);
        containerdNode.put(LABEL_KEY, formUUID.toString());
        containerdNode.put("tableView", false);
        ArrayNode componentsArrayNode = objectMapper.createArrayNode();
        componentsArrayNode.add(getFormUUIDInfoInContainer(objectMapper, formUUID));
        JsonNode existingComponentNode = formDefinition.at("/" + COMPONENTS_KEY);
        if (existingComponentNode != null && existingComponentNode.isArray()) {
            for (final JsonNode eComp : existingComponentNode) {
                componentsArrayNode.add(eComp);
            }
        }
        containerdNode.set(COMPONENTS_KEY, componentsArrayNode);
        containerizedParentComponentsNode.add(containerdNode);
        containerizedParentNode.set(COMPONENTS_KEY, containerizedParentComponentsNode);
        return containerizedParentNode;
    }

    private JsonNode getFormUUIDInfoInContainer(final ObjectMapper objectMapper, final UUID formUUID) {
        ObjectNode formInfodNode = objectMapper.createObjectNode();
        formInfodNode.put(COMPONENTS_KEY_FIELD, COMPONENT_CONTENT_TYPE);
        String htmlText = "<p><span class=\"text-tiny\" style=\"font-family:Arial, Helvetica, sans-serif;\"><b>Form UUID:" + formUUID + "</b></span></p>";
        formInfodNode.put("html", htmlText);
        formInfodNode.put(COMPONENTS_TYPE_FIELD, COMPONENT_CONTENT_TYPE);
        formInfodNode.put("input", false);
        formInfodNode.put(LABEL_KEY, COMPONENT_CONTENT_TYPE);
        formInfodNode.put("tableView", false);
        formInfodNode.put("refreshOnChange", false);
        return formInfodNode;
    }

}
