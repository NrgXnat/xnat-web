package org.nrg.xnat.orm.services;

import org.nrg.xnat.orm.entities.AuditedEntity;

public interface AuditedEntitiesService {
    AuditedEntity create(final AuditedEntity entity);

    AuditedEntity retrieve(final long id);

    void update(final AuditedEntity entity);

    void delete(final long id);
}
