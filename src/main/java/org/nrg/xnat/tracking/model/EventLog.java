package org.nrg.xnat.tracking.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

@JsonInclude
@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
public class EventLog {
    private String type;
    private long eventTime;
    @Nullable private String message;

    public EventLog(final long eventTime, @Nullable final String message) {
        this.eventTime = eventTime;
        this.message = message;
    }
}
