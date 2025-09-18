package org.nrg.xnat.entities;

import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;
import org.nrg.xnat.services.dicom.ProjectDimseConfigService.Availability;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Project configuration for DIMSE QR operations:
 *   - project ID
 *   - availability of data in this project for QR access:
 *       null/default: use site default
 *       true        : allow
 *       false       : exclude
 */
@Entity
public class ProjectDimseConfig extends AbstractHibernateEntity {
    @Column(unique = true, nullable = false)
    public String getProjectId() { return _projectId; }

    public void setProjectId(final String projectId) { _projectId = projectId; }

    @Column
    public Availability getQrScpAvailable() { return _qrScpAvailable; }

    public void setQrScpAvailable(final Availability available) { _qrScpAvailable = available; }

    private String _projectId;
    private Availability _qrScpAvailable;
}
