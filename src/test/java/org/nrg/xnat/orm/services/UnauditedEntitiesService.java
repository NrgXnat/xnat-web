package org.nrg.xnat.orm.services;

import org.nrg.xnat.orm.entities.UnauditedEntity;

public interface UnauditedEntitiesService {
    UnauditedEntity create(final UnauditedEntity entity);

    UnauditedEntity retrieve(final long id);

    void update(final UnauditedEntity entity);

    void delete(final long id);
}
