package org.nrg.xnat.tracking.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nrg.xnat.tracking.exceptions.SummarizerException;
import org.nrg.xnat.tracking.model.EventLogSummary;
import org.nrg.xnat.tracking.entities.EventTracking;

import java.util.List;

public interface EventLogSummarizer {
    /**
     * Summarize the EventLogs
     * @param eventTrackingList list of log entries
     * @param objectMapper the special object mapper that can deserialize EventLogs based on subtype
     * @return the summary
     */
    EventLogSummary summarize(final List<EventTracking> eventTrackingList, final ObjectMapper objectMapper) throws SummarizerException;
}
