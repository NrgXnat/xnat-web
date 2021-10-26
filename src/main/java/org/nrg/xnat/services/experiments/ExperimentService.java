package org.nrg.xnat.services.experiments;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.model.XnatExperimentdataI;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.model.util.XnatEventUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public interface ExperimentService {

    XnatExperimentdataI create(UserI user, XnatExperimentdataI xnatExperimentdata, String projectId, String subjectId, String xsiType, String allowDataDelete, XnatEventUtil event, boolean triggerPipelines, boolean supressEmails) throws NotFoundException;

    List<XnatExperimentdataI> findAll(UserI user) throws NotFoundException;

    Optional<XnatExperimentdataI> findById(UserI user, String experimentId) throws DataFormatException, NotFoundException;

    List<XnatExperimentdataI> findAllByProjectIdAndSubjectId(UserI user, String projectId, String subject) throws DataFormatException, NotFoundException;

    List<XnatExperimentdataI> findAllByProjectId(UserI user, String projectId) throws DataFormatException, NotFoundException;

    // TODO: This should be combined with findByProjectIdAndExperimentId() to something like findByProjectIdAndExperimentIdOrLabel()
    List<XnatExperimentdataI> findAllByProjectIdAndLabel(UserI user, String projectId, String label);

    XnatExperimentdataI update(UserI user, XnatExperimentdataI xnatExperimentdata, String experimentId, String projectId, String subjectId, String allowDataDelete, String label, String primary, String moveAssessors, boolean overwrite, String filepath, XnatEventUtil event, boolean fixScanTypes, boolean pullDataFromHeaders, boolean triggerPipelines, boolean supressEmails);

    void deleteById(UserI user, String projectId, String experimentId, String filepath, boolean removeFiles, XnatEventUtil event) throws DataFormatException, NotFoundException;

    Optional<XnatExperimentdataI> findByProjectIdAndExperimentId(UserI user, String projectId, String experimentId) throws DataFormatException, NotFoundException;

    String findExperimentIdByProjectSubjectAndIdOrLabel(UserI user, @Nullable String projectId, @Nullable String subjectId, String idOrLabel) throws DataFormatException, NotFoundException;

    String findExperimentIdBySubjectIdAndExperimentId(UserI user, String subjectId, String experimentId) throws DataFormatException, NotFoundException;
}
