package org.nrg.xnat.tracking.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xnat.tracking.entities.EventTracking;
import org.nrg.xnat.tracking.exceptions.SummarizerException;
import org.nrg.xnat.tracking.model.BulkLaunchLog;
import org.nrg.xnat.tracking.model.BulkLaunchLogSummary;
import org.nrg.xnat.tracking.model.EventLog;
import org.nrg.xnat.tracking.model.EventLogSummary;
import org.nrg.xnat.tracking.services.EventLogSummarizer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service("org.nrg.xnat.tracking.model.BulkLaunchLog")
public class BulkLaunchLogSummarizer implements EventLogSummarizer {
    private static final String INITIAL_EVENT_KEY = "initial";
    private static final String UNKNOWN_ITEM_KEY = "unknown";

    /**
     * {@inheritDoc}
     */
    @Override
    public EventLogSummary summarize(final List<EventTracking> eventTrackingList, final ObjectMapper objectMapper) throws SummarizerException {
        final Map<String, BulkLaunchLog> logMap = getLatestLogPerItem(eventTrackingList, objectMapper);
        final BulkLaunchLog initialLog = getInitialLog(logMap);

        int failureCount = 0;
        int successCount = 0;
        int steps = initialLog.getSteps();
        final Map<String, Integer> itemSteps = new HashMap<>();
        for (final BulkLaunchLog bulkLaunchLog : logMap.values()) {
            if (bulkLaunchLog == null || bulkLaunchLog.isInitialEvent()) {
                continue;
            }
            final BulkLaunchLog.WorkflowLog workflowLog = bulkLaunchLog.getWorkflowLog();
            if (workflowLog == null) {
                // executor service failures will be stored this way
                if (!bulkLaunchLog.isSuccess()) {
                    failureCount++;
                }
                continue;
            }
            final String status = workflowLog.getStatus();
            if (status.startsWith(PersistentWorkflowUtils.FAILED)) {
                // For orchestrations: as soon as a failure is encountered, the orchestration is aborted, so we don't
                // need to consider steps.
                failureCount++;
            } else if (status.equals(PersistentWorkflowUtils.COMPLETE)) {
                String itemId = workflowLog.getItemId();
                int count = itemSteps.getOrDefault(itemId, 0);
                itemSteps.put(itemId, ++count);
                if (count == steps) {
                    // If this is the last step, increment the success count
                    successCount++;
                }
            }
        }

        int total = initialLog.getTotal();
        final BulkLaunchLogSummary.BulkLaunchLogSummaryBuilder builder = BulkLaunchLogSummary.builder()
                .payload(new ArrayList<>(logMap.values()))
                .failureCount(failureCount)
                .successCount(successCount)
                .total(total);
        if (failureCount + successCount == total) {
            if (successCount == total) {
                builder.succeeded(true);
                builder.finalMessage("All jobs succeeded");
            } else {
                builder.succeeded(false);
                if (failureCount == total) {
                    builder.finalMessage("All jobs failed");
                } else {
                    builder.finalMessage(successCount + " jobs succeed / " + failureCount + " jobs failed");
                }
            }
        }
        return builder.build();
    }

    private Map<String, BulkLaunchLog> getLatestLogPerItem(final List<EventTracking> eventTrackingList, final ObjectMapper objectMapper) {
        return eventTrackingList.stream()
                .map(eventTracking -> trackingToLog(eventTracking, objectMapper))
                .filter(BulkLaunchLog.class::isInstance)
                .map(BulkLaunchLog.class::cast)
                .collect(Collectors.toMap(this::makeMapKeyForItem, Function.identity(),
                        BinaryOperator.maxBy(Comparator.comparingLong(BulkLaunchLog::getEventTime))));
    }

    private EventLog trackingToLog(final EventTracking eventTracking, final ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(eventTracking.getEventLog(), EventLog.class);
        } catch (JsonProcessingException e) {
            log.error("Issue parsing event tracking data {}", eventTracking.getId(), e);
            return null;
        }
    }

    private String makeMapKeyForItem(final BulkLaunchLog eventLog) {
        final BulkLaunchLog.WorkflowLog workflowLog = eventLog.getWorkflowLog();
        if (workflowLog == null) {
            return eventLog.isInitialEvent() ? INITIAL_EVENT_KEY : UNKNOWN_ITEM_KEY;
        } else {
            return workflowLog.getItemId();
        }
    }

    private BulkLaunchLog getInitialLog(final Map<String, BulkLaunchLog> logMap) throws SummarizerException {
        final BulkLaunchLog initialLog = logMap.get(INITIAL_EVENT_KEY);
        if (initialLog == null) {
            throw new SummarizerException("No initial event to determine total count");
        }
        return initialLog;
    }
}
