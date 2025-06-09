package org.nrg.xnat.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nrg.xnat.config.TestCatalogServiceConfig;
import org.nrg.xnat.daos.CatalogLockDAO;
import org.nrg.xnat.entities.CatalogLock;
import org.nrg.xnat.services.archive.CatalogLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ConcurrentModificationException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestCatalogServiceConfig.class)
@Transactional // roll back after each @Test
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TestCatalogLockService {

    private static final int RESOURCE_ID = 42;
    private static final String CHECKSUM_V1 = "abc123";
    private static final String CHECKSUM_V2 = "def456";

    @Autowired
    private CatalogLockService catalogLockService;
    @Autowired
    private CatalogLockDAO catalogLockDAO;

    /* -----------------------------------------------------------------------------------------------------------------
       1. BRAND-NEW LOCK ─────────────── creates row, returns true
       ----------------------------------------------------------------------------------------------------------------*/
    @Test
    public void lockCatalog_newLock_createsRowAndReturnsTrue() {
        boolean verified = catalogLockService.lockCatalog(RESOURCE_ID, CHECKSUM_V1);

        assertThat(verified, is(true));

        CatalogLock row = catalogLockDAO.findByResourceId(RESOURCE_ID);
        assertThat(row, notNullValue());
        assertThat(row.getChecksum(), is(CHECKSUM_V1));
        assertThat(row.getLockedUntil(), notNullValue());
    }

    /* -----------------------------------------------------------------------------------------------------------------
       2. ACTIVE LOCK ─────────────── throws ConcurrentModificationException
       ----------------------------------------------------------------------------------------------------------------*/
    @Test(expected = ConcurrentModificationException.class)
    public void lockCatalog_activeLock_throwsException() {
        // create an *active* lock
        catalogLockService.lockCatalog(RESOURCE_ID, CHECKSUM_V1);
        // now try to create another
        catalogLockService.lockCatalog(RESOURCE_ID, CHECKSUM_V1);
    }

    /* -----------------------------------------------------------------------------------------------------------------
       3. FREE LOCK + CHECKSUM MISMATCH  ─────────────── throws ConcurrentModificationException
       ----------------------------------------------------------------------------------------------------------------*/
    @Test(expected = ConcurrentModificationException.class)
    public void lockCatalog_freeLock_badChecksum() {
        catalogLockService.lockCatalog(RESOURCE_ID, CHECKSUM_V1);
        catalogLockService.unlockCatalog(RESOURCE_ID, CHECKSUM_V2);
        catalogLockService.lockCatalog(RESOURCE_ID, CHECKSUM_V1);
    }

    /* -----------------------------------------------------------------------------------------------------------------
       3. STALE LOCK + SAME CHECKSUM ─────────────── returns false & gets lock
       ----------------------------------------------------------------------------------------------------------------*/
    @Test
    public void lockCatalog_staleLockWithSameChecksum_locksAndReturnsFalse() {
        catalogLockDAO.create(CatalogLock.builder()
                .resourceId(RESOURCE_ID)
                .checksum(CHECKSUM_V1)
                .lockedUntil(LocalDateTime.now().minusMinutes(10)) // stale
                .build());

        boolean verified = catalogLockService.lockCatalog(RESOURCE_ID, CHECKSUM_V1);
        assertThat(verified, is(false));

        CatalogLock row = catalogLockDAO.findByResourceId(RESOURCE_ID);
        assertThat(row.getLockedUntil(), greaterThan(LocalDateTime.now()));
        assertThat(row.getChecksum(), is(CHECKSUM_V1));
    }

    /* -----------------------------------------------------------------------------------------------------------------
       4. STALE LOCK + DIFFERENT CHECKSUM ─────────────── returns false & gets lock
       ----------------------------------------------------------------------------------------------------------------*/
    public void lockCatalog_expiredLockWithDifferentChecksum_locksAndReturnsFalse() {
        catalogLockDAO.create(CatalogLock.builder()
                .resourceId(RESOURCE_ID)
                .checksum(CHECKSUM_V1)
                .lockedUntil(LocalDateTime.now().minusMinutes(10))
                .build());

        boolean verified = catalogLockService.lockCatalog(RESOURCE_ID, CHECKSUM_V2);
        assertThat(verified, is(false));

        CatalogLock row = catalogLockDAO.findByResourceId(RESOURCE_ID);
        assertThat(row.getLockedUntil(), greaterThan(LocalDateTime.now()));
        assertThat(row.getChecksum(), is(CHECKSUM_V2));
    }

    /* -----------------------------------------------------------------------------------------------------------------
       5. UNLOCK ─────────────── clears lockedUntil & updates checksum
       ----------------------------------------------------------------------------------------------------------------*/
    @Test
    public void unlockCatalog_clearsLockedUntilAndUpdatesChecksum() {
        // lock and unlock
        catalogLockService.lockCatalog(RESOURCE_ID, CHECKSUM_V1);
        catalogLockService.unlockCatalog(RESOURCE_ID, CHECKSUM_V2);

        CatalogLock row = catalogLockDAO.findByResourceId(RESOURCE_ID);
        assertThat(row.getLockedUntil(), nullValue());
        assertThat(row.getChecksum(), is(CHECKSUM_V2));
    }

//    /* -----------------------------------------------------------------------------------------------------------------
//       6. END-TO-END  CatalogUtils.writeCatalogFileAndSaveResource(…)  guards critical section
//          (no need for multiple threads; we just take the lock manually and prove a 2nd call can’t get it)
//       ----------------------------------------------------------------------------------------------------------------*/
//    @Test(expected = ConcurrentModificationException.class)
//    public void writeCatalogFileAndSaveResource_respectsLock() throws Exception {
//        // First call grabs the DB lock but *intentionally* never unlocks (simulate a long-running writer)
//        catalogLockService.lockCatalog(RESOURCE_ID, CHECKSUM_V1);
//
//        // Prepare a dummy CatalogData pointing at a temp file
//        File tmp = File.createTempFile("dummy-cat", ".xml");
//        CatalogUtils.CatalogData data = new CatalogUtils.CatalogData(tmp, null, false);
//        data.catRes.setXnatAbstractresourceId(RESOURCE_ID);
//        data.catFileChecksum = CHECKSUM_V2;   // pretend the checksum changed
//
//        // any non-null stubs will do for these collaborators
//        CatalogUtils.writeCatalogFileAndSaveResource(
//                data,
//                false,
//                new HashMap<>(),
//                null,
//                null);
//
//        /*
//           ───── expected path ─────
//           ⇒ writeCatalogFileAndSaveResource calls catalogLockService.lockCatalog(..) again
//           ⇒ service sees “active” lock → throws ConcurrentModificationException
//         */
//    }
}
