package org.nrg.xnat.services.prearchive;

import java.util.List;
import java.util.Map;

import org.nrg.xft.security.UserI;
import org.nrg.xnat.dto.prearchive.PrearchiveDto;

public interface PrearchiveService {

	List<PrearchiveDto> findAllPrearchives(UserI user, String projectId);
}
