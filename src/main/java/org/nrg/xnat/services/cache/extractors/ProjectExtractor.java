package org.nrg.xnat.services.cache.extractors;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xnat.services.cache.DefaultUserProjectCache;
import org.nrg.xnat.services.cache.UserProjectCache;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProjectExtractor extends AbstractDataExtractor<UserProjectCache, String, XnatProjectdata> {
    public ProjectExtractor(final UserProjectCache cache, final NamedParameterJdbcTemplate template) {
        super(cache, DefaultUserProjectCache.CACHE_PROJECTS, template, null);
    }

    @Override
    public XnatProjectdata extract(final Object... parameters) {
        if (parameters.length == 0) {
            return null;
        }

        final String projectId = (String) parameters[0];
        log.info("Extracting project for ID '{}'", projectId);
        return XnatProjectdata.getProjectByIDorAlias(projectId, null, false);
    }
}
