package org.nrg.xnat.services.archive.impl.hibernate;

import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.orm.hibernate.AbstractHibernateDAO;
import org.nrg.xnat.entities.ResourceScanRequest;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class ResourceScanRequestRepository extends AbstractHibernateDAO<ResourceScanRequest> {
    public List<ResourceScanRequest> findByProjectId(final String projectId) {
        return Optional.ofNullable(findByProperty("projectId", projectId)).orElseGet(Collections::emptyList);
    }

    public Optional<ResourceScanRequest> findByResourceId(final int resourceId) {
        final List<ResourceScanRequest> requests = findByProperty("resourceId", resourceId);
        return requests == null ? Optional.empty() : Optional.of(requests.get(0));
    }
}
