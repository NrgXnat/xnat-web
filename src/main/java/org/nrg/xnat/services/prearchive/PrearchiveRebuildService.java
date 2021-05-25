package org.nrg.xnat.services.prearchive;

import org.nrg.xft.security.UserI;
import org.nrg.xnat.dto.prearchive.PrearchiveDto;
import org.springframework.web.multipart.MultipartFile;

public interface PrearchiveRebuildService {
	
	PrearchiveDto createPrarchiveRebuild(UserI user, String src, MultipartFile file);
}
