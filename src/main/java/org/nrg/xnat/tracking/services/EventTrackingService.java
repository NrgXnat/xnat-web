/*
 * web: org.nrg.xnat.tracking.services.EventTrackingDataService
 * XNAT http://www.xnat.org
 * Copyright (c) 2020, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.tracking.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.tracking.exceptions.SummarizerException;
import org.nrg.xnat.tracking.model.EventLogSummary;
import org.nrg.xnat.tracking.model.EventLog;
import org.nrg.xnat.tracking.model.TrackableEvent;

import java.util.List;

/**
 * Provides information about the event tracking
 */
public interface EventTrackingService {
    /**
     * Get list of EventTrackingPayloads by key
     * @param key the key
     * @param user the user
     * @return the payload
     * @throws NotFoundException if no event listener data exists for this key or user cannot access it
     */
    List<? extends EventLog> getPayloadByKey(final String key, UserI user) throws NotFoundException, JsonProcessingException, SummarizerException;

    /**
     * Aggregate event tracking data entities into a single pojo
     * @param key the key
     * @param user the user
     * @return the pojo
     * @throws NotFoundException if no event listener data exists for this key or user cannot access it
     */
    EventLogSummary getSummaryForKey(final String key, UserI user) throws NotFoundException, JsonProcessingException, SummarizerException;

    /**
     * Create eventTrackingData for TrackableEvent.
     *
     * @param eventData the trackable event
     * @throws JsonProcessingException if event payload cannot be serialized
     */
    void create(TrackableEvent eventData) throws JsonProcessingException;

    /**
     * Remove entries older than 1 month
     */
    void cleanupOldEntries();
}
