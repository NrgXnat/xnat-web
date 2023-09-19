/*
 * web: org.nrg.xnat.tracking.daos.EventTrackingDataDao
 * XNAT http://www.xnat.org
 * Copyright (c) 2020, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.tracking.daos;

import org.nrg.framework.generics.GenericUtils;
import org.nrg.framework.orm.hibernate.AbstractHibernateDAO;
import org.nrg.xnat.tracking.entities.EventTracking;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class EventTrackingEntryDao extends AbstractHibernateDAO<EventTracking> {
    /**
     * Delete event tracking data last updated prior to expiration
     * @param expiration the expiration date
     */
    public void deleteEntriesLastUpdatedBefore(final Date expiration) {
        getSession().getNamedQuery(EventTracking.DELETE_OLD_ENTRIES)
                .setDate("expiration", expiration)
                .executeUpdate();
    }

    /**
     * Find entries by key and user id
     * @param key the unique event key
     * @param userId the user id
     * @return list of entries
     */
    public List<EventTracking> findByKeyAndUserId(final String key, final Integer userId) {
        return GenericUtils.convertToTypedList(getSession().getNamedQuery(EventTracking.FIND_BY_KEY_AND_USER)
                .setString("key", key)
                .setInteger("userId", userId)
                .list(), getParameterizedType());
    }
}
