package org.nrg.xnat.daos;

import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.orm.hibernate.AbstractHibernateDAO;
import org.nrg.xnat.entities.CatalogLock;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class CatalogLockDAO extends AbstractHibernateDAO<CatalogLock> {
    public CatalogLock findByResourceId(int resourceId) {
        return findByUniqueProperty("resourceId", resourceId);
    }
}
