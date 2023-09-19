package org.nrg.xnat.tracking.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.framework.orm.hibernate.BaseHibernateService;
import org.nrg.xnat.tracking.entities.EventTracking;
import org.nrg.xnat.tracking.exceptions.SummarizerException;
import org.nrg.xnat.tracking.model.EventLogSummary;
import org.nrg.xnat.tracking.model.TrackableEvent;

import java.util.Date;

public interface EventTrackingHibernateService extends BaseHibernateService<EventTracking> {
    /**
     * Remove entries older than expiration
     * @param expiration entries older than this date will be removed
     */
    void deleteEntriesOlderThan(Date expiration);

    /**
     * Create an event tracking data entry for the trackable event
     * @param eventData the trackable event
     * @throws JsonProcessingException if event payload cannot be serialized
     */
    void create(TrackableEvent eventData) throws JsonProcessingException;

    /**
     * Aggregate event tracking data entities into a summary pojo
     * @param key the event key
     * @param userId the user id
     * @return the summary pojo
     */
    EventLogSummary getSummaryForKey(String key, Integer userId) throws NotFoundException, JsonProcessingException, SummarizerException;
}
