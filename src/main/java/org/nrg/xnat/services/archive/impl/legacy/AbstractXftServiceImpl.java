package org.nrg.xnat.services.archive.impl.legacy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;
import java.util.List;

@Getter(AccessLevel.PROTECTED)
@Accessors(prefix = "_")
@Slf4j
public abstract class AbstractXftServiceImpl {
    protected AbstractXftServiceImpl(final NamedParameterJdbcTemplate template) {
        _template = template;
    }

    protected XftType getXftType(final String itemId) {
        return XftType.values[_template.queryForObject(QUERY_GET_XFT_TYPE, new MapSqlParameterSource("candidate", itemId), Integer.class)];
    }

    protected ArchivableItem getArchivableItem(final UserI user, final String itemId) throws NotFoundException {
        switch (getXftType(itemId)) {
            case Project:
                return XnatProjectdata.getProjectByIDorAlias(itemId, user, true);

            case Subject:
                return XnatSubjectdata.getXnatSubjectdatasById(itemId, user, true);

            case Experiment:
                return XnatExperimentdata.getXnatExperimentdatasById(itemId, user, true);

            default:
                throw new NotFoundException("Found no project, subject, or experiment with ID " + itemId);
        }
    }

    protected List<XnatAbstractresourceI> getArchivableItemResources(final UserI user, final String itemId) throws NotFoundException {
        final ArchivableItem item = getArchivableItem(user, itemId);
        if (item instanceof XnatProjectdata) {
            return ((XnatProjectdata) item).getResources_resource();
        }
        if (item instanceof XnatSubjectdata) {
            return ((XnatSubjectdata) item).getResources_resource();
        }
        if (item instanceof XnatExperimentdata) {
            return ((XnatExperimentdata) item).getResources_resource();
        }
        // This should probably be an exception but it should also never reach this point.
        return Collections.emptyList();
    }


    private static final String    QUERY_GET_XFT_TYPE = "SELECT " +
                                                        "    CASE " +
                                                        "        WHEN (SELECT EXISTS(SELECT id FROM xnat_projectdata WHERE id = :candidate))    THEN 0 " +
                                                        "        WHEN (SELECT EXISTS(SELECT id FROM xnat_subjectdata WHERE id = :candidate))    THEN 1 " +
                                                        "        WHEN (SELECT EXISTS(SELECT id FROM xnat_experimentdata WHERE id = :candidate)) THEN 2 " +
                                                        "        ELSE 3 " +
                                                        "    END AS type";

    private final NamedParameterJdbcTemplate _template;
}
