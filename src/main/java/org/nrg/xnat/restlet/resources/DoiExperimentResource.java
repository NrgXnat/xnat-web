/*
 * web: org.nrg.xnat.restlet.resources.ExperimentResource
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
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.model.XnatExperimentdataShareI;
import org.nrg.xdat.om.*;
import org.nrg.xdat.om.base.BaseXnatExperimentdata;
import org.nrg.xdat.om.base.BaseXnatSubjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xft.XFTItem;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.exception.InvalidValueException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.ValidationUtils.ValidationResults;
import org.nrg.xft.utils.XftStringUtils;
import org.nrg.xnat.archive.ValidationException;
import org.nrg.xnat.helpers.merge.ProjectAnonymizer;
import org.nrg.xnat.restlet.actions.FixScanTypes;
import org.nrg.xnat.restlet.actions.PullSessionDataFromHeaders;
import org.nrg.xnat.restlet.actions.TriggerPipelines;
import org.nrg.xnat.restlet.representations.ItemHTMLRepresentation;
import org.nrg.xnat.restlet.util.XNATRestConstants;
import org.nrg.xnat.utils.WorkflowUtils;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.xml.sax.SAXException;

import java.util.Hashtable;

public class DoiExperimentResource extends SecureResource {
    public DoiExperimentResource(Context context, Request request, Response response) {
        super(context, request, response);

        _experimentId = (String) getParameter(request, "EXPT_ID");
        if (StringUtils.isNotBlank(_experimentId)) {
            getVariants().add(new Variant(MediaType.TEXT_XML));
        } else {
            response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
        }

        final String projectId = (String) getParameter(request, "PROJECT_ID");
        if (StringUtils.isNotBlank(projectId)) {
            final UserI user = Users.getAdminUser();
            _project = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
            _existing = XnatExperimentdata.GetExptByProjectIdentifier(projectId, _experimentId, user, false);
        }
    }

    @Override
    public boolean isModifiable() {
        return false;
    }

    @Override
    public Representation represent(Variant variant) throws ResourceException {
        final MediaType mt = overrideVariant(variant);

        if (_experiment == null && _experimentId != null) {
            final UserI user = Users.getAdminUser();
            _experiment = XnatExperimentdata.getXnatExperimentdatasById(_experimentId, user, false);

            if (_project != null) {
                if (_experiment == null) {
                    _experiment = XnatExperimentdata.GetExptByProjectIdentifier(_project.getId(), _experimentId, user, false);
                }
            }
        }

        if (_experiment != null) {
            if (filepath != null && !filepath.equals("") && filepath.equals("status")) {

                return returnStatus(_experiment, mt);
            } else if (filepath != null && !filepath.equals("") && filepath.equals("history")) {
                try {
                    return buildChangesets(_experiment.getItem(), _experiment.getStringProperty("ID"), mt);
                } catch (Exception e) {
                    logger.error("", e);
                    getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
                    return null;
                }
            } else if (StringUtils.startsWith(filepath, "projects")) {
                return representProjectsForArchivableItem(_experiment.getLabel(), _experiment.getPrimaryProject(false), _experiment.getProjectDatas(), mt);
            } else {
                return representItem(_experiment.getItem(), mt);
            }
        } else {
            getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND,
                    "Unable to find the specified experiment.");
            return null;
        }

    }

    private void setSubject(final XFTItem item) throws Exception {
        //MATCH SUBJECT
        XnatSubjectdata subject;
        if (item.instanceOf(XnatSubjectassessordata.SCHEMA_ELEMENT_NAME)) {
            final XnatSubjectassessordata assessor = (XnatSubjectassessordata) _experiment;

            if (StringUtils.isNotBlank(getQueryVariable("subject_ID"))) {
                assessor.setSubjectId(getQueryVariable("subject_ID"));
            }

            if (StringUtils.isNotBlank(assessor.getSubjectId())) {
                subject = getSubject(assessor);

                if (subject == null && _existing != null) {
                    subject = ((XnatSubjectassessordata) _existing).getSubjectData();
                    if (subject != null) {
                        assessor.setSubjectId(subject.getId());
                    }
                }

                if (subject == null) {
                    final UserI user = Users.getAdminUser();
                    subject = new XnatSubjectdata(user);
                    subject.setProject(_project.getId());
                    subject.setLabel(assessor.getSubjectId());
                    subject.setId(XnatSubjectdata.CreateNewID());
                    if (!Permissions.canCreate(user, subject)) {
                        getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Specified user account has insufficient create privileges for subjects in this project.");
                        return;
                    }
                    BaseXnatSubjectdata.save(subject, false, true, user, newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.AUTO_CREATE_SUBJECT));
                    assessor.setSubjectId(subject.getId());
                }
            }
        }
    }

    private XnatSubjectdata getSubject(XnatSubjectassessordata assessor) {
        final UserI     user    = Users.getAdminUser();
        XnatSubjectdata subject = XnatSubjectdata.getXnatSubjectdatasById(assessor.getSubjectId(), user, false);
        if (subject != null) {
            return subject;
        }

        if (StringUtils.isNotBlank(assessor.getProject()) && StringUtils.isNotBlank(assessor.getLabel())) {
            subject = XnatSubjectdata.GetSubjectByProjectIdentifier(assessor.getProject(), assessor.getSubjectId(), user, false);
        }
        if (subject != null) {
            return subject;
        }

        for (final XnatExperimentdataShareI pp : assessor.getSharing_share()) {
            subject = XnatSubjectdata.GetSubjectByProjectIdentifier(pp.getProject(), assessor.getSubjectId(), user, false);
            if (subject != null) {
                break;
            }
        }
        return subject;
    }

    private void anonymize(final XnatImagesessiondata session, final XnatImagesessiondata previous) throws BaseXnatExperimentdata.UnknownPrimaryProjectException {
        if (StringUtils.isNotBlank(session.getSubjectId()) && !StringUtils.equalsIgnoreCase(session.getSubjectId(), previous.getSubjectId())) {
            try {
                // re-apply this project's edit script
                session.applyAnonymizationScript(new ProjectAnonymizer((XnatImagesessiondata) _experiment, _experiment.getProject(), session.getArchiveRootPath()));
            } catch (TransactionException e) {
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
            }
        }
    }

    private XnatExperimentdata getExistingExperiment(XnatExperimentdata currExp) {
        XnatExperimentdata retExp = null;
        if (currExp.getId() != null) {
            retExp = XnatExperimentdata.getXnatExperimentdatasById(currExp.getId(), null, completeDocument);
        }

        final UserI user = Users.getAdminUser();
        if (retExp == null && currExp.getProject() != null && currExp.getLabel() != null) {
            retExp = XnatExperimentdata.GetExptByProjectIdentifier(currExp.getProject(), currExp.getLabel(), user, completeDocument);
        }

        if (retExp == null) {
            for (XnatExperimentdataShareI pp : currExp.getSharing_share()) {
                retExp = XnatExperimentdata.GetExptByProjectIdentifier(pp.getProject(), pp.getLabel(), user, completeDocument);
                if (retExp != null) {
                    break;
                }
            }
        }
        return retExp;
    }

    @Override
    public Representation representItem(XFTItem item, MediaType mt) {
//        if (mt.equals(MediaType.TEXT_HTML)) {
        try {
            return new ItemHTMLRepresentation(item, MediaType.TEXT_HTML, getRequest(), Users.getAdminUser(), "DOI_report_xnat_mrSessionData.vm", new Hashtable<String, Object>());
        } catch (Exception e) {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
            return null;
        }
    }

    private final String _experimentId;

    private XnatProjectdata    _project    = null;
    private XnatExperimentdata _experiment = null;
    private XnatExperimentdata _existing   = null;
}
