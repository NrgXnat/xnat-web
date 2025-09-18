package org.nrg.xnat.services.dicom.impl.hibernate;

import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.daos.ProjectDimseConfigDAO;
import org.nrg.xnat.entities.ProjectDimseConfig;
import org.nrg.xnat.exceptions.InvalidScpAvailabilityException;
import org.nrg.xnat.services.dicom.ProjectDimseConfigService;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;

public class HibernateProjectDimseConfigService
        extends AbstractHibernateEntityService<ProjectDimseConfig, ProjectDimseConfigDAO>
    implements ProjectDimseConfigService
{
    @Override
    public Availability getProjectQrAvailability(final String projectId) {
        final ProjectDimseConfig config = getDao().getByProjectId(projectId);
        return null == config ? Availability.DEFAULT : config.getQrScpAvailable();
    }

    @Override
    @Transactional
    public ProjectDimseConfig updateProjectQrAvailability(
            final String projectId, final Availability available
    ) throws NotFoundException {
        ProjectDimseConfig config = getDao().getByProjectId(projectId);
        if (null == config && null == available) {
            return null;
        }
        if (null == config) {
            // TODO: is there a cheaper way to check for project existence?
            // TODO: could we do this with lower privilege?
            final XnatProjectdata project = XnatProjectdata.getProjectByIDorAlias(projectId, Users.getAdminUser(), false);
            if (null == project) {
                throw new NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME, projectId);
            }
            config = new ProjectDimseConfig();
            config.setProjectId(projectId);
        }
        config.setQrScpAvailable(available);
        getDao().saveOrUpdate(config);
        return config;
    }

    @Override
    public ProjectDimseConfig updateProjectQrAvailability(
            final String projectId, final String available
    ) throws NotFoundException, InvalidScpAvailabilityException {
        return updateProjectQrAvailability(projectId, Availability.fromString(available));
    }

    @Override
    public Collection<String> getQrAvailableProjects(final UserI user) {
        // TODO
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void deleteProjectDimseConfig(String projectId) {
        final ProjectDimseConfig entity = getDao().getByProjectId(projectId);
        // ### FIXME: throw exception if doesn't exist
        getDao().delete(entity);
    }
}
