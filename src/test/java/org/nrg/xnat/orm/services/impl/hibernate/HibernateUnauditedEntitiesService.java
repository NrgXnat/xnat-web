package org.nrg.xnat.orm.services.impl.hibernate;

import org.nrg.xnat.orm.entities.UnauditedEntity;
import org.nrg.xnat.orm.repositories.UnauditedEntityRepository;
import org.nrg.xnat.orm.services.UnauditedEntitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HibernateUnauditedEntitiesService implements UnauditedEntitiesService {
    @Autowired
    public HibernateUnauditedEntitiesService(final UnauditedEntityRepository repository) {
        _repository = repository;
    }

    @Override
    public UnauditedEntity create(final UnauditedEntity entity) {
        return _repository.create(entity);
    }

    @Override
    public UnauditedEntity retrieve(final long id) {
        return _repository.retrieve(id);
    }

    @Override
    public void update(final UnauditedEntity entity) {
        _repository.update(entity);
    }

    @Override
    public void delete(final long id) {
        _repository.delete(id);
    }

    private final UnauditedEntityRepository _repository;
}
