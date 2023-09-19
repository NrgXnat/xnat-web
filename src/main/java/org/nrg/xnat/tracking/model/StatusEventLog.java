package org.nrg.xnat.tracking.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

@EventLogSubType("StatusEventLog")
@JsonInclude
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StatusEventLog extends EventLog {
    private String status;

    public StatusEventLog(final String status, final long eventTime, final @Nullable String message) {
        super(eventTime, message);
        this.status = status;
        setType("StatusEventLog");
    }
}
