package org.nrg.xnat.orm.services.impl.hibernate;

import org.nrg.xnat.orm.entities.AuditedEntity;
import org.nrg.xnat.orm.repositories.AuditedEntityRepository;
import org.nrg.xnat.orm.services.AuditedEntitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HibernateAuditedEntitiesService implements AuditedEntitiesService {
    @Autowired
    public HibernateAuditedEntitiesService(final AuditedEntityRepository repository) {
        _repository = repository;
    }

    @Override
    public AuditedEntity create(final AuditedEntity entity) {
        return _repository.create(entity);
    }

    @Override
    public AuditedEntity retrieve(final long id) {
        return _repository.retrieve(id);
    }

    @Override
    public void update(final AuditedEntity entity) {
        _repository.update(entity);
    }

    @Override
    public void delete(final long id) {
        _repository.delete(id);
    }

    private final AuditedEntityRepository _repository;
}
