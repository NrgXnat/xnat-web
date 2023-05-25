package org.nrg.xnat.services.cache.extractors;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.base.auto.AutoXnatProjectdata;
import org.nrg.xnat.services.cache.DefaultUserProjectCache;
import org.nrg.xnat.services.cache.UserProjectCache;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProjectExtractor extends AbstractUserProjectCacheDataExtractor<String, XnatProjectdata> {
    public ProjectExtractor(final @Lazy UserProjectCache cache, final NamedParameterJdbcTemplate template) {
        super(cache, DefaultUserProjectCache.CACHE_PROJECTS, template, null);
    }

    @Override
    public XnatProjectdata extract(final Object... parameters) {
        if (parameters.length == 0) {
            return null;
        }

        final String projectId = (String) parameters[0];
        log.info("Extracting project for ID '{}'", projectId);
        // Note: this uses AutoXnatProjectdata.getXnatProjectdatasById() because that doesn't try to access the cache.
        // Calling XnatProjectdata.getXnatProjectdatasById() or BaseXnatProjectdata.getXnatProjectdatasById() will lead
        // to stack overflow errors from circular recursive calls.
        return AutoXnatProjectdata.getXnatProjectdatasById(projectId, null, false);
    }
}
