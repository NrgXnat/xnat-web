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
package org.nrg.xnat.customforms.pojo;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.customforms.utils.CustomFormsConstants;

import java.util.ArrayList;
import java.util.List;

/*
 * A POJO to represent the submission from Client
 * The submission is generated as part of the Add New task on the Manage Data Form Page
 *
 */

@Getter
@Setter
public class ClientPojo {

    private SubmissionDataPojo submission;

    private Object builder;

    @JsonRawValue
    public String getBuilder() {
        // default raw value: null or "{}"
        return builder == null ? null : builder.toString();
    }

    public void setBuilder(JsonNode node) {
        this.builder = node;
    }

    /**
     * Validates the submitted data from the UI
     * @param user - the user who submits the data
     * @return - List of validation error strings, if any
     */
    public List<String> validate(UserI user) {
        List<String> problems = new ArrayList<String>();
        boolean isUserAnAdminOrDataFormManager = true;
        if (!Roles.isSiteAdmin(user.getUsername()) && !Roles.checkRole(user, CustomFormsConstants.DATAFORM_MANAGER_ROLE)) {
            isUserAnAdminOrDataFormManager = false;
        }
        String isSiteWide = submission.getData().getIsThisASiteWideConfiguration();
        if (isSiteWide == null) {
            problems.add("Invalid json");
        }else if (isSiteWide.equalsIgnoreCase("NO")) {
            List<ComponentPojo> projects = submission.getData().getXnatProject();
            if (projects == null) {
                problems.add("Invalid JSON");
                return problems;
            }
            for (ComponentPojo proj : projects) {
                //Encoded as: PROTOCOL_NAME : PROJECT_ID
                String projectId = proj.getValue();
                if (!isUserAnAdminOrDataFormManager && !Permissions.isProjectOwner(user, projectId)) {
                    problems.add("Insufficient user permissions (Not Admin, Data Form Manager or Project Owner of " + projectId);
                }
            }
        } else if (isSiteWide.equalsIgnoreCase("YES") ) {
            if (!isUserAnAdminOrDataFormManager) {
                problems.add("Insufficient user permissions (Not Admin, Data Form Manager) to create a site wide form");
            }
        } else {
            problems.add("Invalid JSON");
        }
        return problems;
    }
}
