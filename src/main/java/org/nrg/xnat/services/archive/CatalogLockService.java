package org.nrg.xnat.services.archive;

import org.nrg.framework.orm.hibernate.BaseHibernateService;
import org.nrg.xnat.entities.CatalogLock;

import java.util.ConcurrentModificationException;

public interface CatalogLockService extends BaseHibernateService<CatalogLock> {
    /**
     * Lock a catalog for writing
     *
     * @param resourceId the xnat_abstractResource_id of the catalog resource
     * @param checksum the checksum of the catalog when it was read (aka the catalog as it was before
     *                 I modified it to perform the write I'm about to perform
     * @return true if checksum was verified (meaning, the lock was free, and the checksum matched)
     *         false if the checksum could not be verified because the lock was stale
     * @throws ConcurrentModificationException if another thread or process holds the lock, or if the lock is free
     * but the checksum didn't match what was expected, meaning that another thread or process used the lock and wrote
     * the catalog since this thread or process read the catalog
     */
    boolean lockCatalog(int resourceId, String checksum) throws ConcurrentModificationException;

    /**
     * Unlock catalog for others to write
     *
     * @param resourceId the xnat_abstractResource_id of the catalog resource
     * @param checksum the checksum of the catalog just written
     */
    void unlockCatalog(int resourceId, String checksum);
}
