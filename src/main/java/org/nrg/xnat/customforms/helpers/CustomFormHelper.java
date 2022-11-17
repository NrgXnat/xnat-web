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
package org.nrg.xnat.customforms.helpers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.constants.Scope;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.model.XnatFielddefinitiongroupFieldPossiblevalueI;
import org.nrg.xdat.model.XnatFielddefinitiongroupI;
import org.nrg.xdat.om.XnatFielddefinitiongroup;
import org.nrg.xdat.om.XnatFielddefinitiongroupField;
import org.nrg.xft.event.XftItemEvent;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.customforms.events.CustomFormEvent;
import org.nrg.xnat.customforms.exceptions.CustomVariableNameClashException;
import org.nrg.xnat.customforms.exceptions.InsufficientPermissionsException;
import org.nrg.xnat.customforms.pojo.*;
import org.nrg.xnat.customforms.pojo.formio.FormAppliesToPoJo;
import org.nrg.xnat.customforms.pojo.formio.FormByStatusPoJo;
import org.nrg.xnat.customforms.pojo.formio.PseudoConfiguration;
import org.nrg.xnat.customforms.pojo.formio.RowIdentifier;
import org.nrg.xnat.customforms.service.CustomFormManagerService;
import org.nrg.xnat.customforms.service.CustomFormPermissionsService;
import org.nrg.xnat.customforms.service.CustomVariableFormService;
import org.nrg.xnat.customforms.utils.CustomFormsConstants;
import org.nrg.xnat.customforms.utils.FormsIOJsonUtils;
import org.nrg.xnat.entities.CustomVariableAppliesTo;
import org.nrg.xnat.entities.CustomVariableForm;
import org.nrg.xnat.entities.CustomVariableFormAppliesTo;

import java.io.IOException;
import java.util.*;

import static org.nrg.xft.event.XftItemEventI.CREATE;
import static org.nrg.xft.event.XftItemEventI.DELETE;
import static org.nrg.xnat.customforms.events.CustomFormEventI.UPDATE;

/**
 * A Helper class to manage the Custom Form JSONs
 */

@Slf4j
public class CustomFormHelper {

    /**
     * Get all configured custom forms for a project which are Enabled.
     * @param user - the user requesting access to the form
     * @param projectId - the project id
     * @return - matched rows as serialized PseudoConfiguration
     * @throws InsufficientPermissionsException
     */

    public List<PseudoConfiguration> getAllCustomFormConfigurations(final UserI user, final String projectId) throws InsufficientPermissionsException {
        List<PseudoConfiguration> configurations = new ArrayList<>();
        CustomVariableFormService customVariableFormService = XDAT.getContextService().getBeanSafely(CustomVariableFormService.class);
        List<CustomVariableFormAppliesTo> filter = new ArrayList<CustomVariableFormAppliesTo>();

        if (customVariableFormService != null) {
            List<CustomVariableForm> customVariableForms = customVariableFormService.getAllEagerly();
            if (null != customVariableForms) {
                for (CustomVariableForm form : customVariableForms) {
                    List<CustomVariableFormAppliesTo> formAppliesTos = form.getCustomVariableFormAppliesTos();
                    if (projectId != null) {
                        List<CustomVariableFormAppliesTo> siteForms = new ArrayList<CustomVariableFormAppliesTo>();
                        List<CustomVariableFormAppliesTo> projectForms = new ArrayList<CustomVariableFormAppliesTo>();
                        //Add only Site and Project specific forms
                        for (CustomVariableFormAppliesTo formAppliesTo : formAppliesTos) {
                            CustomVariableAppliesTo appliesTo = formAppliesTo.getCustomVariableAppliesTo();
                            if ((appliesTo.getScope().equals(Scope.Site) && formAppliesTo.getStatus().equals(CustomFormsConstants.ENABLED_STATUS_STRING))) {
                                siteForms.add(formAppliesTo);
                            } else if (appliesTo.getScope().equals(Scope.Project) && appliesTo.getEntityId().equals(projectId)) {
                                projectForms.add(formAppliesTo);
                            }
                        }
                        FormsIOJsonUtils formsIOJsonUtils = new FormsIOJsonUtils();
                        filter.addAll(formsIOJsonUtils.removeSiteFormOptedOutByProject(siteForms, projectForms));
                        filter.addAll(projectForms);
                    } else {
                        filter.addAll(formAppliesTos);
                    }
                }
            }
        }
        //Group the forms by Status and for a given status collect the Project Information
        Hashtable<FormByStatusPoJo, List<CustomVariableFormAppliesTo>> formsByStatus = new Hashtable<FormByStatusPoJo, List<CustomVariableFormAppliesTo>>();
        for (CustomVariableFormAppliesTo c : filter) {
            FormByStatusPoJo f = new FormByStatusPoJo(c);
            if (formsByStatus.containsKey(f)) {
                formsByStatus.get(f).add(c);
            } else {
                List<CustomVariableFormAppliesTo> forms = new ArrayList<CustomVariableFormAppliesTo>();
                forms.add(c);
                formsByStatus.put(f, forms);
            }
        }

        Set<FormByStatusPoJo> keys = formsByStatus.keySet();
        for (FormByStatusPoJo k : keys) {
            PseudoConfiguration configuration = new PseudoConfiguration();
            configuration.setStatus(k.getStatus());
            configuration.setFormId(Long.toString(k.getFormId()));
            configuration.setFormUUID(k.getFormUUID());
            configuration.setFormZIndex(k.getZIndex());
            List<CustomVariableFormAppliesTo> formAppliesTos = formsByStatus.get(k);
            List<FormAppliesToPoJo> formAppliesToPoJos = new ArrayList<FormAppliesToPoJo>();
            boolean formJsonContentHasBeenSet = false;
            for (CustomVariableFormAppliesTo formAppliesTo : formAppliesTos) {
                if (!formJsonContentHasBeenSet) {
                    configuration.setPath(formAppliesTo.getCustomVariableAppliesTo().pathAsString());
                    configuration.setScope(formAppliesTo.getCustomVariableAppliesTo().getScope());
                    configuration.setDoProjectsShareForm(formAppliesTo.doProjectsShareForm());
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jNode = formAppliesTo.getCustomVariableForm().getFormIOJsonDefinition();
                        String pretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jNode);
                        configuration.setContents(pretty);
                    } catch (JsonProcessingException jpe) {
                        log.debug("Could not process json", jpe);
                    } finally {
                        formJsonContentHasBeenSet = true;
                    }
                }
                FormAppliesToPoJo formAppliesToPoJo = new FormAppliesToPoJo(formAppliesTo);
                formAppliesToPoJos.add(formAppliesToPoJo);
            }
            configuration.setAppliesToList(formAppliesToPoJos);
            configurations.add(configuration);
        }
        return configurations;
    }


    /**
     * Saves form assocation configuration passed through ClientPojo object.
     *
     * @param clientPojo - JSON into POJO which contains mulitple paths to which the form JSON is to be saved
     * @param user       - User
     */

    public String save(final ClientPojo clientPojo, final UserI user) throws Exception {
        String formId = null;
        String builderJson = clientPojo.getBuilder();
        SubmissionPojo data = clientPojo.getSubmission().getData();
        String datatype = data.getXnatDatatype().getValue();
        String siteWideStr = data.getIsThisASiteWideConfiguration();
        String existingFormPrinaryKey = data.getIdCustomVariableFormAppliesTo();
        int zIndex = data.getzIndex();
        RowIdentifier rowId = RowIdentifier.Unmarshall(existingFormPrinaryKey);
        if (null == rowId) {
            throw new IllegalArgumentException("Incorrect row id received");
        }
        boolean isSiteWideForm = siteWideStr.equalsIgnoreCase("YES");
        List<ComponentPojo> protocols = data.getXnatProtocol();
        List<ComponentPojo> visits = data.getXnatVisit();
        List<ComponentPojo> subTypes = data.getXnatSubtype();
        List<ComponentPojo> projects = data.getXnatProject();

        if (protocols == null) {
            protocols = new ArrayList<>();
        }
        if (visits == null) {
            visits = new ArrayList<>();
        }
        if (subTypes == null) {
            subTypes = new ArrayList<>();
        }
        if (protocols.size() > 0) {
            //Is a protocol specific customForm for the datatype
            if (subTypes.size() > 0) {
                for (ComponentPojo s : subTypes) {
                    //Encoded as: PROTOCOL_NAME : VISIT_ID : SUBTYPE_NAME
                    String encodedSubtype = s.getValue();
                    String[] tokens = encodedSubtype.split(CustomFormsConstants.DELIMITER);
                    String protocol = tokens[0];
                    String visit_name = tokens[1];
                    String subType = tokens[2];
                    UserOptionsPojo userOptionsPojo = new UserOptionsPojo(datatype, protocol, visit_name, subType);
                    userOptionsPojo.setZIndex(zIndex);
                    if (isSiteWideForm) {
                        //Save for the site:  datatype, protocol, visit, subtype
                        formId = saveConfiguration(user, userOptionsPojo, builderJson, null, rowId);
                    } else {
                        //Is for specific projects
                        if (null != projects && projects.size() > 0) {
                            for (ComponentPojo proj : projects) {
                                //Encoded as: PROTOCOL_NAME : PROJECT_ID
                                String encodedProject = proj.getValue();
                                tokens = encodedProject.split(CustomFormsConstants.DELIMITER);
                                String protocolName = tokens[0];
                                String projectId = tokens[1];
                                if (protocolName.equals(protocol)) {
                                    //Save for this datatype, project, protocol, visit, subtype
                                    formId = saveConfiguration(user, userOptionsPojo, builderJson, projects, rowId);
                                }
                            }
                        }
                    }
                }
            } else {
                //Only Protocol and Visit
                if (visits.size() > 0) {
                    for (ComponentPojo v : visits) {
                        //PROTOCOL_NAME:VISIT_ID
                        String encodedVisit = v.getValue();
                        String[] tokens = encodedVisit.split(CustomFormsConstants.DELIMITER);
                        String protocol = tokens[0];
                        String visitName = tokens[1];
                        UserOptionsPojo userOptionsPojo = new UserOptionsPojo(datatype, protocol, visitName, null);
                        userOptionsPojo.setZIndex(zIndex);
                        if (isSiteWideForm) {
                            //Save for the site:  datatype, protocol, visit
                            formId = saveConfiguration(user, userOptionsPojo, builderJson, null, rowId);
                        } else {
                            //Is for specific projects
                            if (null != projects && projects.size() > 0) {
                                for (ComponentPojo proj : projects) {
                                    //Encoded as: PROTOCOL_NAME : PROJECT_ID
                                    String encodedProject = proj.getValue();
                                    tokens = encodedProject.split(CustomFormsConstants.DELIMITER);
                                    String protocolName = tokens[0];
                                    String projectId = tokens[1];
                                    if (protocolName.equals(protocol)) {
                                        //Save for this datatype, project, protocol, visit
                                        formId = saveConfiguration(user, userOptionsPojo, builderJson, projects, rowId);
                                    }

                                }
                            }
                        }
                    }
                } else {
                    //Only Protocol provided - all visits under this datatype use the same form
                    for (ComponentPojo p : protocols) {
                        //This has the protocolId and we will be using the protocolname
                        //String protocol = p.getValue();
                        String protocol = p.getLabel();
                        UserOptionsPojo userOptionsPojo = new UserOptionsPojo(datatype, protocol, null, null);
                        userOptionsPojo.setZIndex(zIndex);
                        if (isSiteWideForm) {
                            //Save for the site:  datatype, protocol, visit
                            formId = saveConfiguration(user, userOptionsPojo, builderJson, null, rowId);
                        } else {
                            if (null != projects && projects.size() > 0) {
                                for (ComponentPojo proj : projects) {
                                    //Encoded as: PROTOCOL_NAME : PROJECT_ID
                                    String encodedProject = proj.getValue();
                                    String[] tokens = encodedProject.split(CustomFormsConstants.DELIMITER);
                                    String protocolName = tokens[0];
                                    String projectId = tokens[1];
                                    if (protocolName.equals(protocol)) {
                                        //Save for this datatype, project, protocol, visit
                                        formId = saveConfiguration(user, userOptionsPojo, builderJson, projects, rowId);
                                    }

                                }
                            }
                        }

                    }
                }
            }
        } else {
            //Is a datatype specific customForm
            UserOptionsPojo userOptionsPojo = new UserOptionsPojo(datatype, null, null, null);
            userOptionsPojo.setZIndex(zIndex);
            if (isSiteWideForm) {
                //Save site wide for datatype
                formId = saveConfiguration(user, userOptionsPojo, builderJson, null, rowId);
            } else {
                //Save for project
                if (null != projects && projects.size() > 0) {
                    formId = saveConfiguration(user, userOptionsPojo, builderJson, projects, rowId);
                }
            }
        }
        return formId;
    }

    /**
     * Generates the FormsIO components array from XnatFielddefinitiongroup
     *
     * @param fieldDefinitionGroup - The legacy custom variable
     * @return
     */

    public ArrayNode buildComponentsForFormsIO(final XnatFielddefinitiongroup fieldDefinitionGroup, final UUID formUUID) {
        String dataType = fieldDefinitionGroup.getDataType();
        String description = fieldDefinitionGroup.getDescription();
        String field_definition_id = fieldDefinitionGroup.getId();
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode componentsArrayNode = objectMapper.createArrayNode();
        //Create a HTML Title Node
        ObjectNode componentTitleNode = objectMapper.createObjectNode();
        //Create a container node to be able to separate the Custom Variable Data
        ObjectNode containerNode = objectMapper.createObjectNode();
        containerNode.put("key", formUUID.toString());
        containerNode.put("type", "container");
        containerNode.put("input", true);
        containerNode.put("label", formUUID.toString());
        containerNode.put("hideLabel", true);
        containerNode.put("tableView", false);
        ArrayNode containerComponentsArrayNode = objectMapper.createArrayNode();
        if (!field_definition_id.equalsIgnoreCase("DEFAULT")) {
            componentTitleNode.put("input", false);
            componentTitleNode.put("html", "<p><span class=\"text-tiny\" style=\"font-family:Arial, Helvetica, sans-serif;\"><b> Form UUID: " + formUUID.toString() + "</b></span></p>");
            componentTitleNode.put("type", "content");
            if (null != description)
                componentTitleNode.put("description", description);
            else {
                componentTitleNode.put("description", field_definition_id);
            }
            containerComponentsArrayNode.add(componentTitleNode);
        }

        ArrayList<XnatFielddefinitiongroupField> fields = fieldDefinitionGroup.getFields_field();
        for (XnatFielddefinitiongroupField f : fields) {
            String fieldLabel = f.getName();
            //String, Integer, Float, Boolean, Date
            String fieldType = f.getDatatype();
            Boolean isRequired = f.getRequired();
            List<XnatFielddefinitiongroupFieldPossiblevalueI> possibleValues = f.getPossiblevalues_possiblevalue();
            ObjectNode fieldNode = buildFieldNode(fieldLabel, fieldType, isRequired, f.getXmlpath(), possibleValues);
            containerComponentsArrayNode.add(fieldNode);
        }
        containerNode.set("components", containerComponentsArrayNode);
        componentsArrayNode.add(containerNode);
        return componentsArrayNode;
    }


    /**
     * Given a components node, builds the FORMSIO JSON
     *
     * @param components - the components node
     * @return FormsIO Json as string
     * @throws JsonProcessingException
     */

    public String convertToFormJson(ArrayNode components) throws JsonProcessingException {
        String formIoJson = null;
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode parentNode = objectMapper.createObjectNode();
        parentNode.put("display", "form");
        parentNode.put("title", "");
        ObjectNode settingsNode = objectMapper.createObjectNode();
        parentNode.set("settings", settingsNode);
        parentNode.set("components", components);
        formIoJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(parentNode);
        return formIoJson;
    }



    /**
     * A helper to convert the legacy Field Definition Groups to FORMSIO JSONs
     *
     * @param fieldDefinitionGroup - FieldDefinitionGroup
     * @return - String: If found, Custom Field Definition Group represented as FormIO Json or null
     */

    public String convertToFormJson( XnatFielddefinitiongroupI fieldDefinitionGroup, final UUID formUUID) throws JsonProcessingException {
        String formIoJson = null;
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode parentNode = objectMapper.createObjectNode();
        parentNode.put("display", "form");
        parentNode.put("title", fieldDefinitionGroup.getId());
        ObjectNode settingsNode = objectMapper.createObjectNode();
        parentNode.set("settings", settingsNode);
        ArrayNode componentsArrayNode = buildComponentsForFormsIO((XnatFielddefinitiongroup)fieldDefinitionGroup,formUUID);
        parentNode.set("components", componentsArrayNode);
        formIoJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(parentNode);
        return formIoJson;
    }


    private ObjectNode buildFieldNode(final String fieldLabel, final String fieldType, final Boolean isRequired, final String fieldXmlPath, final List<XnatFielddefinitiongroupFieldPossiblevalueI> possibleValues) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode fieldNode = objectMapper.createObjectNode();
        fieldNode.put("label", fieldLabel);
        fieldNode.put("inline", false);
        fieldNode.put("tableView", false);
        fieldNode.put("optionsLabelPosition", "right");
        fieldNode.put("selectThreshold", 0.3);
        if (fieldType.equalsIgnoreCase("DATE")) {
            String preferredDateFormat = "MM/dd/yyyy";
            String dateFormat = (String) XDAT.getSiteConfigPreferences().get("UI.date-format");
            if (null != dateFormat) {
                preferredDateFormat = dateFormat;
            }
            fieldNode.put("format", preferredDateFormat);
            fieldNode.put("type", "xnatdate");
         } else {
            fieldNode.put("widget", "choicejs");
            if (null != possibleValues && possibleValues.size() > 0) {
                fieldNode.put("widget", "choicesjs");
                fieldNode.put("type", "select");
                ObjectNode fieldDataValuesNode = objectMapper.createObjectNode();
                ArrayNode dataValuesArrayNode = objectMapper.createArrayNode();
                for (XnatFielddefinitiongroupFieldPossiblevalueI p : possibleValues) {
                    ObjectNode valueNode = objectMapper.createObjectNode();
                    valueNode.put("label", p.getDisplay() == null ? p.getPossiblevalue() : p.getDisplay());
                    valueNode.put("value", p.getPossiblevalue());
                    dataValuesArrayNode.add(valueNode);
                }
                fieldDataValuesNode.set("values", dataValuesArrayNode);
                fieldNode.set("data", fieldDataValuesNode);
                fieldNode.put("searchEnabled", false);
                fieldNode.put("type", "select");
            } else {
                if (fieldType.equalsIgnoreCase("INTEGER") || fieldType.equalsIgnoreCase("FLOAT")) {
                    fieldNode.put("mask", false);
                    fieldNode.put("tableView", false);
                    fieldNode.put("delimiter", false);
                    //Setting this to true, sets the decimal precision to 2 by FormIO
                    //fieldNode.put("requireDecimal", fieldType.equalsIgnoreCase("FLOAT"));
                    fieldNode.put("inputFormat", "plain");
                    if (fieldType.equalsIgnoreCase("INTEGER")) {
                        fieldNode.put("decimalLimit", 0);
                        fieldNode.put("requireDecimal", false);
                        fieldNode.put("validate", "{integer: true}");
                        fieldNode.put("type", "xnatInteger");
                    }else {
                        fieldNode.put("type", "xnatFloat");
                    }
                    fieldNode.put("inputFormat", "plain");
                    fieldNode.put("truncateMultipleSpaces", false);
                    fieldNode.put("input", true);
                } else if (fieldType.equalsIgnoreCase("BOOLEAN")) {
                    fieldNode.put("inline", true);
                    ArrayNode dataValuesArrayNode = objectMapper.createArrayNode();
                    ObjectNode valueNode = objectMapper.createObjectNode();
                    valueNode.put("label", "True");
                    valueNode.put("value", "true");
                    dataValuesArrayNode.add(valueNode);
                    valueNode = objectMapper.createObjectNode();
                    valueNode.put("label", "False");
                    valueNode.put("value", "false");
                    dataValuesArrayNode.add(valueNode);
                    fieldNode.set("values", dataValuesArrayNode);
                    fieldNode.put("dataType", "boolean");
                    fieldNode.put("type", "radio");
                } else if (fieldType.equalsIgnoreCase("STRING")) {
                    fieldNode.put("type", "textfield");
                }
            }
        }
        ObjectNode fieldValidateNode = objectMapper.createObjectNode();
        fieldValidateNode.put("required", isRequired);
        fieldValidateNode.put("onlyAvailableItems", true);
        fieldNode.set("validate", fieldValidateNode);
        fieldNode.put("input", true);
        fieldNode.put("key", fieldLabel.toLowerCase());
        return fieldNode;
    }

    public String saveConfiguration(final UserI user, final UserOptionsPojo userOptionsPojo, final String jsonContent, final List<ComponentPojo> projects, final RowIdentifier existingFormPrikaryKey)
            throws  IOException, CustomVariableNameClashException {
       String formUUID = null;
        CustomFormManagerService customVariableFormMgr = XDAT.getContextService().getBeanSafely(CustomFormManagerService.class);
        if (customVariableFormMgr != null) {
            formUUID = customVariableFormMgr.save(user, userOptionsPojo, projects, jsonContent, existingFormPrikaryKey);
        }
        return formUUID;
    }


    /**
     * Parses a configuration and converts the FormsIO Fields to XNAT Custom Fields
     *
     * @param c        - CustomVariableAppliesTo from which the formsIO fields are to be extracted
     * @param dataType - XNAT Datatype for which the custom fields are to be created
     * @return List of FormIOJsonToXnatCustomField
     */
    public List<FormIOJsonToXnatCustomField> getFormObj(CustomVariableAppliesTo c, String dataType) throws JsonProcessingException, IOException {
        // Convert the configuration into a new FormJson Pojo
        List<FormIOJsonToXnatCustomField> formsIOJsonToXnatCustomFields = new ArrayList<>();
        List<CustomVariableFormAppliesTo> customFormSelections = c.getCustomVariableFormAppliesTos();
        for (CustomVariableFormAppliesTo customVariableFormAppliesTo : customFormSelections) {
            if (customVariableFormAppliesTo.getStatus().equals(CustomFormsConstants.ENABLED_STATUS_STRING)) {
                formsIOJsonToXnatCustomFields.addAll(getFormObj(customVariableFormAppliesTo.getCustomVariableForm(), dataType));
            }
        }
        return formsIOJsonToXnatCustomFields;
    }

    public List<FormIOJsonToXnatCustomField> getFormObj(CustomVariableForm form, String dataType) {
        // Convert the configuration into a new FormJson Pojo
        List<FormIOJsonToXnatCustomField> formIOJsonToXnatCustomFields = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY));
        final JsonNode contents = form.getFormIOJsonDefinition();
        JsonNode components = contents.get("components");
        if (components != null) {
            if (components.isArray()) {
                ArrayNode componentArray = (ArrayNode) components;
                traverse(form.getFormUuid(), componentArray, formIOJsonToXnatCustomFields, dataType);
            }
        }
        return formIOJsonToXnatCustomFields;
    }

    public List<FormIOJsonToXnatCustomField> appendFormUUIDToKey(final List<FormIOJsonToXnatCustomField> fields) {
        List<FormIOJsonToXnatCustomField> appendedFields = new ArrayList<>();
        if (fields == null || fields.isEmpty()) {
            return appendedFields;
        }
        for (FormIOJsonToXnatCustomField f : fields) {
            FormIOJsonToXnatCustomField field = appendFormUUID(f);
            appendedFields.add(field);
        }
        return appendedFields;
    }

    /**
     * Recursively traverse the FormIO JSON structure to find input components and skip layout components
     * Based off of FormIO Utils eachComponent method:
     * https://github.com/formio/formio-utils/blob/master/src/index.js
     *
     * @param components                   - FormIO components JSON
     * @param formIOJsonToXnatCustomFields - Converted input components are added to this list
     * @param dataType                     - XNAT Datatype for which the custom fields are to be created
     */
    private void traverse(final UUID formUUID, JsonNode components, List<FormIOJsonToXnatCustomField> formIOJsonToXnatCustomFields, String dataType) {

        boolean isArray = components.isArray();
        boolean hasColumns = components.has("columns") && components.get("columns").isArray();
        boolean hasRows = components.has("rows") && components.get("rows").isArray();
        boolean hasComponents = components.has("components") && components.get("components").isArray();

        if (!isArray && !hasColumns && !hasRows && !hasComponents) {
            FormIOJsonToXnatCustomField f = getFormsIOJsonToXnatCustomField(formUUID, components, dataType);
            if (null != f) {
                formIOJsonToXnatCustomFields.add(f);
            }
            return;
        }

        if (hasColumns) {
            components.get("columns").forEach(column -> traverse(formUUID, column.get("components"), formIOJsonToXnatCustomFields, dataType));
        }

        if (hasRows) {
            components.get("rows").forEach(row -> {
                row.forEach(rowComponent -> traverse(formUUID, rowComponent.get("components"), formIOJsonToXnatCustomFields, dataType));
            });
        }

        if (hasComponents) {
            traverse(formUUID, components.get("components"), formIOJsonToXnatCustomFields, dataType);
        }

        if (isArray) {
            components.forEach(component -> traverse(formUUID, component, formIOJsonToXnatCustomFields, dataType));
        }
    }

    private FormIOJsonToXnatCustomField getFormsIOJsonToXnatCustomField(final UUID formUUID, JsonNode compNode, String dataType) {
        FormIOJsonToXnatCustomField formIOJsonToXnatCustomField = null;
        if (compNode.has("input")) {
            boolean isInput = compNode.get("input").asBoolean();
            if (isInput) {
                String key = compNode.get("key").asText();
                String label = compNode.get("label").asText();
                String type = compNode.get("type").asText();
                String fieldName = key;
                formIOJsonToXnatCustomField = new FormIOJsonToXnatCustomField(formUUID, label, key, fieldName, type);
            }
        }
        return formIOJsonToXnatCustomField;
    }

    private FormIOJsonToXnatCustomField appendFormUUID(final FormIOJsonToXnatCustomField f) {
        FormIOJsonToXnatCustomField newF = new FormIOJsonToXnatCustomField(f.getFormUUID(), f.getLabel(), f.getFormUUID() + CustomFormsConstants.DOT_SEPARATOR + f.getKey(), f.getFieldName(), f.getType());
        return newF;
    }


}
