package org.nrg.xnat.tracking.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xnat.tracking.model.EventLogSummary;
import org.nrg.xnat.tracking.entities.EventTracking;
import org.nrg.xnat.tracking.model.EventLog;
import org.nrg.xnat.tracking.services.EventLogSummarizer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("org.nrg.xnat.tracking.model.StatusEventLog")
public class StatusEventLogSummarizer implements EventLogSummarizer {
    /**
     * {@inheritDoc}
     */
    @Override
    public EventLogSummary summarize(final List<EventTracking> eventTrackingList, final ObjectMapper objectMapper) {
        final EventLogSummary.EventLogSummaryBuilder builder = EventLogSummary.builder()
                .payload(eventTrackingList.stream()
                        .filter(entry -> !entry.hasCompleted())
                        .map(eventTrackingEntry -> {
                            try {
                                return objectMapper.readValue(eventTrackingEntry.getEventLog(), EventLog.class);
                            } catch (JsonProcessingException e) {
                                log.error("Issue parsing event tracking data {}", eventTrackingEntry.getId(), e);
                                return null;
                            }
                        })
                        .collect(Collectors.toList()));
        eventTrackingList.stream()
                .filter(EventTracking::hasCompleted)
                .findFirst()
                .ifPresent(eventTrackingEntry -> {
                    builder.finalMessage(eventTrackingEntry.getFinalMessage())
                            .succeeded(eventTrackingEntry.getSucceeded());
                });
        return builder.build();
    }
}
