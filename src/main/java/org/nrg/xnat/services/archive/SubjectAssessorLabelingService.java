package org.nrg.xnat.services.archive;

import org.nrg.xdat.model.XnatSubjectassessordataI;
import org.nrg.xft.security.UserI;

import javax.annotation.Nullable;
import java.util.Map;

public interface SubjectAssessorLabelingService {
    /**
     * Determine a label for the session if not explicitly specified
     *
     * @param sad the subject assessor
     * @param parameters the parameters
     * @param user the user
     * @return the label or null if not applicable to your plugin
     */
    @Nullable
    String determineLabel(XnatSubjectassessordataI sad, Map<String, Object> parameters, UserI user);
}
