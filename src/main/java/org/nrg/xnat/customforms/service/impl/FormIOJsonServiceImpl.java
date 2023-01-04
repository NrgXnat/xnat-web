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
package org.nrg.xnat.customforms.service.impl;

import org.nrg.framework.constants.Scope;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.forms.models.pojo.FormFieldPojo;
import org.nrg.xdat.forms.services.FormIOJsonService;
import org.nrg.xnat.customforms.helpers.CustomFormHelper;
import org.nrg.xnat.customforms.pojo.FormIOJsonToXnatCustomField;
import org.nrg.xnat.customforms.service.CustomVariableAppliesToService;
import org.nrg.xnat.customforms.utils.CustomFormsConstants;
import org.nrg.xnat.customforms.utils.FormsIOJsonUtils;
import org.nrg.xnat.entities.CustomVariableAppliesTo;
import org.nrg.xnat.entities.CustomVariableFormAppliesTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormIOJsonServiceImpl implements FormIOJsonService {

    private final CustomVariableAppliesToService customVariableAppliesToService;

    @Autowired
    public FormIOJsonServiceImpl(final CustomVariableAppliesToService customVariableAppliesToService) {
        this.customVariableAppliesToService = customVariableAppliesToService;
    }

    /**
     * Returns list of Fields for dataType (xsiType), project, protocol, visit, visitSubType
     * The list is used to build the Search listing
     *
     * @param dataType     (@Nonnull) - xsiType
     * @param project      - project Id
     * @param protocol     - optional protocol name
     * @param visit        - optional visit
     * @param visitSubType - optional subtype of the visit
     * @return - List of FormFieldPojo
     */
    public List<FormFieldPojo> getFormsForObject(@Nonnull String dataType, String project, String protocol, String visit, String visitSubType) {
        List<FormFieldPojo> forms = new ArrayList();
        //Look for project specific configurations
        //Look for site specific configurations
        try {
            forms = getForm(Scope.Project, project, dataType, protocol, visit, visitSubType);
        } catch (Exception e) {
        }
        return forms;
    }

    private List<FormFieldPojo> getForm(@Nonnull Scope scope, String scopeId, @Nonnull String dataType, String protocol, String visit, String visitSubtype) throws IOException, NotFoundException {
        List<FormFieldPojo> formFields = new ArrayList();
        List<FormFieldPojo> fields = getFields(scope, scopeId, dataType, protocol, visit, visitSubtype);
        formFields.addAll(fields);
        return formFields;
    }

    private List<FormFieldPojo> getFields(@Nonnull Scope scope, String scopeId, @Nonnull String dataType, String protocol, String visit, String visitSubtype) {

        List<FormFieldPojo> formFields = new ArrayList();
        List<String> statuses = new ArrayList<String>();
        statuses.add(CustomFormsConstants.ENABLED_STATUS_STRING);
        statuses.add(CustomFormsConstants.OPTED_OUT_STATUS_STRING);

        List<CustomVariableAppliesTo> projectSpecificSelections = customVariableAppliesToService.filterByPossibleStatusFindByScopeEntityIdDataTypeProtocolVisitSubtype(scope, scopeId,
                dataType, protocol, visit,
                visitSubtype, statuses);
        List<CustomVariableAppliesTo> siteWideSelections = customVariableAppliesToService.filterByStatusFindByScopeEntityIdDataTypeProtocolVisitSubtype(Scope.Site, null,
                dataType, protocol, visit,
                visitSubtype, CustomFormsConstants.ENABLED_STATUS_STRING);
        //A project can opt out of the site wide form.
        //Go through all the site wide forms for a datatype/protoocol/visit/subtype
        //Include it only if the project has not opted out of the form
        List<CustomVariableFormAppliesTo> optedInSiteForms = FormsIOJsonUtils.removeSiteFormsOptedOutByProject(siteWideSelections, projectSpecificSelections);

        if (optedInSiteForms != null && optedInSiteForms.size() > 0) {
            for (CustomVariableFormAppliesTo siteWideSelection : optedInSiteForms) {
                formFields.addAll(getFormObj(siteWideSelection.getCustomVariableAppliesTo()));
            }
        }

        if (projectSpecificSelections != null && projectSpecificSelections.size() > 0) {
            for (CustomVariableAppliesTo projectSpecificSelection : projectSpecificSelections) {
                formFields.addAll(getFormObj(projectSpecificSelection));
            }
        }
        return formFields;

    }

    /**
     * Parses a configuration and converts the FormsIO Fields to XNAT Custom Fields
     *
     * @param c - CustomVariableAppliesTo from which the formsIO fields are to be extracted
     * @return List of FormIOJsonToXnatCustomField
     */
    private List<FormIOJsonToXnatCustomField> getFormObj(CustomVariableAppliesTo c) {
        // Convert the configuration into a new FormJson Pojo
        return c.getCustomVariableFormAppliesTos().stream()
                .filter(customVariableFormAppliesTo -> CustomFormsConstants.ENABLED_STATUS_STRING.equals(customVariableFormAppliesTo.getStatus()))
                .map(CustomVariableFormAppliesTo::getCustomVariableForm)
                .map(CustomFormHelper::getFormObj)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

}
