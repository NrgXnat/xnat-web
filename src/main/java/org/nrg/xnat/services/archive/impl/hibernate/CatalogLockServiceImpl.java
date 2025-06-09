package org.nrg.xnat.services.archive.impl.hibernate;

import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.nrg.xnat.daos.CatalogLockDAO;
import org.nrg.xnat.entities.CatalogLock;
import org.nrg.xnat.services.archive.CatalogLockService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ConcurrentModificationException;

@Transactional
public class CatalogLockServiceImpl extends AbstractHibernateEntityService<CatalogLock, CatalogLockDAO>
        implements CatalogLockService {

    /**
     * When a catalog is unlocked, it sets the lockedUntil to null, so this is just a failsafe if tomcat goes down or
     * something and the catalog is left locked.
     */
    private static final int LOCK_DURATION = 5;

    @Override
    public boolean lockCatalog(int resourceId, String checksum) {
        CatalogLock catalogLock = getDao().findByResourceId(resourceId);
        if (catalogLock == null) {
            create(CatalogLock.builder()
                    .resourceId(resourceId)
                    .checksum(checksum)
                    .lockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION))
                    .build());
            return true;
        } else {
            boolean checksumVerified = checkLockExpiration(checksum, catalogLock);
            catalogLock.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION));
            return checksumVerified;
        }
    }

    private static boolean checkLockExpiration(String checksum, CatalogLock catalogLock) {
        boolean checksumVerified;
        if (catalogLock.isFree()) {
            if (checksum == null || !checksum.equals(catalogLock.getChecksum())) {
                // TODO Should we check against filesystem here?
                throw new ConcurrentModificationException("Catalog has been modified since last read");
            }
            checksumVerified = true;
        } else if (catalogLock.isStale()) {
            // Lock is stale, this means checksum might be outdated - check against filesystem to be safe
            checksumVerified = false;
        } else {
            // Lock is active
            throw new ConcurrentModificationException("Catalog is locked");
        }
        return checksumVerified;
    }

    @Override
    public void unlockCatalog(int resourceId, String checksum) {
        CatalogLock catalogLock = getDao().findByResourceId(resourceId);
        if (catalogLock == null) {
            // This shouldn't happen
            create(CatalogLock.builder()
                    .resourceId(resourceId)
                    .checksum(checksum)
                    .build());
            return;
        }
        catalogLock.setLockedUntil(null);
        catalogLock.setChecksum(checksum);
    }
}
