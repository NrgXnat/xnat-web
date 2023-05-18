package org.nrg.xnat.services.cache.extractors;

public interface DataExtractor<P, T> {
    String PARAM_DATA_TYPE     = "dataType";
    String PARAM_EXPERIMENT_ID = "experimentId";
    String PARAM_PROJECT_ID    = "projectId";
    String PARAM_PROJECT_IDS   = "projectIds";
    String PARAM_SUBJECT_ID    = "subjectId";
    String PARAM_USERNAME      = "username";
    String PARAM_USER_IDS      = "userIds";

    String getCacheGroup();

    T extract(P parameters);
}
