package org.nrg.xnat.tracking.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.List;

@JsonInclude
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class EventLogSummary {
    private String key;
    @Nullable private List<? extends EventLog> payload;
    @Nullable private Boolean succeeded;
    @Nullable private String finalMessage;
}
