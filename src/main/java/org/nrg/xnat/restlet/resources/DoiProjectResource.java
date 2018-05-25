/*
 * web: org.nrg.xnat.restlet.resources.ProjectResource
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.restlet.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ActionException;
import org.nrg.framework.utilities.Reflection;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.ArcProject;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.base.BaseXnatProjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xft.XFTItem;
import org.nrg.xft.db.PoolDBUtils;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.exception.InvalidPermissionException;
import org.nrg.xft.presentation.FlattenedItemA;
import org.nrg.xft.presentation.ItemJSONBuilder;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.XftStringUtils;
import org.nrg.xnat.helpers.xmlpath.XMLPathShortcuts;
import org.nrg.xnat.restlet.representations.ItemHTMLRepresentation;
import org.nrg.xnat.restlet.representations.ItemXMLRepresentation;
import org.nrg.xnat.restlet.representations.JSONObjectRepresentation;
import org.nrg.xnat.turbine.utils.ArcSpecManager;
import org.nrg.xnat.utils.WorkflowUtils;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import static org.nrg.xdat.om.base.auto.AutoXnatProjectdata.SCHEMA_ELEMENT_NAME;
import static org.restlet.data.Status.*;

@Slf4j
public class DoiProjectResource extends SecureResource {
    private XnatProjectdata project = null;
    private final String projectId;
    private final String doi;

    public DoiProjectResource(Context context, Request request, Response response) {
        super(context, request, response);

        // This was part of a fix for XNAT-3453, but it breaks other non-standard REST ways of setting project properties.
        // if (!validateCleanUrl(request, response)) {
        //     throw new ResourceException(response.getStatus());
        // }
        doi = (String) getParameter(request, "doi");
        projectId = (String) getParameter(request, "PROJECT_ID");
        if (projectId != null) {
            project = XnatProjectdata.getProjectByIDorAlias(projectId, Users.getAdminUser(), false);
        }

        if (project != null) {
            getVariants().add(new Variant(MediaType.TEXT_HTML));
            getVariants().add(new Variant(MediaType.TEXT_XML));
        }

        fieldMapping.putAll(XMLPathShortcuts.getInstance().getShortcuts(XMLPathShortcuts.PROJECT_DATA, false));
        fieldMapping.put("doi", doi);
    }

    @Override
    public boolean allowDelete() {
        return false;
    }

    @Override
    public boolean allowPut() {
        return false;
    }

    @Override
    public Representation represent(Variant variant) {
        if (project != null) {
            try {
                return handleDoi(this, variant);
            } catch (Exception e) {
                log.error("", e);
                getResponse().setStatus(SERVER_ERROR_INTERNAL);
                return null;
            }
        } else {
            getResponse().setStatus(CLIENT_ERROR_NOT_FOUND, "Unable to find the specified experiment.");
            return null;
        }
    }

    public String getProjectId() {
        return project == null ? projectId : project.getId();
    }

    private Representation handleDoi(SecureResource resource, Variant variant) {
        MediaType mt = resource.overrideVariant(variant);
        DoiProjectResource projResource = (DoiProjectResource) resource;
        if (resource.filepath != null && !resource.filepath.equals("")) {
            if (resource.filepath.equals("quarantine_code")) {
                try {
                    return new StringRepresentation(projResource.project.getArcSpecification().getQuarantineCode().toString(), mt);
                } catch (Throwable e) {
                    log.error("", e);
                    projResource.getResponse().setStatus(SERVER_ERROR_INTERNAL, e.getMessage());
                    return null;
                }
            } else if (resource.filepath.startsWith("prearchive_code")) {
                try {
                    return new StringRepresentation(projResource.project.getArcSpecification().getPrearchiveCode().toString(), mt);
                } catch (Throwable e) {
                    log.error("", e);
                    projResource.getResponse().setStatus(SERVER_ERROR_INTERNAL, e.getMessage());
                    return null;
                }
            } else if (resource.filepath.startsWith("current_arc")) {
                try {
                    return new StringRepresentation(projResource.project.getArcSpecification().getCurrentArc(), mt);
                } catch (Throwable e) {
                    log.error("", e);
                    resource.getResponse().setStatus(SERVER_ERROR_INTERNAL, e.getMessage());
                    return null;
                }
            } else {
                resource.getResponse().setStatus(CLIENT_ERROR_BAD_REQUEST);
                return null;
            }
        } else {
            return projResource.representItem(projResource.project.getItem(), mt);
        }
    }

    @Override
    public Representation representItem(XFTItem item, MediaType mt) {
//        if (mt.equals(MediaType.TEXT_HTML)) {
            try {
                return new ItemHTMLRepresentation(item, MediaType.TEXT_HTML, getRequest(), Users.getAdminUser(), "DOI_report_xnat_projectData.vm", new Hashtable<String, Object>());
            } catch (Exception e) {
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
                return null;
            }
    }
}
