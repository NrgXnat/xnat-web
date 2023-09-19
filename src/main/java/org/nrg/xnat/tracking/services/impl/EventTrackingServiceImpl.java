/*
 * web: org.nrg.xnat.tracking.services.impl.EventTrackingDataServiceImpl
 * XNAT http://www.xnat.org
 * Copyright (c) 2020, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.tracking.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.tracking.exceptions.SummarizerException;
import org.nrg.xnat.tracking.model.EventLogSummary;
import org.nrg.xnat.tracking.model.EventLog;
import org.nrg.xnat.tracking.model.TrackableEvent;
import org.nrg.xnat.tracking.services.EventTrackingHibernateService;
import org.nrg.xnat.tracking.services.EventTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Slf4j
@Service
public class EventTrackingServiceImpl implements EventTrackingService {
    private final EventTrackingHibernateService eventTrackingHibernateService;

    @Autowired
    public EventTrackingServiceImpl(final EventTrackingHibernateService eventTrackingHibernateService) {
        this.eventTrackingHibernateService = eventTrackingHibernateService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends EventLog> getPayloadByKey(final String key, UserI user)
            throws NotFoundException, JsonProcessingException, SummarizerException {
        return getSummaryForKey(key, user).getPayload();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventLogSummary getSummaryForKey(String key, UserI user)
            throws NotFoundException, JsonProcessingException, SummarizerException {
        return eventTrackingHibernateService.getSummaryForKey(key, user.getID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(TrackableEvent eventData) throws JsonProcessingException {
        eventTrackingHibernateService.create(eventData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanupOldEntries() {
        // Remove entries >1 month old
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        eventTrackingHibernateService.deleteEntriesOlderThan(cal.getTime());
    }
}
