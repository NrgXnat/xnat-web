/*
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2021, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * @author: Mohana Ramaratnam (mohanaramaratnam@flywheel.io)
 */
package org.nrg.xnat.customforms.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import groovy.util.logging.Slf4j;
import org.nrg.xnat.customforms.exceptions.CustomVariableNameClashException;
import org.nrg.xnat.entities.CustomVariableAppliesTo;
import org.nrg.xnat.entities.CustomVariableForm;
import org.nrg.xnat.entities.CustomVariableFormAppliesTo;

import java.io.IOException;
import java.util.*;

import static org.nrg.xnat.customforms.utils.CustomFormsConstants.*;


/**
 * A convenience class to compare two FormsIO JSON's to check if there are any name clashes
 */

@Slf4j
public class FormsIOJsonUtils {


    /**
     * Check if there are any name clashes between two formsio JSON's.
     * The parameter proposed is compared to existing.
     *
     * @param existing - First of the two JSON's being compared
     * @param proposed - Second of the two JSON's being compared
     * @throws CustomVariableNameClashException - which contains a list of fields identified as clashing
     */

    public void checkForNameClash(JsonNode existing, JsonNode proposed)
            throws JsonProcessingException, IOException, NullPointerException, CustomVariableNameClashException {
        List<String> nameClashes = new ArrayList<String>();
        if (existing == null) throw new NullPointerException();
        if (proposed == null) throw new NullPointerException();
        List<JsonNode> proposedComponents = proposed.findValues(COMPONENTS_KEY);
        for (JsonNode componentNode : proposedComponents) {
            if (!componentNode.isMissingNode()) {
                try {
                    checkForNameClashPerNode(existing, componentNode);
                } catch (CustomVariableNameClashException c) {
                    nameClashes.addAll(c.getClashes());
                }
            }
        }
        if (nameClashes.size() > 0) {
            throw new CustomVariableNameClashException(nameClashes);
        }
    }

    /**
     * Convenience method to filter site wide forms which a project has opted out of
     * @param siteWideSelections - list of site wide forms
     * @param projectSpecificSelections - list of project specific forms
     * @return
     */

    public List<CustomVariableFormAppliesTo> removeSiteFormsOptedOutByProject(final List<CustomVariableAppliesTo> siteWideSelections, final List<CustomVariableAppliesTo> projectSpecificSelections) {
        List<CustomVariableFormAppliesTo> filteredRows = new ArrayList<CustomVariableFormAppliesTo>();
        if ((siteWideSelections == null || siteWideSelections.isEmpty()) && (projectSpecificSelections == null || projectSpecificSelections.isEmpty())) {
            return filteredRows;
        }
        if (projectSpecificSelections == null || projectSpecificSelections.isEmpty()) {
            for (CustomVariableAppliesTo c : siteWideSelections) {
                filteredRows.addAll(c.getCustomVariableFormAppliesTos());
            }
            return filteredRows;
        }
        for (CustomVariableAppliesTo site : siteWideSelections) {
            CustomVariableAppliesTo siteClone = site;
            for (CustomVariableFormAppliesTo c : site.getCustomVariableFormAppliesTos()) {
                long formId = c.getCustomVariableForm().getId();
                boolean optedOut = hasProjectOptedOutOfForm(formId, projectSpecificSelections);
                if (!optedOut) {
                    if (!filteredRows.contains(c)) {
                        filteredRows.add(c);
                    }
                }
            }
        }
        return filteredRows;
    }


    /**
     * Convenience method to filter site wide forms which a project has opted out of
     * @param siteWideSelections - list of site wide forms
     * @param projectSpecificSelections - list of project specific forms
     * @return
     */


    public List<CustomVariableFormAppliesTo> removeSiteFormOptedOutByProject(final List<CustomVariableFormAppliesTo> siteWideSelections, final List<CustomVariableFormAppliesTo> projectSpecificSelections) {
        List<CustomVariableFormAppliesTo> filteredRows = new ArrayList<CustomVariableFormAppliesTo>();
        if (siteWideSelections == null || siteWideSelections.size() < 1) {
            return filteredRows;
        }
        if (projectSpecificSelections == null || projectSpecificSelections.size() < 1) {
            filteredRows.addAll(siteWideSelections);
            return filteredRows;
        }
        for (CustomVariableFormAppliesTo c : siteWideSelections) {
            long formId = c.getCustomVariableForm().getId();
            boolean optedOut = hasProjectOptedOutOfSiteForm(formId, projectSpecificSelections);
            if (!optedOut) {
                if (!filteredRows.contains(c)) {
                    filteredRows.add(c);
                }
            }
        }
        return filteredRows;
    }

    private boolean hasProjectOptedOutOfForm(long formId, List<CustomVariableAppliesTo> projectSpecificSelections) {
        boolean optedOut = false;
        for (CustomVariableAppliesTo c : projectSpecificSelections) {
            for (CustomVariableFormAppliesTo cs : c.getCustomVariableFormAppliesTos()) {
                String status = cs.getStatus();
                if (cs.getCustomVariableForm().getId() == formId && status != null && status.equals(CustomFormsConstants.OPTED_OUT_STATUS_STRING)) {
                    optedOut = true;
                    break;
                }
            }
            if (optedOut) {
                break;
            }
        }
        return optedOut;
    }

    private boolean hasProjectOptedOutOfSiteForm(long formId, List<CustomVariableFormAppliesTo> projectSpecificSelections) {
        boolean optedOut = false;
        for (CustomVariableFormAppliesTo cs : projectSpecificSelections) {
            String status = cs.getStatus();
            if (cs.getCustomVariableForm().getId() == formId && status != null && status.equals(CustomFormsConstants.OPTED_OUT_STATUS_STRING)) {
                optedOut = true;
                break;
            }
        }
        return optedOut;
    }


    /**
     * Concateate forms
     * @param forms - List of forms to concatenate
     * @param title - the title of the concatenated form
     * @param onlyEnabled - boolean - if only enabled forms are to be concatenated
     * @return - String - the concatenated form JSON
     * @throws Exception
     */
    public String concatenate(final List<CustomVariableFormAppliesTo> forms, final String title, final boolean onlyEnabled, final boolean appendPreviousNextButtons) throws Exception {
        String concatenatedFormsJson = "{}";
        if (forms == null || forms.size() < 1) {
            return concatenatedFormsJson;
        }
        Comparator<CustomVariableFormAppliesTo> comparator = Comparator.comparing(h -> h.getCustomVariableForm().getzIndex());
        forms.sort(comparator.reversed());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode concatenatedNode = objectMapper.createObjectNode();
        concatenatedNode.put("title", title);
        concatenatedNode.put("display", "wizard");
        concatenatedNode.put("settings", "{}");
        ArrayNode rootComponentsNode = objectMapper.createArrayNode();
        int index = 0;
        LinkedHashMap<ObjectNode, JsonNode> wizardPages = new LinkedHashMap<ObjectNode, JsonNode>();
        for (CustomVariableFormAppliesTo vfs : forms) {
                if (!onlyEnabled || (onlyEnabled && vfs.getStatus().equals(CustomFormsConstants.ENABLED_STATUS_STRING))) {
                    index = index + 1;
                    CustomVariableForm form = vfs.getCustomVariableForm();
                    JsonNode formDefinition = form.getFormIOJsonDefinition();
                    JsonNode titleNode = formDefinition.path(TITLE_KEY);
                    JsonNode displayType = formDefinition.path(DISPLAY_KEY);
                    String pageTitle = "Custom Fields" + index;
                    if (titleNode != null) {
                        pageTitle = titleNode.asText();
                    }
                    JsonNode componentNode = formDefinition.at("/" + COMPONENTS_KEY);
                    if (displayType.asText().equals("wizard")) {
                        if (componentNode != null && componentNode.isArray()) {
                            for (final JsonNode comp : componentNode) {
                                JsonNode key = comp.get(COMPONENTS_KEY_FIELD);
                                JsonNode type = comp.get(COMPONENTS_TYPE_FIELD);
                                if (type.asText().equals(COMPONENT_CONTENT_TYPE)) {
                                    continue;
                                }
                                JsonNode wizardPageTitle = comp.get(TITLE_KEY);
                                JsonNode wizardPageLabel = comp.get(TITLE_KEY);
                                String panelTitle = pageTitle;
                                if (wizardPageTitle != null) {
                                    panelTitle += " - " + wizardPageTitle.asText();
                                }else if (wizardPageLabel != null) {
                                    panelTitle += " - " + wizardPageLabel.asText();
                                }

                                if (type.asText().equals(COMPONENT_PANEL_TYPE)) {
                                    ObjectNode panelNode   = getObjectnode(panelTitle, ++index);
                                    JsonNode compNodes = comp.get(COMPONENTS_KEY);
                                    wizardPages.put(panelNode, compNodes);
                                }
                            }
                        }
                    }else {
                        ObjectNode panelNode   = getObjectnode(pageTitle, index);
                        wizardPages.put(panelNode, componentNode);
                    }
                }
        }
        Set<ObjectNode> wizardPageSet = wizardPages.keySet();
        ObjectNode[] wizardPageArray = wizardPageSet.toArray(new ObjectNode[wizardPageSet.size()]);
        index = 0;
        int totalPages = wizardPageArray.length;
        for (ObjectNode pageNode : wizardPageArray) {
            JsonNode components = wizardPages.get(pageNode);
            ArrayNode newComponentNode = objectMapper.createArrayNode();
            if (components.isArray()) {
                newComponentNode.addAll((ArrayNode) components);
            } else {
                newComponentNode.add(components);
            }
            final String pageKey = "page" + (index + 1);
            if (totalPages > 1 && appendPreviousNextButtons) {
                if (index == 0) { //First Page - only Go to Next
                    JsonNode nextBtn = addNextButtonOnFirstPage(pageKey, objectMapper);
                    newComponentNode.add(nextBtn);
                } else if (index == (wizardPageArray.length - 1)) { //Last Page - only Go to Prev
                    JsonNode prevNextBtns = addPreviousButtonOnLastPage(pageKey, objectMapper);
                    newComponentNode.add(prevNextBtns);
                } else { //Page in the middle - needs Go to Next and Prev Page
                    JsonNode lastPageBtns = addPrevNextButtons(pageKey, objectMapper);
                    newComponentNode.add(lastPageBtns);
                }
            }
            pageNode.set("components", newComponentNode);
            rootComponentsNode.add(pageNode);
            index++;
        }
        if (rootComponentsNode.size() < 1) {
            return "{}";
        }

        concatenatedNode.set("components", rootComponentsNode);
        concatenatedFormsJson = getAsJsonString(concatenatedNode);
        return concatenatedFormsJson;
    }



    private ObjectNode getObjectnode(final String panelTitle, final int index) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode panelNode  = objectMapper.createObjectNode();
        panelNode.put("key", "page"+index);
        panelNode.put("type", "panel");
        panelNode.put("input", false);
        panelNode.put("label",panelTitle);
        panelNode.put("title",panelTitle);
        panelNode.put("tableView",false);
        panelNode.put("collapsible", false);
        panelNode.put( "saveOnEnter", false);
        panelNode.put(        "scrollToTop", false);
        panelNode.put("navigateOnEnter", false);
        panelNode.put("breadcrumbClickable", true);
        return panelNode;
    }

    private JsonNode addNextButtonOnFirstPage(final String pageKey, final ObjectMapper mapper) throws Exception{
      final String nextButtonJsonStr = "        {" +
              "          \"label\": \"Columns\"," +
              "          \"input\": false," +
              "          \"tableView\": false," +
              "          \"key\": \"" + pageKey + "Columns\"," +
              "          \"columns\": [" +
              "            {" +
              "              \"components\": []," +
              "              \"width\": 6," +
              "              \"offset\": 0," +
              "              \"push\": 0," +
              "              \"pull\": 0," +
              "              \"size\": \"md\"" +
              "            }," +
              "            {" +
              "              \"components\": [" +
              "                {" +
              "                  \"input\": true," +
              "                  \"label\": \"Go to Next Set\"," +
              "                  \"tableView\": false," +
              "                  \"key\": \"" + pageKey+ "NextPage\"," +
              "                  \"rightIcon\": \"fa fa-chevron-right\"," +
              "                  \"block\": true," +
              "                  \"action\": \"event\"," +
              "                  \"type\": \"button\"," +
              "                  \"event\": \"gotoNextPage\"," +
              "                  \"hideOnChildrenHidden\": false," +
              "                  \"size\":\"sm\"," +
              "                  \"theme\":\"secondary\"" +
              "                }" +
              "              ]," +
              "              \"width\": 6," +
              "              \"offset\": 0," +
              "              \"push\": 0," +
              "              \"pull\": 0," +
              "              \"size\": \"md\"" +
              "            }" +
              "          ]," +
              "          \"type\": \"columns\"," +
              "          \"hideLabel\": true" +
              "        }";
      return mapper.readTree(nextButtonJsonStr);
    }

    private JsonNode addPrevNextButtons(final String pageKey, final ObjectMapper mapper) throws Exception{
        final String prevAndNextJsonStr = "        {" +
                "          \"label\": \"Columns\"," +
                "          \"input\": false," +
                "          \"tableView\": false," +
                "          \"key\": \""+ pageKey+"Columns\"," +
                "          \"columns\": [" +
                "            {" +
                "              \"components\": [" +
                "                {" +
                "                  \"input\": true," +
                "                  \"label\": \"Go to Previous Set\"," +
                "                  \"tableView\": false," +
                "                  \"key\": \"" + pageKey +"PreviousPage\"," +
                "                  \"leftIcon\": \"fa fa-chevron-left\"," +
                "                  \"block\": true," +
                "                  \"action\": \"event\"," +
                "                  \"type\": \"button\"," +
                "                  \"event\": \"gotoPreviousPage\"," +
                "                  \"hideOnChildrenHidden\": false," +
                "                  \"size\":\"sm\"," +
                "                  \"theme\":\"secondary\"" +
                "                }" +
                "              ]," +
                "              \"width\": 6," +
                "              \"offset\": 0," +
                "              \"push\": 0," +
                "              \"pull\": 0," +
                "              \"size\": \"md\"" +
                "            }," +
                "            {" +
                "              \"components\": [" +
                "                {" +
                "                  \"input\": true," +
                "                  \"label\": \"Go to Next Set\"," +
                "                  \"tableView\": false," +
                "                  \"key\": \"" + pageKey+"NextPage\"," +
                "                  \"rightIcon\": \"fa fa-chevron-right\"," +
                "                  \"block\": true," +
                "                  \"action\": \"event\"," +
                "                  \"type\": \"button\"," +
                "                  \"event\": \"gotoNextPage\"," +
                "                  \"hideOnChildrenHidden\": false," +
                "                  \"size\":\"sm\"," +
                "                  \"theme\":\"secondary\"" +
                "                }" +
                "              ]," +
                "              \"width\": 6," +
                "              \"offset\": 0," +
                "              \"push\": 0," +
                "              \"pull\": 0," +
                "              \"size\": \"md\"" +
                "            }" +
                "          ]," +
                "          \"type\": \"columns\"," +
                "          \"hideLabel\": true" +
                "        }";
        return mapper.readTree(prevAndNextJsonStr);
    }

    private JsonNode addPreviousButtonOnLastPage(final String pageKey, final ObjectMapper mapper) throws Exception{
        final String prevButtonJsonStr = "        {" +
                "          \"label\": \"Columns\"," +
                "          \"input\": false," +
                "          \"tableView\": false," +
                "          \"key\": \"" + pageKey + "Columns\"," +
                "          \"columns\": [" +
                "            {" +
                "              \"components\": []," +
                "              \"width\": 6," +
                "              \"offset\": 0," +
                "              \"push\": 0," +
                "              \"pull\": 0," +
                "              \"size\": \"md\"" +
                "            }," +
                "            {" +
                "              \"components\": [" +
                "                {" +
                "                  \"input\": true," +
                "                  \"label\": \"Go to Previous Set\"," +
                "                  \"tableView\": false," +
                "                  \"key\": \"" + pageKey+ "PreviousPage\"," +
                "                  \"leftIcon\": \"fa fa-chevron-left\"," +
                "                  \"block\": true," +
                "                  \"action\": \"event\"," +
                "                  \"type\": \"button\"," +
                "                  \"event\": \"gotoPreviousPage\"," +
                "                  \"hideOnChildrenHidden\": false," +
                "                  \"size\":\"sm\"," +
                "                  \"theme\":\"secondary\"" +
                "                }" +
                "              ]," +
                "              \"width\": 6," +
                "              \"offset\": 0," +
                "              \"push\": 0," +
                "              \"pull\": 0," +
                "              \"size\": \"md\"" +
                "            }" +
                "          ]," +
                "          \"type\": \"columns\"," +
                "          \"hideLabel\": true" +
                "        }";
        return mapper.readTree(prevButtonJsonStr);
    }


    private String getAsJsonString(final ObjectNode objectNode) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String pretty = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectNode);
        return objectNode.toString();
    }

    private void checkForNameClashPerNode(JsonNode existing, JsonNode proposedComponents)
            throws JsonProcessingException, IOException, NullPointerException, CustomVariableNameClashException {
        List<String> nameClashes = new ArrayList<String>();
        if (proposedComponents != null) {
            if (proposedComponents.isArray()) {
                for (final JsonNode proposedComp : proposedComponents) {
                    if (proposedComp.has(COMPONENTS_KEY_FIELD)) {
                        JsonNode key = proposedComp.get(COMPONENTS_KEY_FIELD);
                        JsonNode type = proposedComp.get(COMPONENTS_TYPE_FIELD);
                        if (type.asText().equals(COMPONENT_CONTENT_TYPE)) {
                            continue;
                        }
                        if (type.asText().equals(COMPONENT_PANEL_TYPE)) {
                            List<JsonNode> panelComponents = proposedComp.findValues(COMPONENTS_KEY);
                            for (JsonNode componentNode : panelComponents) {
                                if (!componentNode.isMissingNode()) {
                                    try {
                                        checkForNameClashPerNode(existing, componentNode);
                                    } catch (CustomVariableNameClashException c) {
                                        nameClashes.addAll(c.getClashes());
                                    }
                                }
                            }
                        }
                        if (type.asText().equals(COMPONENTS_COLUMNS_TYPE)) {
                            //recursive
                            try {
                                checkForNameClash(existing, proposedComp);
                            } catch (CustomVariableNameClashException cv) {
                                nameClashes.addAll(cv.getClashes());
                            }
                        } else {
                            boolean found = searchJsonForKeyWithValue(existing, key.textValue());
                            if (found) {
                                nameClashes.add(key.textValue());
                            }
                        }
                    }
                }
            }
        }
        if (nameClashes.size() > 0) {
            throw new CustomVariableNameClashException(nameClashes);
        }
    }

    private boolean searchJsonForKeyWithValue(JsonNode existing, String value)
            throws JsonProcessingException, IOException, NullPointerException {
        boolean found = false;
        List<JsonNode> existingComponents = existing.findValues(COMPONENTS_KEY);
        for (JsonNode e : existingComponents) {
            if (!e.isMissingNode()) {
                found = searchJsonForKeyWithValuePerNode(e, value);
                if (found) {
                    break;
                }
            }
        }
        return found;
    }

    private boolean searchJsonForKeyWithValuePerNode(JsonNode existingComponents, String value)
            throws JsonProcessingException, IOException, NullPointerException {
        boolean found = false;
        if (existingComponents != null) {
            if (existingComponents.isArray()) {
                for (final JsonNode existingComp : existingComponents) {
                    if (existingComp.has(COMPONENTS_KEY_FIELD)) {
                        JsonNode key = existingComp.get(COMPONENTS_KEY_FIELD);
                        JsonNode type = existingComp.get(COMPONENTS_TYPE_FIELD);
                        if (type.asText().equals(COMPONENT_CONTENT_TYPE)) {
                            continue;
                        }
                        if (type.asText().equals(COMPONENTS_COLUMNS_TYPE)) {
                            //recursive
                            found = searchJsonForKeyWithValue(existingComp.get(COMPONENTS_COLUMNS_TYPE), value);
                        } else {
                            if (key.textValue().equals(value)) {
                                found = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return found;
    }


}
