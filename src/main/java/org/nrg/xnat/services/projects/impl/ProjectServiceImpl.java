package org.nrg.xnat.services.projects.impl;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.archive.impl.legacy.AbstractXftServiceImpl;
import org.nrg.xnat.services.projects.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ProjectServiceImpl extends AbstractXftServiceImpl implements ProjectService {
    @Autowired
    public ProjectServiceImpl(final NamedParameterJdbcTemplate template) {
        super(template);
    }

    @Override
    public List<XnatProjectdata> getAll(final UserI user) {
        return XnatProjectdata.getAllXnatProjectdatas(user, false);
    }

    @Override
    public XnatProjectdata findById(final UserI user, final String projectId) {
        if (Objects.nonNull(projectId)) {
            return XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
        } else {
            throw new NullPointerException("ProjectId is Null");
        }
    }

    @Override
    public XnatProjectdata create(final UserI user, final XnatProjectdata project) {
        log.debug("User {} is creating the project {}", user.getUsername(), project.getId());
        return null;
    }

    @Override
    public XnatProjectdata update(final UserI user, final XnatProjectdata project) {
        log.debug("User {} is updating the project {}", user.getUsername(), project.getId());
        return null;
    }

    @Override
    public void deleteById(final UserI user, final String projectId) {
        delete(user, findById(user, projectId));
    }

    @Override
    public void delete(final UserI user, final XnatProjectdata project) {
        log.debug("User {} is deleting the project {}", user.getUsername(), project.getId());
    }
}
