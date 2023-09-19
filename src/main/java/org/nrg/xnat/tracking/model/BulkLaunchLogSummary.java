package org.nrg.xnat.tracking.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@JsonInclude
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class BulkLaunchLogSummary extends EventLogSummary {
    private int failureCount;
    private int successCount;
    private int total;
}
