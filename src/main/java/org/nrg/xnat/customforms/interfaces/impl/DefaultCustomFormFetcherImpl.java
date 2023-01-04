/*
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2021, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 * @author: Mohana Ramaratnam (mohana@radiologics.com)
 * @since: 07-03-2021
 */
package org.nrg.xnat.customforms.interfaces.impl;

import org.nrg.framework.constants.Scope;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.customforms.interfaces.CustomFormFetcherI;
import org.nrg.xnat.customforms.interfaces.annotations.CustomFormFetcherAnnotation;
import org.nrg.xnat.customforms.service.CustomVariableAppliesToService;
import org.nrg.xnat.customforms.utils.CustomFormsConstants;
import org.nrg.xnat.customforms.utils.FormsIOJsonUtils;
import org.nrg.xnat.entities.CustomVariableAppliesTo;
import org.nrg.xnat.entities.CustomVariableFormAppliesTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@CustomFormFetcherAnnotation(type = CustomFormsConstants.PROTOCOL_UNAWARE)

public class DefaultCustomFormFetcherImpl implements CustomFormFetcherI {

    private final CustomVariableAppliesToService formService;

    @Autowired
    public DefaultCustomFormFetcherImpl(final CustomVariableAppliesToService formService) {
        this.formService = formService;
    }

    /**
     * Gets custom form for a given xsiType, Entity Id
     * If the id is null, custom form is built as a stackable collection of forms.
     *
     *
     * @param user - User
     * @param xsiType - The xsiType of the entity
     * @param id - id of the entity
     * @param projectIdQueryParam - Optional project id
     * @param visitId - Optional Visit id (required if you are creating a new xnat entity within a Protocol)
     * @param subType - Optional Subtype of the entity
     * @return String - Enabled Custom Form JSON; {} if no form is found
     *
     */
    public String getCustomForm(final UserI user, final String xsiType, final String id,
                                final String projectIdQueryParam, final String visitId,
                                final String subType, final boolean appendPreviousNextButtons) throws Exception {

        if ((null == id || id.equalsIgnoreCase("NULL")) && (null == projectIdQueryParam || projectIdQueryParam.equalsIgnoreCase("") || projectIdQueryParam.equalsIgnoreCase("NULL"))) {
            List<CustomVariableAppliesTo> forms = formService.filterByStatusFindByScopeEntityIdDataTypeProtocolVisitSubtype(Scope.Site, null, xsiType, null, null, null, CustomFormsConstants.ENABLED_STATUS_STRING);
            List<CustomVariableFormAppliesTo> formsToConcatenate = new ArrayList<CustomVariableFormAppliesTo>();
            for (CustomVariableAppliesTo c : forms) {
                formsToConcatenate.addAll(c.getCustomVariableFormAppliesTos());
            }
            String concatenatedJson = FormsIOJsonUtils.concatenate(formsToConcatenate, "Custom Variables", true, appendPreviousNextButtons);
            return concatenatedJson;
        }

        if (XnatProjectdata.SCHEMA_ELEMENT_NAME.equals(xsiType)) {
            // A project
            XnatProjectdata project = XnatProjectdata.getXnatProjectdatasById(id, user, false);
            if (null == project) {
                //A new project?
                if (projectIdQueryParam != null) {
                    project = XnatProjectdata.getXnatProjectdatasById(projectIdQueryParam, user, false);
                }
            }
            if (null == project) {
                throw new Exception("Did not find any project with ID " + id + " or " + projectIdQueryParam);
            }
            List<CustomVariableFormAppliesTo> forms = getFormsToConcatenate(project.getId(), xsiType);
            String concatenatedJson = FormsIOJsonUtils.concatenate(forms, "Custom Variables", true, appendPreviousNextButtons);
            return concatenatedJson;
        } else if (XnatSubjectdata.SCHEMA_ELEMENT_NAME.equals(xsiType)) {
            // A Subject
            XnatSubjectdata subject = XnatSubjectdata.getXnatSubjectdatasById(id, user, false);
            String projectId = null;
            if (null == subject) {
                if (null != projectIdQueryParam) {
                    projectId = projectIdQueryParam;
                } else {
                    throw new Exception("Did not find any subject with ID " + id + " or project with " + projectIdQueryParam);
                }
            } else {
                if (null != projectIdQueryParam) {
                    projectId = projectIdQueryParam;
                }else {
                    projectId = subject.getProject();
                }
            }
            List<CustomVariableFormAppliesTo> forms = getFormsToConcatenate(projectId, xsiType);
            String concatenatedJson = FormsIOJsonUtils.concatenate(forms, "Custom Variables", true, appendPreviousNextButtons);
            return concatenatedJson;
        } else {
            // An experiment
            XnatExperimentdata experiment = XnatExperimentdata.getXnatExperimentdatasById(id, user, false);
            String projectId = null;
            if (null == experiment) { // A new experiment being created
                if (null != projectIdQueryParam) {
                    projectId = projectIdQueryParam;
                } else {
                    throw new Exception("Did not find any experiment with ID " + id);
                }
            } else {
                if (null != projectIdQueryParam) {
                    projectId = projectIdQueryParam;
                }else {
                    projectId = experiment.getProject();
                }
            }
            List<CustomVariableFormAppliesTo> forms = getFormsToConcatenate(projectId, xsiType);
            String concatenatedJson = FormsIOJsonUtils.concatenate(forms, "Custom Variables", true, appendPreviousNextButtons);
            return concatenatedJson;
        }
    }

    private List<CustomVariableFormAppliesTo> getFormsToConcatenate(final String projectId, final String xsiType) {
        List<CustomVariableFormAppliesTo> forms = new ArrayList<CustomVariableFormAppliesTo>();

        List<String> statuses = new ArrayList<String>();
        statuses.add(CustomFormsConstants.ENABLED_STATUS_STRING);
        statuses.add(CustomFormsConstants.OPTED_OUT_STATUS_STRING);

        List<CustomVariableAppliesTo> projectForms = formService.filterByPossibleStatusFindByScopeEntityIdDataTypeProtocolVisitSubtype(Scope.Project, projectId, xsiType, null, null, null, statuses);
        List<CustomVariableAppliesTo> siteForms = formService.filterByStatusFindByScopeEntityIdDataTypeProtocolVisitSubtype(Scope.Site, null, xsiType, null, null, null, CustomFormsConstants.ENABLED_STATUS_STRING);
        List<CustomVariableFormAppliesTo> optedInForms = FormsIOJsonUtils.removeSiteFormsOptedOutByProject(siteForms, projectForms);
        forms.addAll(optedInForms);
        for (CustomVariableAppliesTo c : projectForms) {
            forms.addAll(c.getCustomVariableFormAppliesTos());
        }
        return forms;
    }

}
