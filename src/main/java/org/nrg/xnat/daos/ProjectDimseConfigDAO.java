package org.nrg.xnat.daos;

import org.nrg.framework.orm.hibernate.AbstractHibernateDAO;
import org.nrg.xnat.entities.ProjectDimseConfig;

public class ProjectDimseConfigDAO extends AbstractHibernateDAO<ProjectDimseConfig> {
    public ProjectDimseConfig getByProjectId(final String projectId) {
        return findByUniqueProperty("projectId", projectId);
    }
}
