package org.nrg.xnat.services.experiments.impl;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.nrg.action.ActionException;
import org.nrg.transaction.TransactionException;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.exceptions.ResourceAlreadyExistsException;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.model.*;
import org.nrg.xdat.om.*;
import org.nrg.xdat.om.base.*;
import org.nrg.xdat.om.base.auto.AutoXnatExperimentdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.services.DataTypeAwareEventService;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFTItem;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.XftItemEvent;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.InvalidValueException;
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xft.utils.ValidationUtils.ValidationResults;
import org.nrg.xft.utils.XftStringUtils;
import org.nrg.xnat.archive.ValidationException;
import org.nrg.xnat.helpers.merge.ProjectAnonymizer;
import org.nrg.xnat.model.util.SecureResourceUtil;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.restlet.actions.FixScanTypes;
import org.nrg.xnat.restlet.actions.PullSessionDataFromHeaders;
import org.nrg.xnat.restlet.util.XNATRestConstants;
import org.nrg.xnat.services.archive.PipelineService;
import org.nrg.xnat.services.experiments.ExperimentService;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.nrg.xnat.utils.WorkflowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.nrg.xft.event.XftItemEventI.DELETE;

@Slf4j
@Service
public class ExperimentServiceImpl implements ExperimentService {
    @Autowired
    public ExperimentServiceImpl(final PipelineService pipelineService, final NamedParameterJdbcTemplate template, final DataTypeAwareEventService eventService) {
        _pipelineService = pipelineService;
        _template = template;
        _eventService = eventService;
    }

    @Override
    public List<XnatExperimentdataI> findAll(UserI user) throws NotFoundException {
        List<XnatExperimentdata> experimentss = XnatExperimentdata.getAllXnatExperimentdatas(user, false);

        List<XnatExperimentdataI> experiments = new ArrayList<>();
        for (XnatExperimentdataI experiment : experimentss) {
            experiments.add(experiment);
        }
        if (Objects.isNull(experiments) || experiments.isEmpty()) {
            throw new NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME);
        }
        return experiments;
    }

    @Override
    public Optional<XnatExperimentdataI> findById(UserI user, String experimentId) throws DataFormatException, NotFoundException {
        if (StringUtils.isBlank(experimentId)) {
            throw new DataFormatException("The requested experiment ID" + experimentId + " wasn't found ");
        }
        XnatExperimentdataI experiment = XnatExperimentdata.getXnatExperimentdatasById(experimentId, user, false);
        if (Objects.isNull(experiment)) {
            throw new NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME);
        }
        return Optional.of(experiment);
    }

    @Override
    public List<XnatExperimentdataI> findAllByProjectId(UserI user, String projectId) throws DataFormatException, NotFoundException {
        if (StringUtils.isBlank(projectId)) {
            throw new DataFormatException("The requested project ID" + projectId + " wasn't found ");
        }
        List<XnatExperimentdataI> experimentss = _template.query(PROJECT_EXPERIMENT_QUERY, new MapSqlParameterSource("projectId", projectId), new ExperimentRowMapper(user));

        List<XnatExperimentdataI> experiments = new ArrayList<>();
        for (XnatExperimentdataI experiment : experimentss) {
            experiments.add(experiment);
        }
        if (Objects.isNull(experiments) || experiments.isEmpty()) {
            throw new NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME);
        }
        return experiments;
    }

    @Override
    public List<XnatExperimentdataI> findAllByProjectIdAndSubjectId(UserI user, String projectId, String subjectId) throws DataFormatException, NotFoundException {
        if (StringUtils.isBlank(projectId)) {
            throw new DataFormatException("The requested project ID" + projectId + " wasn't found ");
        }
        if (StringUtils.isBlank(subjectId)) {
            throw new DataFormatException("The requested subject ID" + subjectId + " wasn't found ");
        }
        List<XnatExperimentdataI> experiments = _template.query(PROJECT_SUBJECT_EXPERIMENT_QUERY, new MapSqlParameterSource("projectId", projectId).addValue(PARAM_SUBJECT_ID, subjectId), new ExperimentRowMapper(user));
        if (Objects.isNull(experiments) || experiments.isEmpty()) {
            throw new NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME);
        }
        return experiments;
    }

    @Override
    public Optional<XnatExperimentdataI> findByProjectIdAndExperimentId(UserI user, String projectId, String experimentId) throws DataFormatException, NotFoundException {
        if (StringUtils.isBlank(experimentId)) {
            throw new DataFormatException("The requested experiment ID" + experimentId + " wasn't found ");
        }
        if (StringUtils.isBlank(projectId)) {
            throw new DataFormatException("The requested project ID" + projectId + " wasn't found ");
        }
        XnatExperimentdataI experiment = _template.queryForObject(PROJECT_AND_EXPERIMENT_QUERY + BY_PRO_EXP_ID_WHERE, new MapSqlParameterSource("experimentId", experimentId).addValue("projectId", projectId), new ExperimentRowMapper(user));
        if (Objects.isNull(experiment)) {
            throw new NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME);
        }
        return Optional.of(experiment);
    }

    @Override
    public String findExperimentIdByProjectSubjectAndIdOrLabel(final UserI user, @Nullable final String projectId, @Nullable final String subjectId, final String idOrLabel) throws DataFormatException, NotFoundException {
        final boolean hasProject = StringUtils.isNotBlank(projectId);
        final boolean hasSubject = StringUtils.isNotBlank(subjectId);
        if (!hasProject && !hasSubject) {
            if (_template.queryForObject(QUERY_EXPERIMENT_BY_ID_EXISTS, new MapSqlParameterSource(PARAM_EXPERIMENT_ID, idOrLabel), Boolean.class)) {
                return idOrLabel;
            }
            throw new NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME, idOrLabel);
        }
        if (hasProject && hasSubject) {
            // TODO: This would be much more efficient as a simple SQL query.
            return findAllByProjectIdAndSubjectId(user, projectId, subjectId).stream().filter(experiment -> StringUtils.equalsAny(idOrLabel, experiment.getId(), experiment.getLabel())).findFirst().orElseThrow(() -> new NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME, projectId + ":" + subjectId + ":" + idOrLabel)).getId();
        }
        if (hasProject) {
            // TODO: This would be much more efficient as a simple SQL query.
            return findByProjectIdAndExperimentId(user, projectId, idOrLabel).orElseThrow(() -> new NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME, projectId + ":" + idOrLabel)).getId();
        }
        return findExperimentIdBySubjectIdAndExperimentId(user, subjectId, idOrLabel);
    }

    @Override
    public String findExperimentIdBySubjectIdAndExperimentId(final UserI user, final String subjectId, final String experimentId) throws DataFormatException, NotFoundException {
        if (StringUtils.isBlank(subjectId)) {
            throw new DataFormatException("You must specify a valid subject ID");
        }
        if (StringUtils.isBlank(experimentId)) {
            throw new DataFormatException("You must specify a valid experiment ID");
        }
        try {
            return _template.queryForObject(QUERY_SUBJECT_EXPERIMENT, new MapSqlParameterSource(PARAM_SUBJECT_ID, subjectId).addValue(PARAM_ID_OR_LABEL, experimentId), String.class);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(XnatExperimentdata.SCHEMA_ELEMENT_NAME, subjectId + ":" + experimentId);
        }
    }

    @SuppressWarnings("unused")
    @Override
    public XnatExperimentdataI create(UserI user, XnatExperimentdataI xnatExperimentdata, String projectId, String subjectId, String xsiType, String allowDataDelete, XnatEventUtil event, boolean triggerPipelines, boolean supressEmail) throws NotFoundException {
        SecureResourceUtil secureResoureUtil = new SecureResourceUtil();
        if (projectId != null) {
            XnatProjectdataI project = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
            if (project == null) {
                throw new NotFoundException("Unable to identify project " + projectId);
            }

            XnatSubjectdataI subject = getSubjectData(subjectId, project, user);

            try {
                XFTItem item = ((BaseElement) xnatExperimentdata).getItem();

                item = getXFTItemIsNull(user, item, xsiType);

                if (!item.instanceOf("xnat:subjectAssessorData")) {
                    throw new DataFormatException("Only xnat:Subject documents can be PUT to this address.");
                }
                expt = (XnatSubjectassessordataI) BaseElement.GetGeneratedItem(item);

                // MATCH PROJECT
                if (project == null && expt.getProject() != null) {
                    project = XnatProjectdata.getXnatProjectdatasById(expt.getProject(), user, false);
                }

                expt = getExperimentWhenProjectIsNotNull(expt, project, user);

                // MATCH SUBJECT
                if (subject != null) {
                    expt.setSubjectId(subject.getId());
                } else {
                    subject = getSubjectDataWhenSubjectIsNull(subject, expt, user, project, secureResoureUtil, event);
                }

                if (subject == null) {
                    throw new DataFormatException("Submitted experiment record must include the subject.");
                }

                // FIND PRE-EXISTING
                XnatSubjectassessordataI existing = null;
                if (expt.getId() != null) {
                    existing = (XnatSubjectassessordata) XnatExperimentdata.getXnatExperimentdatasById(expt.getId(), user, completeDocument);
                }

                if (existing == null && expt.getProject() != null && expt.getLabel() != null) {
                    existing = (XnatSubjectassessordata) XnatExperimentdata.GetExptByProjectIdentifier(expt.getProject(), expt.getLabel(), user, completeDocument);
                }

                if (existing == null) {
                    for (XnatExperimentdataShareI pp : expt.getSharing_share()) {
                        existing = (XnatSubjectassessordata) XnatExperimentdata.GetExptByProjectIdentifier(pp.getProject(), pp.getLabel(), user, completeDocument);
                        if (existing != null) {
                            break;
                        }
                    }
                }

                if (existing == null) {
                    if (!Permissions.canCreate(user, (ItemI) expt)) {
                        throw new InsufficientPrivilegesException("Specified user account has insufficient create privileges for experiments in this project.");
                    }
                    // IS NEW
                    if (expt.getId() == null || expt.getId().equals("")) {
                        expt.setId(XnatExperimentdata.CreateNewID());
                    }
                } else {
                    throw new ResourceAlreadyExistsException("Specified experiment already exists.", expt.getLabel());
                }

                boolean allowDataDeletion = false;
                if (allowDataDelete != null && allowDataDelete.equals("true")) {
                    allowDataDeletion = true;
                }


                if (StringUtils.isNotBlank(expt.getLabel()) && !XftStringUtils.isValidId(expt.getId())) {
                    throw new DataFormatException("Invalid character in experiment label.");
                }

                final ValidationResults vr = ((ItemI) expt).validate();

                if (vr != null && !vr.isValid()) {
                    throw new DataFormatException(vr.toFullString());
                }

                secureResoureUtil.create((ArchivableItem) expt, false, allowDataDeletion, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.getAddModifyAction(expt.getXSIType(), (existing == null)), event), event, user);

                secureResoureUtil.postSaveManageStatus((ItemI) expt, user, event);

                if (Permissions.canEdit(user, ((ItemI) expt).getItem()) && (triggerPipelines || secureResoureUtil.containsAction(XNATRestConstants.TRIGGER_PIPELINES))) {
                    _pipelineService.launchAutoRun((XnatExperimentdata) expt, supressEmail, user);
                }
            } catch (ActionException e) {
                log.error("ActionException", e.getMessage());
            } catch (InvalidValueException e) {
                log.error("InvalidValueException", e.getMessage());
            } catch (Exception e) {
                log.error("SERVER_ERROR_INTERNAL", e);
            }
        }
        return expt;
    }

    @Override
    public XnatExperimentdataI update(UserI user, XnatExperimentdataI xnatexperiment, String experimentId, String projectId, String subjectId, String allowDataDelete, String label, String primary, String moveAssessors, boolean overwrite, String filepath, XnatEventUtil event, boolean fixScanTypes, boolean pullDataFromHeaders, boolean triggerPipelines, boolean supressEmail) {
        XnatExperimentdataI existing          = new XnatExperimentdata();
        XnatProjectdataI    project           = null;
        XnatExperimentdataI experiment        = null;
        SecureResourceUtil  secureResoureUtil = new SecureResourceUtil();
        if (StringUtils.isNotBlank(projectId)) {
            project = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
            existing = XnatExperimentdata.GetExptByProjectIdentifier(projectId, experimentId, user, false);
        }

        try {
            XFTItem template = null;
            if (existing != null) {
                template = ((BaseElement) existing).getItem().getCurrentDBVersion();
            }

            XFTItem item = getXnatExperimentItem(user, project, xnatexperiment, experimentId);

            experiment = (XnatExperimentdata) BaseElement.GetGeneratedItem(item);

            if (filepath != null && !filepath.equals("")) {
                filePathIsNotNull(filepath, user, experiment, project, primary, moveAssessors, event);
            } else {
                if (experiment.getLabel() == null) {
                    experiment.setLabel(experimentId);
                }

                // MATCH PROJECT
                if (project == null && experiment.getProject() != null) {
                    project = XnatProjectdata.getXnatProjectdatasById(experiment.getProject(), user, false);
                }

                experiment = verifyProjectNotNull(project, experiment, user);

                // Find the pre-existing experiment
                if (existing == null) {
                    existing = getExistingExperiment(experiment, user);
                }

                if (existing == null) {
                    experiment = existingExperimentIsNull(experiment, user, existing, project, subjectId, event);
                } else {
                    experiment = existingExperimentIsNotNull(experiment, user, existing, project, secureResoureUtil, subjectId, label, event);
                }


                boolean allowDataDeletion = false;
                if (allowDataDelete != null && allowDataDelete.equals("true")) {
                    allowDataDeletion = true;
                }

                PersistentWorkflowI wrk = WorkflowUtils.buildOpenWorkflow(user, ((ItemI) experiment).getItem(), XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.getAddModifyAction(experiment.getXSIType(), (existing == null)), event));
                EventMetaI          c   = wrk.buildEvent();

                if (fixScanTypes || secureResoureUtil.containsAction(XNATRestConstants.FIX_SCAN_TYPES)) {
                    if (experiment instanceof XnatImagesessiondata) {
                        FixScanTypes.builder().experiment((XnatExperimentdata) experiment).user(user).project((XnatProjectdata) project).allowSave(false).eventMeta(c).build().call();
                    }
                }

                if (StringUtils.isNotBlank(experiment.getLabel()) && !XftStringUtils.isValidId(experiment.getId())) {
                    throw new DataFormatException("Invalid character in experiment label.");
                }


                final ValidationResults vr = ((ItemI) experiment).validate();

                if (vr != null && !vr.isValid()) {
                    throw new DataFormatException(vr.toFullString());
                }

                try {
                    //check for unexpected modifications of ID, Project and label
                    if (existing != null && !StringUtils.equals(existing.getId(), experiment.getId())) {
                        throw new DataFormatException("ID cannot be modified");
                    }

                    if (existing != null && !StringUtils.equals(existing.getProject(), experiment.getProject())) {
                        throw new DataFormatException("Project must be modified through separate URI.");
                    }

                    //MATCHED
                    if (existing != null && !StringUtils.equals(existing.getLabel(), experiment.getLabel())) {
                        throw new DataFormatException("Label must be modified through separate URI.");
                    }

                    // Preserve the previous version of the experiment before we save it.
                    XnatExperimentdataI previous = getExistingExperiment(experiment, user);
                    if (existing == null ? secureResoureUtil.create((ArchivableItem) experiment, false, allowDataDeletion, wrk, c, user) : secureResoureUtil.update((ArchivableItem) experiment, false, allowDataDeletion, wrk, c, user)) {
                        if (((XnatProjectdata) project).getArcSpecification().getQuarantineCode() != null && ((XnatProjectdata) project).getArcSpecification().getQuarantineCode().equals(1)) {
                            ((ItemI) experiment).quarantine(user);
                        }

                        if (experiment instanceof XnatImagesessiondata && previous != null) {
                            anonymize((XnatImagesessiondataI) experiment, (XnatImagesessiondata) previous, experiment);
                        }
                    }
                } catch (Exception e1) {
                    WorkflowUtils.fail(wrk, c);
                    throw e1;
                }

                secureResoureUtil.postSaveManageStatus((ItemI) experiment, user, event);

                verifyPermission(user, experiment, secureResoureUtil, allowDataDelete, overwrite, event, pullDataFromHeaders, triggerPipelines, supressEmail);

            }

        } catch (InvalidValueException e) {
            log.error("InvalidValueException", e);
        } catch (ActionException e) {
            log.error("ActionException", e);
        } catch (Exception e) {
            log.error("SERVER_ERROR_INTERNAL", e);
        }
        return experiment;
    }


    @Override
    public void deleteById(UserI user, String projectId, String experimentId, String filepath, boolean removeFiles, XnatEventUtil event) throws DataFormatException, NotFoundException {
        delete(user, findById(user, experimentId).get(), projectId, filepath, removeFiles, event);
    }

    @Override
    public List<XnatExperimentdataI> findAllByProjectIdAndLabel(UserI user, String projectId, String label) {
        return null;
    }

    private XnatSubjectdataI getSubjectDataWhenSubjectIsNull(XnatSubjectdataI subject, XnatSubjectassessordataI expt, UserI user, XnatProjectdataI proj, SecureResourceUtil secureResoureUtil, XnatEventUtil event) {
        if (expt.getSubjectId() != null && !expt.getSubjectId().equals("")) {
            subject = XnatSubjectdata.getXnatSubjectdatasById(expt.getSubjectId(), user, false);

            if (subject == null && expt.getProject() != null && expt.getLabel() != null) {
                subject = XnatSubjectdata.GetSubjectByProjectIdentifier(expt.getProject(), expt.getSubjectId(), user, false);
            }

            if (subject == null) {
                for (XnatExperimentdataShareI pp : expt.getSharing_share()) {
                    subject = XnatSubjectdata.GetSubjectByProjectIdentifier(pp.getProject(), expt.getSubjectId(), user, false);
                    if (subject != null) {
                        break;
                    }
                }
            }

            if (subject == null) {
                String newSubjectId = null;
                try {
                    newSubjectId = XnatSubjectdata.CreateNewID();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                subject = new XnatSubjectdata(user);
                subject.setProject(proj.getId());
                subject.setLabel(expt.getSubjectId());
                subject.setId(newSubjectId);
                try {
                    secureResoureUtil.create((ArchivableItem) subject, false, true, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.AUTO_CREATE_SUBJECT, event), event, user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                expt.setSubjectId(subject.getId());
            }
        }
        return subject;
    }

    private XnatSubjectassessordataI getExperimentWhenProjectIsNotNull(XnatSubjectassessordataI expt, XnatProjectdataI proj, UserI user) throws Exception {
        if (proj != null) {
            if (expt.getProject() == null || expt.getProject().equals("")) {
                expt.setProject(proj.getId());
            } else {
                boolean matched = false;
                for (XnatExperimentdataShareI pp : expt.getSharing_share()) {
                    if (pp.getProject().equals(proj.getId())) {
                        matched = true;
                        break;
                    }
                }

                if (!matched) {
                    XnatExperimentdataShareI pp = new XnatExperimentdataShare((UserI) user);
                    pp.setProject(proj.getId());
                    ((AutoXnatExperimentdata) expt).setSharing_share((ItemI) pp);
                }
            }
        } else {
            throw new DataFormatException("Submitted experiment record must include the project attribute.");
        }

        return expt;
    }

    private XFTItem getXFTItemIsNull(UserI user, XFTItem item, String xsiType) throws XFTInitException, ElementNotFoundException, DataFormatException {
        if (item == null) {
            if (xsiType != null) {
                item = XFTItem.NewItem(xsiType, user);
            }
        }
        if (item == null) {
            throw new DataFormatException("Need PUT Contents");
        }

        return item;
    }

    private XnatSubjectdataI getSubjectData(String subjectId, XnatProjectdataI proj, UserI user) {
        XnatSubjectdataI subject = null;
        if (subjectId != null) {
            subject = XnatSubjectdata.GetSubjectByProjectIdentifier(proj.getId(), subjectId, user, false);
            if (subject == null) {
                subject = XnatSubjectdata.getXnatSubjectdatasById(subjectId, user, false);
                if (subject != null && (proj != null && !((BaseXnatSubjectdata) subject).hasProject(proj.getId()))) {
                    subject = null;
                }
            }
        }
        return subject;
    }

    private void verifyPermission(UserI user, XnatExperimentdataI experiment, SecureResourceUtil secureResoureUtil, String allowDataDelete, boolean overwrite, XnatEventUtil event, boolean pullDataFromHeaders, boolean triggerPipelines, boolean supressEmail) throws Exception {
        if (Permissions.canEdit(user, ((ItemI) experiment).getItem())) {
            if ((pullDataFromHeaders || secureResoureUtil.containsAction(XNATRestConstants.PULL_DATA_FROM_HEADERS)) && experiment instanceof XnatImagesessiondataI) {
                try {
                    final PersistentWorkflowI wrk = PersistentWorkflowUtils.buildOpenWorkflow(user, ((ItemI) experiment).getItem(), XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.DICOM_PULL, event));
                    assert wrk != null;
                    final EventMetaI c = wrk.buildEvent();
                    try {
                        PullSessionDataFromHeaders pull = new PullSessionDataFromHeaders((XnatImagesessiondataI) experiment, user, allowDataDeletion(allowDataDelete), overwrite, false, c);
                        pull.call();
                        WorkflowUtils.complete(wrk, c);
                    } catch (Exception e) {
                        WorkflowUtils.fail(wrk, c);
                        throw e;
                    }

                } catch (SAXException e) {
                    log.error("Error processing XML", e);
                } catch (ValidationException e) {
                    log.error("Error validating the item", e);
                } catch (Exception e) {
                    log.error("Unknown error encountered", e);
                }
            }

            if (triggerPipelines || secureResoureUtil.containsAction(XNATRestConstants.TRIGGER_PIPELINES)) {
                _pipelineService.launchAutoRun((XnatExperimentdata) experiment, supressEmail, user);
            }
        }

    }

    private XnatExperimentdataI existingExperimentIsNotNull(XnatExperimentdataI experiment, UserI user, XnatExperimentdataI existing, XnatProjectdataI project, SecureResourceUtil secureResoureUtil, String subjectId, String label, XnatEventUtil event) throws Exception {
        if (StringUtils.isBlank(experiment.getId())) {
            experiment.setId(existing.getId());
        }

        //MATCHED
        if (!existing.getProject().equals(experiment.getProject())) {
            throw new ResourceAlreadyExistsException("Project must be modified through separate URI.", experiment.getProject());
        }

        if (!Permissions.canEdit(user, (ItemI) experiment)) {
            throw new InsufficientPrivilegesException("Specified user account has insufficient edit privileges for experiments in this project.");
        }

        setSubject(((ItemI) existing).getItem(), experiment, project, existing, user, subjectId, event);

        if (StringUtils.isNotBlank(label)) {
            if (!experiment.getLabel().equals(existing.getLabel())) {
                experiment.setLabel(existing.getLabel());
            }
            if (!StringUtils.equals(label, existing.getLabel())) {
                if (XnatExperimentdata.GetExptByProjectIdentifier(project.getId(), label, user, false) != null) {
                    throw new ResourceAlreadyExistsException(existing.getXSIType(), existing.getLabel());
                }
                secureResoureUtil.rename(project, (ArchivableItem) existing, label, user);
            }
        }
        return experiment;
    }

    private XnatExperimentdataI existingExperimentIsNull(XnatExperimentdataI experiment, UserI user, XnatExperimentdataI existing, XnatProjectdataI project, String subjectId, XnatEventUtil event) throws Exception {
        if (!Permissions.canCreate(user, (ItemI) experiment)) {
            throw new InsufficientPrivilegesException("Specified user account has insufficient create privileges for experiments in this project.");
        }

        //IS NEW
        if (StringUtils.isBlank(experiment.getId())) {
            experiment.setId(XnatExperimentdata.CreateNewID());
        }

        setSubject(((ItemI) existing).getItem(), experiment, project, existing, user, subjectId, event);

        return experiment;

    }

    private XnatExperimentdataI verifyProjectNotNull(XnatProjectdataI project, XnatExperimentdataI experiment, UserI user) throws Exception {
        if (project != null) {
            if (experiment.getProject() == null || experiment.getProject().equals("")) {
                experiment.setProject(project.getId());
            } else if (!experiment.getProject().equals(project.getId())) {
                boolean matched = false;
                for (XnatExperimentdataShareI pp : experiment.getSharing_share()) {
                    if (pp.getProject().equals(project.getId())) {
                        matched = true;
                        break;
                    }
                }

                if (!matched) {
                    XnatExperimentdataShareI pp = new XnatExperimentdataShare(user);
                    pp.setProject(project.getId());
                    ((AutoXnatExperimentdata) experiment).setSharing_share((ItemI) pp);
                }
            }
        } else {
            throw new DataFormatException("Submitted experiment record must include the project attribute.");
        }

        return experiment;

    }

    @SuppressWarnings("unused")
    private void filePathIsNotNull(String filepath, UserI user, XnatExperimentdataI experiment, XnatProjectdataI project, String primary, String moveAssessors, XnatEventUtil event) throws Exception {
        if (filepath.startsWith("projects/")) {
            String          newProjectS = filepath.substring(9);
            XnatProjectdata newProject  = XnatProjectdata.getXnatProjectdatasById(newProjectS, user, false);
            String          newLabel    = null;
            if (newProject != null) {
                int                     index   = 0;
                XnatExperimentdataShare matched = null;
                for (XnatExperimentdataShareI pp : experiment.getSharing_share()) {
                    if (pp.getProject().equals(newProject.getId())) {
                        matched = (XnatExperimentdataShare) pp;
                        if (newLabel != null && !pp.getLabel().equals(newLabel)) {
                            pp.setLabel(newLabel);
                            BaseXnatExperimentdata.SaveSharedProject((XnatExperimentdataShare) pp, (XnatExperimentdata) experiment, user, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.RENAME_IN_SHARED_PROJECT, event));
                        }
                        break;
                    }
                    index++;
                }

                if (primary != null && primary.equals("true")) {
                    changeExperimentPrimaryProject(experiment, project, newProject, newLabel, moveAssessors, matched, index, user, event);
                    //return;
                } else {
                    if (matched == null) {
                        if (newLabel != null) {
                            XnatExperimentdataI temp = XnatExperimentdata.GetExptByProjectIdentifier(newProject.getId(), newLabel, null, false);
                            if (temp != null) {
                                throw new ResourceAlreadyExistsException("Label already in use:", newLabel);
                            }
                        }
                        if (Permissions.canCreate(user, experiment.getXSIType() + "/project", newProject.getId())) {
                            shareExperimentToProject(user, newProject, experiment, newLabel, event);
                        } else {
                            throw new InsufficientPrivilegesException("Specified user account has insufficient create privileges for experiments in the " + newProject.getId() + " project.");
                        }
                    } else {
                        throw new ResourceAlreadyExistsException("Already assigned to project:", newProject.getId());
                    }
                }

            } else {
                //setGuestDataResponse("Unable to identify project: " + newProjectS);
                //return;
            }
        } else {
            throw new DataFormatException("Please check experiment request");
        }


    }

    @SuppressWarnings("unused")
    private XFTItem getXnatExperimentItem(UserI user, XnatProjectdataI project, XnatExperimentdataI experiment, String experimentId) throws XFTInitException, ElementNotFoundException, DataFormatException {
        XFTItem item = ((BaseElement) experiment).getItem();

        if (item == null) {
            String xsiType = experiment.getXSIType();
            if (xsiType != null) {
                item = XFTItem.NewItem(xsiType, user);
            }
        }

        if (item == null) {
            if (project != null) {
                XnatSubjectassessordata om = (XnatSubjectassessordata) XnatSubjectassessordata.GetExptByProjectIdentifier(project.getId(), experimentId, user, false);
                if (om != null) {
                    item = om.getItem();
                }
            }

            if (item == null) {
                XnatSubjectassessordata om = (XnatSubjectassessordata) XnatSubjectassessordata.getXnatExperimentdatasById(experimentId, null, false);
                if (om != null) {
                    item = om.getItem();
                }
            }
        }

        if (item == null) {
            throw new DataFormatException("Need PUT Contents");
        }

        if (!item.instanceOf("xnat:experimentData")) {
            throw new DataFormatException("Only xnat:Subject documents can be PUT to this address.");
        }

        return item;
    }

    private void anonymize(final XnatImagesessiondataI session, final XnatImagesessiondata previous, XnatExperimentdataI experiment) throws BaseXnatExperimentdata.UnknownPrimaryProjectException {
        if (StringUtils.isNotBlank(session.getSubjectId()) && !StringUtils.equalsIgnoreCase(session.getSubjectId(), previous.getSubjectId())) {
            try {
                // re-apply this project's edit script
                ((BaseXnatSubjectassessordata) session).applyAnonymizationScript(new ProjectAnonymizer((XnatImagesessiondata) experiment, experiment.getProject(), ((BaseXnatSubjectassessordata) session).getArchiveRootPath()));
            } catch (TransactionException e) {
                log.error("TransactionException", e.getMessage());
            }
        }
    }


    protected boolean completeDocument = false;

    private XnatExperimentdataI getExistingExperiment(XnatExperimentdataI currExp, UserI user) {
        XnatExperimentdata retExp = null;
        if (currExp.getId() != null) {
            retExp = XnatExperimentdata.getXnatExperimentdatasById(currExp.getId(), null, completeDocument);
        }

        if (retExp == null && currExp.getProject() != null && currExp.getLabel() != null) {
            retExp = XnatExperimentdata.GetExptByProjectIdentifier(currExp.getProject(), currExp.getLabel(), user,
                                                                   completeDocument);
        }

        if (retExp == null) {
            for (XnatExperimentdataShareI pp : currExp.getSharing_share()) {
                retExp = XnatExperimentdata.GetExptByProjectIdentifier(pp.getProject(), pp.getLabel(), user,
                                                                       completeDocument);
                if (retExp != null) {
                    break;
                }
            }
        }
        return retExp;
    }

    private void shareExperimentToProject(final UserI user, final XnatProjectdataI newProject, final XnatExperimentdataI experiment, final String newLabel, XnatEventUtil event) throws Exception {
        shareExperimentToProject(user, newProject, experiment, new XnatExperimentdataShare(user), newLabel, event);
    }

    private void shareExperimentToProject(final UserI user, final XnatProjectdataI newProject, final XnatExperimentdataI experiment, final XnatExperimentdataShareI shared, final String newLabel, XnatEventUtil event) throws Exception {
        shareExperimentToProject(user, newProject, experiment, shared, newLabel, true, event);
    }

    private void shareExperimentToProject(final UserI user, final XnatProjectdataI newProject, final XnatExperimentdataI experiment, final XnatExperimentdataShareI shared, final String newLabel, boolean shareAllScans, XnatEventUtil event) throws Exception {
        final String newProjectId = newProject.getId();

        shared.setProject(newProjectId);
        ((BaseElement) shared).setProperty("sharing_share_xnat_experimentda_id", experiment.getId());
        if (StringUtils.isNotBlank(newLabel)) {
            shared.setLabel(newLabel);
        }
        if (shareAllScans) {
            if (experiment instanceof XnatImagesessiondataI) {
                for (XnatImagescandataI scan : ((XnatImagesessiondataI) experiment).getScans_scan()) {
                    shareScanToProject(user, newProject, (XnatImagescandataI) scan, event);
                }
            }
        }
        BaseXnatExperimentdata.SaveSharedProject((XnatExperimentdataShare) shared, (XnatExperimentdata) experiment, user, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.CONFIGURED_PROJECT_SHARING, event));
        _eventService.triggerXftItemEvent((BaseElement) experiment, XftItemEvent.SHARE, ImmutableMap.<String, Object>of("target", newProjectId));
    }

    private void setSubject(final XFTItem item, XnatExperimentdataI experiment, XnatProjectdataI project, XnatExperimentdataI existing, UserI user, String subjectId2, XnatEventUtil event) throws Exception {
        //MATCH SUBJECT
        XnatSubjectdataI subject;
        try {
            if (item.instanceOf(XnatSubjectassessordata.SCHEMA_ELEMENT_NAME)) {
                final XnatSubjectassessordataI assessor = (XnatSubjectassessordata) experiment;

                if (StringUtils.isNotBlank(subjectId2)) {
                    assessor.setSubjectId(subjectId2);
                }

                if (StringUtils.isNotBlank(assessor.getSubjectId())) {
                    subject = getSubject(assessor, user);

                    if (subject == null && existing != null) {
                        subject = ((XnatSubjectassessordata) existing).getSubjectData();
                        if (subject != null) {
                            assessor.setSubjectId(subject.getId());
                        }
                    }

                    if (subject == null) {
                        final String subjectId = XnatSubjectdata.CreateNewID();
                        subject = new XnatSubjectdata(user);
                        subject.setProject(project.getId());
                        subject.setLabel(assessor.getSubjectId());
                        subject.setId(subjectId);
                        if (!Permissions.canCreate(user, (ItemI) subject)) {
                            throw new InsufficientPrivilegesException("Specified user account has insufficient create privileges for subjects in this project.");
                        }
                        BaseXnatSubjectdata.save((XnatSubjectdata) subject, false, true, user, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.AUTO_CREATE_SUBJECT, event));
                        assessor.setSubjectId(subject.getId());
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("Error in set subject");
        }

    }

    private XnatSubjectdataI getSubject(XnatSubjectassessordataI assessor, UserI user) {
        XnatSubjectdataI subject = XnatSubjectdata.getXnatSubjectdatasById(assessor.getSubjectId(), user, false);
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


    private void shareScanToProject(final UserI user, final XnatProjectdataI newProject, final XnatImagescandataI scan, XnatEventUtil event) throws Exception {
        XnatImagescandataShareI shared       = new XnatImagescandataShare(user);
        final String            newProjectId = newProject.getId();

        shared.setProject(newProjectId);
        ((BaseElement) shared).setProperty("sharing_share_xnat_imagescandat_xnat_imagescandata_id", scan.getXnatImagescandataId());
        shared.setLabel(scan.getId());
        BaseXnatImagescandata.SaveSharedProject((XnatImagescandataShare) shared, (XnatImagescandata) scan, user, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.CONFIGURED_PROJECT_SHARING, event));
        _eventService.triggerXftItemEvent((BaseElement) scan, XftItemEvent.SHARE, ImmutableMap.<String, Object>of("target", newProjectId));
    }

    private void changeExperimentPrimaryProject(final XnatExperimentdataI experiment, final XnatProjectdataI source, final XnatProjectdata destination, final String newLabel, String moveAssessors, final XnatExperimentdataShare share, final int index, UserI user, XnatEventUtil event) throws Exception {
        if (!Permissions.canDelete(user, (ItemI) experiment)) {
            throw new InsufficientPrivilegesException("Specified user account has insufficient privileges for experiments in this project.");
        }

        if (experiment.getProject().equals(destination.getId())) {
            throw new ResourceAlreadyExistsException("Already assigned to project: ", destination.getId());
        }

        final String workingLabel = StringUtils.defaultIfBlank(newLabel, StringUtils.defaultIfBlank(experiment.getLabel(), experiment.getId()));

        final XnatExperimentdataI match = XnatExperimentdata.GetExptByProjectIdentifier(destination.getId(), workingLabel, user, false);

        if (match != null) {
            throw new ResourceAlreadyExistsException("Specified label is already in use.", match.getLabel());
        }

        final List<String> assessorList = StringUtils.isNotBlank(moveAssessors) ? Arrays.asList(moveAssessors.split(",")) : null;
        final EventMetaI   meta         = BaseXnatExperimentdata.ChangePrimaryProject(user, (XnatExperimentdata) experiment, destination, workingLabel, XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.MODIFY_PROJECT, event), assessorList);
        _eventService.triggerXftItemEvent((BaseElement) experiment, XftItemEvent.MOVE, ImmutableMap.<String, Object>of("origin", source.getId(), "target", destination.getId()));

        if (share != null) {
            SaveItemHelper.authorizedRemoveChild(((BaseElement) experiment).getItem(), "xnat:experimentData/sharing/share", share.getItem(), user, meta);
            ((XnatExperimentdata) experiment).removeSharing_share(index);
        }
    }


    private boolean allowDataDeletion(String allowDataDelete) {
        return allowDataDelete != null && allowDataDelete.equals("true");
    }


    @SuppressWarnings("unused")
    private void delete(UserI user, XnatExperimentdataI experiment, String projectId, String filepath, boolean removeFiles, XnatEventUtil event) throws DataFormatException, NotFoundException {
        if (Objects.isNull(experiment)) {
            throw new NotFoundException("The experiment not found");
        }

        if (StringUtils.isNotBlank(projectId) && !StringUtils.equals(experiment.getProject(), projectId)) {
            throw new DataFormatException("You specified the project " + projectId + " in your request but the experiment is assigned to project " + experiment.getProject() + ". These values must be the same.");
        }

        XnatProjectdataI project = XnatProjectdata.getXnatProjectdatasById(experiment.getProject(), user, false);
        if (experiment == null && experiment.getId() != null) {
            experiment = XnatExperimentdata.getXnatExperimentdatasById(experiment.getId(), user, false);

            if (experiment == null && project != null) {
                experiment = XnatExperimentdata.GetExptByProjectIdentifier(project.getId(), experiment.getId(), user, false);
            }
        }

        deleteItem(user, project, (BaseElement) experiment, filepath, removeFiles, event);

    }

    private void deleteItem(UserI user, final XnatProjectdataI proj, final BaseElement item, String filepath, boolean removeFiles, XnatEventUtil event) throws NotFoundException {
        if (!ArchivableItem.class.isAssignableFrom(item.getClass())) {
            throw new IllegalArgumentException("The BaseElement item must also implement the ArchivableItem interface, but the class " + item.getClass().getName() + " doesn't.");
        }

        try {
            SecureResourceUtil        secureResoureUtil = new SecureResourceUtil();
            final XnatProjectdataI    newProject        = secureResoureUtil.getProjectFromFilePath(proj, (ArchivableItem) item, filepath, user);
            final PersistentWorkflowI wrk               = WorkflowUtils.buildOpenWorkflow(user, item.getItem(), XnatEventUtil.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.getDeleteAction(item.getXSIType()), event));
            final EventMetaI          c                 = wrk.buildEvent();

            try {
                final XnatProjectdataI             project  = (newProject != null) ? newProject : proj;
                final Class<? extends BaseElement> itemType = item.getClass();

                final String message;
                if (XnatPvisitdata.class.isAssignableFrom(itemType)) {
                    message = ((XnatPvisitdata) item).delete((XnatProjectdata) project, user, removeFiles, c);
                } else if (XnatImagesessiondata.class.isAssignableFrom(itemType)) {
                    message = ((XnatImagesessiondata) item).delete((XnatProjectdata) project, user, removeFiles, c);
                } else if (XnatSubjectdata.class.isAssignableFrom(itemType)) {
                    message = ((XnatSubjectdata) item).delete((BaseXnatProjectdata) project, user, removeFiles, c);
                } else if (XnatExperimentdata.class.isAssignableFrom(itemType)) {
                    message = ((XnatExperimentdata) item).delete((BaseXnatProjectdata) project, user, removeFiles, c);
                } else {
                    message = null;
                }
                if (message != null) {
                    WorkflowUtils.fail(wrk, c);
                    throw new InsufficientPrivilegesException("You don't have permission to delete", message);
                } else {
                    _eventService.triggerXftItemEvent(item, DELETE, ImmutableMap.of("target", project.getId()));
                    WorkflowUtils.complete(wrk, c);
                }
            } catch (Exception e) {
                try {
                    WorkflowUtils.fail(wrk, c);
                } catch (Exception e1) {
                    log.error("", e1);
                }
                log.error("", e);
            }
        } catch (PersistentWorkflowUtils.EventRequirementAbsent e) {
            log.error("Forbidden: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("Bad Request Found: " + e.getMessage(), e);
        } catch (org.nrg.framework.exceptions.NotFoundException e) {
            throw new NotFoundException(item.getXSIType(), ((ArchivableItem) item).getId());
        }
    }


//    private boolean isQueryVariableTrue(String string) {
//        return false;
//    }

    private static class ExperimentRowMapper implements RowMapper<XnatExperimentdataI> {
        ExperimentRowMapper(final UserI user) {
            _user = user;
        }

        @Override
        public XnatExperimentdataI mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
            final String        experimentId       = resultSet.getString("id");
            XnatExperimentdataI xnatExperimentdata = XnatExperimentdata.getXnatExperimentdatasById(experimentId, _user, false);
            return xnatExperimentdata;
        }

        private final UserI _user;
    }

    private static final String PARAM_SUBJECT_ID              = "subjectId";
    private static final String PARAM_EXPERIMENT_ID           = "experimentId";
    private static final String PARAM_ID_OR_LABEL             = "idOrLabel";
    private static final String TEMPLATE_OBJECT_EXISTS        = "SELECT EXISTS(%s)";
    private static final String QUERY_EXPERIMENT_BY_ID        = "SELECT id FROM xnat_experimentdata WHERE id = :" + PARAM_EXPERIMENT_ID;
    private static final String QUERY_EXPERIMENT_BY_ID_EXISTS = String.format(TEMPLATE_OBJECT_EXISTS, QUERY_EXPERIMENT_BY_ID);

    private static final String PROJECT_AND_EXPERIMENT_QUERY = "SELECT ed.id FROM xnat_experimentdata ed LEFT JOIN xnat_projectdata pd ON ed.project = pd.id ";

    private static final String BY_PRO_EXP_ID_WHERE = " WHERE ed.id= :experimentId AND pd.id = :projectId";

    private final String PROJECT_EXPERIMENT_QUERY = EXPERIMENT_SUB_QUERY1 + BY_PRO_ID_WHERE1 + EXPERIMENT_SUB_QUERY2 + BY_PRO_ID_WHERE2 + EXPERIMENT_SUB_QUERY3;

    private final String PROJECT_SUBJECT_EXPERIMENT_QUERY = EXPERIMENT_SUB_QUERY1 + BY_PRO_SUB_ID_WHERE + EXPERIMENT_SUB_QUERY2 + BY_PRO_SUB_ID_WHERE2 + EXPERIMENT_SUB_QUERY3;


    private static final String BY_PRO_SUB_ID_WHERE = "    SECURITY WHERE ((((xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId)) AND  \n" +
                                                      " ((xnat_subjectAssessorData1= :" + PARAM_SUBJECT_ID + "))) AND (( (xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId)) AND  \n" +
                                                      " ((xnat_subjectAssessorData1= :" + PARAM_SUBJECT_ID + ")))))";

    private static final String BY_PRO_SUB_ID_WHERE2 = "    SECURITY WHERE ((((xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId)) AND  \n" +
                                                       " ((xnat_subjectAssessorData1= :" + PARAM_SUBJECT_ID + "))) AND (( (xnat_experimentData14=:projectId) OR  (xnat_experimentData_share25= :projectId)) AND   \n" +
                                                       " ((xnat_subjectAssessorData1= :" + PARAM_SUBJECT_ID + ")))))";


    private static final String BY_PRO_ID_WHERE1 = "   SECURITY WHERE ((( (xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId))) AND \n" +
                                                   " (( (xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId))))) ";


    private static final String BY_PRO_ID_WHERE2 = "    SECURITY WHERE ((( (xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId))) AND \n" +
                                                   " (( (xnat_experimentData14= :projectId) OR  (xnat_experimentData_share25= :projectId))))) ";


    private static final String EXPERIMENT_SUB_QUERY1 = " SELECT table0.id AS id, xnat_experimentData.xnatSubjectAssessorDataId AS xnatSubjectAssessorDataId, \n" +
                                                        "xnat_experimentData.project AS project, xnat_experimentData.date AS date, xnat_experimentData.xsiType AS xsiType, \n" +
                                                        "xnat_experimentData.label AS label,xnat_experimentData.insertDate AS insertDate \n" +
                                                        "FROM (SELECT SEARCH.* FROM (SELECT DISTINCT ON (id) * FROM (SELECT table0.id AS id, table0.project AS xnat_experimentData14, \n" +
                                                        "table2.project AS xnat_experimentData_share25, xnat_subjectAssessorData.subject_id AS \n" +
                                                        "xnat_subjectAssessorData1 FROM xnat_subjectAssessorData xnat_subjectAssessorData   \n" +
                                                        "LEFT JOIN xnat_experimentData table0 ON xnat_subjectAssessorData.id=table0.id   \n" +
                                                        "LEFT JOIN xnat_experimentData_share table2 ON table0.id=table2.sharing_share_xnat_experimentDa_id)  ";

    private static final String EXPERIMENT_SUB_QUERY2 = " SECURITY LEFT JOIN xnat_subjectAssessorData SEARCH ON SECURITY.id=SEARCH.id) xnat_subjectAssessorData   \n" +
                                                        " LEFT JOIN xnat_experimentData table0 ON xnat_subjectAssessorData.id=table0.id \n" +
                                                        " LEFT JOIN (SELECT table0.id AS id FROM (SELECT SEARCH.* FROM (SELECT DISTINCT ON (id) * FROM (SELECT table0.id AS id, table0.project AS xnat_experimentData14, table2.project AS xnat_experimentData_share25, xnat_subjectAssessorData.subject_id AS xnat_subjectAssessorData1 FROM xnat_subjectAssessorData xnat_subjectAssessorData   \n" +
                                                        " LEFT JOIN xnat_experimentData table0 ON xnat_subjectAssessorData.id=table0.id   LEFT JOIN xnat_experimentData_share table2 ON table0.id=table2.sharing_share_xnat_experimentDa_id)    ";

    private static final String EXPERIMENT_SUB_QUERY3    = "  SECURITY LEFT JOIN xnat_subjectAssessorData SEARCH ON SECURITY.id=SEARCH.id) xnat_subjectAssessorData   \n" +
                                                           " LEFT JOIN xnat_experimentData table0 ON xnat_subjectAssessorData.id=table0.id) AS map_xnat_experimentData ON table0.id=map_xnat_experimentData.id \n" +
                                                           " LEFT JOIN (SELECT xnat_experimentData.id AS xnatSubjectAssessorDataId, xnat_experimentData.project AS project, xnat_experimentData.date AS date, table1.element_name AS xsiType, xnat_experimentData.label AS label, table2.insert_date AS insertDate   \n" +
                                                           " FROM xnat_experimentData xnat_experimentData   \n" +
                                                           " LEFT JOIN xdat_meta_element table1 ON xnat_experimentData.extension=table1.xdat_meta_element_id   \n" +
                                                           " LEFT JOIN xnat_experimentData_meta_data table2 ON xnat_experimentData.experimentData_info=table2.meta_data_id) AS xnat_experimentData ON map_xnat_experimentData.id=xnat_experimentData.xnatSubjectAssessorDataId";
    private static final String QUERY_SUBJECT_EXPERIMENT = "SELECT " +
                                                           "    x.id " +
                                                           "FROM " +
                                                           "    xnat_experimentdata x " +
                                                           "        LEFT JOIN xnat_subjectassessordata a ON x.id = a.id " +
                                                           "        LEFT JOIN xnat_subjectdata s ON a.subject_id = s.id " +
                                                           "        LEFT JOIN xnat_projectparticipant p ON s.id = p.subject_id " +
                                                           "        LEFT JOIN xnat_experimentdata_share es ON x.id = es.sharing_share_xnat_experimentda_id AND p.project = es.project " +
                                                           "WHERE " +
                                                           "    s.id = :" + PARAM_SUBJECT_ID + " AND " +
                                                           "    (:" + PARAM_ID_OR_LABEL + " IN (x.id, x.label) OR :" + PARAM_ID_OR_LABEL + " = es.label)";

    private final NamedParameterJdbcTemplate _template;
    private final PipelineService            _pipelineService;

    private       XnatSubjectassessordataI  expt;
    private final DataTypeAwareEventService _eventService;
}

