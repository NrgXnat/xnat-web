package org.nrg.xnat.tracking.model;

import org.nrg.framework.event.EventI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface TrackableEvent extends EventI {
    /**
     * Return a unique identifier for tracking the event
     * @return the unique ID
     */
    @Nonnull
    String getTrackingId();

    /**
     * Return the user tracking the event
     * @return the user ID
     */
    @Nonnull
    Integer getUserId();

    /**
     * Has the event succeeded?
     * @return T/F
     */
    boolean isSuccess();

    /**
     * Has the event completed?
     * @return T/F
     */
    boolean isCompleted();

    /**
     * Return a status message or null
     * @return the status message or null
     */
    @Nullable
    String getMessage();

    /**
     * Return the event log. Extend the event log class to add custom information to this object.
     * @return the event log
     */
    @Nullable
    EventLog getEventLog();

    /**
     * Return the time the event was triggered
     * @return the time the event was triggered
     */
    long getEventTime();
}
