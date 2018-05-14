/*
 * web: org.nrg.xnat.restlet.resources.SubjectResource
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.restlet.resources;

import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ActionException;
import org.nrg.transaction.TransactionException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.model.XnatProjectparticipantI;
import org.nrg.xdat.om.*;
import org.nrg.xdat.om.base.BaseXnatSubjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xft.XFTItem;
import org.nrg.xft.db.MaterializedView;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.exception.InvalidValueException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xft.utils.ValidationUtils.ValidationResults;
import org.nrg.xft.utils.XftStringUtils;
import org.nrg.xnat.helpers.merge.ProjectAnonymizer;
import org.nrg.xnat.helpers.xmlpath.XMLPathShortcuts;
import org.nrg.xnat.restlet.representations.ItemHTMLRepresentation;
import org.nrg.xnat.restlet.representations.TurbineScreenRepresentation;
import org.nrg.xnat.utils.WorkflowUtils;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.xml.sax.SAXParseException;

import java.util.Hashtable;

import static org.nrg.xft.event.XftItemEventI.CREATE;

public class DoiSubjectResource extends SecureResource {
    private static final String PRIMARY = "primary";

    protected XnatProjectdata proj = null;
    protected String subID;
    protected XnatSubjectdata sub = null;
    protected XnatSubjectdata existing = null;

    public DoiSubjectResource(Context context, Request request, Response response) {
        super(context, request, response);

        final UserI  user = Users.getAdminUser();
        final String pID  = (String) getParameter(request, "PROJECT_ID");
        if (pID != null) {
            proj = XnatProjectdata.getProjectByIDorAlias(pID, user, false);
        }

        subID = (String) getParameter(request, "SUBJECT_ID");

        if (proj != null) {
            existing = XnatSubjectdata.GetSubjectByProjectIdentifier(proj.getId(), subID, user, false);
        }

        if (existing == null) {
            existing = XnatSubjectdata.getXnatSubjectdatasById(subID, user, false);
            if (existing != null && (proj != null && !existing.hasProject(proj.getId()))) {
                existing = null;
            }
        }

        this.getVariants().add(new Variant(MediaType.TEXT_HTML));
        this.getVariants().add(new Variant(MediaType.TEXT_XML));

        this.fieldMapping.putAll(XMLPathShortcuts.getInstance().getShortcuts(XMLPathShortcuts.SUBJECT_DATA, false));
    }

    @Override
    public boolean allowPut() {
        return false;
    }

    @Override
    public boolean allowDelete() {
        return false;
    }

    @Override
    public Representation represent(Variant variant) {
        MediaType mt = overrideVariant(variant);

        final UserI user = Users.getAdminUser();

        if (sub == null && subID != null) {
            sub = XnatSubjectdata.getXnatSubjectdatasById(subID, user, false);

            if (sub == null && proj != null) {
                sub = XnatSubjectdata.GetSubjectByProjectIdentifier(proj.getId(), subID, user, false);
            }
        }

        if (sub != null) {
            String filepath = getRequest().getResourceRef().getRemainingPart();
            if (filepath != null && filepath.contains("?")) {
                filepath = filepath.substring(0, filepath.indexOf("?"));
            }
            if (filepath != null && filepath.startsWith("/")) {
                filepath = filepath.substring(1);
            }
            if (filepath != null && filepath.equals("status")) {
                return returnStatus(sub, mt);
            } else if (StringUtils.startsWith(filepath, "projects")) {
                return representProjectsForArchivableItem(sub.getLabel(), sub.getPrimaryProject(false), sub.getProjectDatas(), mt);
            } else {
                return representItem(sub.getItem(), mt);
            }
        } else {
            final StringBuilder message = new StringBuilder("Unable to find the specified subject. ");
            if (proj == null) {
                message.append("When searching by subject ID only, you must specify the accession number and not the subject label, which is not unique across the XNAT system. ");
                message.append(subID).append(" is not a known subject accession ID.");
            } else {
                message.append("The project ").append(proj.getId()).append(" does not contain a subject identifiable by the ID or label ").append(subID).append(".");
            }
            this.getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND, message.toString());
            return null;
        }

    }

    @Override
    public Representation representItem(XFTItem item, MediaType mt) {
//        if (mt.equals(MediaType.TEXT_HTML)) {
        try {
            return new ItemHTMLRepresentation(item, MediaType.TEXT_HTML, getRequest(), Users.getAdminUser(), "DOI_report_xnat_subjectData.vm", new Hashtable<String, Object>());
        } catch (Exception e) {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
            return null;
        }
    }

}
