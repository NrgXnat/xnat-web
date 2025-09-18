package org.nrg.xnat.services.dicom;

import org.nrg.framework.orm.hibernate.BaseHibernateService;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.entities.ProjectDimseConfig;
import org.nrg.xnat.exceptions.InvalidScpAvailabilityException;

import java.util.Collection;

public interface ProjectDimseConfigService extends BaseHibernateService<ProjectDimseConfig> {
    /**
     * Retrieve the configured QR availability for the named project.
     * @param projectId project ID
     * @return QR availability for the named project; DEFAULT if configuration or project does not exist
     */
    Availability getProjectQrAvailability(String projectId);

    /**
     * Set the QR availability for the named project.
     * @param projectId project ID
     * @param available availability
     * @return updated configuration
     * @throws NotFoundException if the named project does not exist
     */
    ProjectDimseConfig updateProjectQrAvailability(String projectId, Availability available) throws NotFoundException;

    /**
     * Set the QR availability for the named project.
     * @param projectId project ID
     * @param available availability: null or "default", "true", or "false"
     * @return updated configuration
     * @throws NotFoundException if the named project does not exist
     * @throws InvalidScpAvailabilityException if the provided availability value is invalid
     */
    ProjectDimseConfig updateProjectQrAvailability(String projectId, String available) throws NotFoundException, InvalidScpAvailabilityException;

    /**
     * Get IDs of all projects for which QR access is enabled.
     * @param user calling user
     * @param sitewideEnabled true if project data access is sitewide enabled
     * @return list of project IDs
     */
    Collection<String> getQrAvailableProjects(UserI user, boolean sitewideEnabled);

    /**
     * Delete the DIMSE QR SCP config for the named project
     * @param projectId project ID
     * @throws NotFoundException if no config exists for the named project
     */
    void deleteProjectDimseConfig(String projectId) throws NotFoundException;

    enum Availability {
        DEFAULT(null),
        AVAILABLE(true),
        EXCLUDED(false);

        private Availability(Boolean enabled) {
            _enabled = enabled;
        }

        private final Boolean _enabled;

        public boolean enabled(boolean sitewideEnabled) {
            return null == _enabled ? sitewideEnabled : _enabled;
        }

        public Boolean asBoolean() {
            return _enabled;
        };

        public static Availability fromString(final String available) throws InvalidScpAvailabilityException {
            if (null == available || "default".equalsIgnoreCase(available)) {
                return Availability.DEFAULT;
            }
            if ("true".equalsIgnoreCase(available)) {
                return Availability.AVAILABLE;
            }
            if ("false".equalsIgnoreCase(available)) {
                return Availability.EXCLUDED;
            }
            throw new InvalidScpAvailabilityException(available);
        }
    }
}
