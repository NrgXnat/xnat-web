package org.nrg.xnat.tracking.model;

import lombok.Data;
import org.nrg.xft.event.persist.PersistentWorkflowI;

import javax.annotation.Nonnull;

@Data
public class BulkLaunchEvent implements TrackableEvent {
    private final String id;
    private final Integer userId;
    private Integer n = null;
    private Integer steps = null;
    private boolean initialEvent = false;
    private PersistentWorkflowI workflow = null;
    private String message;
    private boolean success;
    private boolean completed;
    private long eventTime;

    public BulkLaunchEvent(String id, Integer userId) {
        this.id = id;
        this.userId = userId;
        this.eventTime = System.currentTimeMillis();
    }

    public BulkLaunchEvent(String bulkLaunchId, Integer userId, PersistentWorkflowI workflow) {
        this(bulkLaunchId, userId);
        this.workflow = workflow;
        this.eventTime = System.currentTimeMillis();
    }

    public static BulkLaunchEvent initial(String bulkLaunchId, Integer userId, int n) {
        return initial(bulkLaunchId, userId, n, 1);
    }

    public static BulkLaunchEvent initial(String bulkLaunchId, Integer userId, int n, int steps) {
        BulkLaunchEvent ble = new BulkLaunchEvent(bulkLaunchId, userId);
        ble.n = n;
        ble.steps = steps;
        ble.initialEvent = true;
        return ble;
    }

    public static BulkLaunchEvent executorServiceFailure(String bulkLaunchId, Integer userId, String message) {
        BulkLaunchEvent ble = new BulkLaunchEvent(bulkLaunchId, userId);
        ble.success = false;
        ble.completed = true;
        ble.message = message;
        return ble;
    }

    @Nonnull
    @Override
    public String getTrackingId() {
        return id;
    }

    @Nonnull
    @Override
    public Integer getUserId() {
        return userId;
    }

    @Override
    public long getEventTime() {
        return eventTime;
    }

    @Override
    public EventLog getEventLog() {
        BulkLaunchLog statusLog = new BulkLaunchLog();
        statusLog.setEventTime(eventTime);
        statusLog.setInitialEvent(initialEvent);
        if (n != null) {
            statusLog.setTotal(n);
        }
        if (steps != null) {
            statusLog.setSteps(steps);
        }
        if (workflow != null) {
            statusLog.setWorkflow(workflow);
            success = statusLog.isSuccess();
            completed = statusLog.isCompleted();
        } else if (completed) {
            statusLog.setSuccess(success);
            statusLog.setCompleted(completed);
            statusLog.setMessage(message);
        }
        return statusLog;
    }
}
