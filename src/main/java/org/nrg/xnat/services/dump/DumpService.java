package org.nrg.xnat.services.dump;

import java.util.List;

import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.dicom.DicomSummary;
import org.nrg.xnat.helpers.dicom.DicomSummaryHeaderDump;
import org.nrg.xnat.helpers.ecat.EcatSummary;

public interface DumpService {

	 List<DicomSummary> findAllDicomSummary(UserI user, String src, String summary, String[] fieldVals) throws Exception;
	
	 List<EcatSummary> findAllEcatSummary(UserI user, String src, String summary, String[] fieldVals) throws Exception;
}
