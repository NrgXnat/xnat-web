package org.nrg.xnat.services.archive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Value
@Accessors(prefix = "_")
@Slf4j
public class ResourceMitigationReport {
    @Builder
    public ResourceMitigationReport(final long resourceScanRequestId, final Map<File, File> movedFiles, final Map<File, File> removedFiles,
                                    final Map<File, Pair<File, String>> backupErrors, final Map<File, Pair<File, String>> moveErrors, final Map<File, String> deleteErrors) {
        this(resourceScanRequestId, movedFiles, removedFiles, backupErrors, moveErrors, deleteErrors, -1, -1, -1);
    }

    @JsonCreator
    public ResourceMitigationReport(final @JsonProperty("resourceScanRequestId") long resourceScanRequestId,
                                    final @JsonProperty("movedFiles") Map<File, File> movedFiles,
                                    final @JsonProperty("removedFiles") Map<File, File> removedFiles,
                                    final @JsonProperty("backupErrors") Map<File, Pair<File, String>> backupErrors,
                                    final @JsonProperty("moveErrors") Map<File, Pair<File, String>> moveErrors,
                                    final @JsonProperty("deleteErrors") Map<File, String> deleteErrors,
                                    final @JsonProperty("totalMovedFiles") int totalMovedFiles,
                                    final @JsonProperty("totalRemovedFiles") int totalRemovedFiles,
                                    final @JsonProperty("totalErrors") int totalErrors) {
        _resourceScanRequestId = resourceScanRequestId;
        _movedFiles            = Optional.ofNullable(movedFiles).orElseGet(Collections::emptyMap);
        _removedFiles          = Optional.ofNullable(removedFiles).orElseGet(Collections::emptyMap);
        _backupErrors          = Optional.ofNullable(backupErrors).orElseGet(Collections::emptyMap);
        _moveErrors            = Optional.ofNullable(moveErrors).orElseGet(Collections::emptyMap);
        _deleteErrors          = Optional.ofNullable(deleteErrors).orElseGet(Collections::emptyMap);
        _totalMovedFiles       = totalMovedFiles == -1 ? _movedFiles.size() : totalMovedFiles;
        _totalRemovedFiles     = totalRemovedFiles == -1 ? _removedFiles.size() : totalRemovedFiles;
        _totalErrors           = totalErrors == -1 ? _backupErrors.size() + _moveErrors.size() + _deleteErrors.size() : totalErrors;
    }

    long _resourceScanRequestId;

    Map<File, File>               _movedFiles;
    Map<File, File>               _removedFiles;
    Map<File, Pair<File, String>> _backupErrors;
    Map<File, Pair<File, String>> _moveErrors;
    Map<File, String>             _deleteErrors;
    int                           _totalMovedFiles;
    int                           _totalRemovedFiles;
    int                           _totalErrors;
}
