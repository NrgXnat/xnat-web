package org.nrg.xnat.services.dump;

import java.util.List;

import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.dicom.DicomSummary;
import org.nrg.xnat.helpers.dicom.DicomSummaryHeaderDump;

public interface DicomDumpService {

	 List<DicomSummary> findAll(UserI user, String src, String summary, String[] fieldVals) throws Exception;
}
