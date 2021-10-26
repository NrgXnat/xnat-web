package org.nrg.xnat.services.scans;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.model.util.XnatEventUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ScanService {

    List<XnatImagescandataI> findAllScanTypesByProjectId(UserI user, String projectId) throws DataFormatException, NotFoundException;

    List<XnatImagescandataI> findAllScanTypes(UserI user) throws NotFoundException;

    List<XnatImagescandataI> findAllByAssessedId(UserI user, String assessedId) throws DataFormatException, NotFoundException;

    Optional<XnatImagescandataI> findByAssessedIdAndScanId(UserI user, String assessedId, Integer scanId) throws DataFormatException, NotFoundException;

    List<XnatImagescandataI> findAllByProjectIdAndSubjectIdAndExperimentId(UserI user, String projectId, String subjectId, String experimentId) throws DataFormatException, NotFoundException;

    Optional<XnatImagescandataI> findByProjectIdAndSubjectIdAndExperimentIdAndScanId(UserI user, String projectId, String subjectId, String experimentId, Integer scanId) throws DataFormatException, NotFoundException;

    void deleteById(UserI user, String assessedId, Integer scanId, String filepath, boolean removeFiles, XnatEventUtil event) throws NotFoundException, DataFormatException, InitializationException;

    List<Map<String, String>> findAllScanners(UserI user, String scanTable, String projectId) throws InsufficientPrivilegesException;

    //Scan Quality Label Service
    String findAllScanQualityLabel(UserI user, String projectId) throws InitializationException;
}
