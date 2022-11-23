package org.nrg.xnat.services.archive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Value
@Accessors(prefix = "_")
@Slf4j
public class ResourceMitigationReport {
    @Builder
    public ResourceMitigationReport(final long resourceScanRequestId, final Map<File, File> movedFiles, final Map<File, File> removedFiles,
                                    final Map<Pair<Path, Path>, String> backupErrors, final Map<Pair<Path, Path>, String> moveErrors, final Map<Path, String> deleteErrors, final String catalogWriteError, final String resourceSaveError) {
        this(resourceScanRequestId, movedFiles, removedFiles, backupErrors, moveErrors, deleteErrors,
                catalogWriteError, resourceSaveError, -1, -1, -1);
    }

    @JsonCreator
    public ResourceMitigationReport(final @JsonProperty("resourceScanRequestId") long resourceScanRequestId,
                                    final @JsonProperty("movedFiles") Map<File, File> movedFiles,
                                    final @JsonProperty("removedFiles") Map<File, File> removedFiles,
                                    final @JsonProperty("backupErrors") Map<Pair<Path, Path>, String> backupErrors,
                                    final @JsonProperty("moveErrors") Map<Pair<Path, Path>, String> moveErrors,
                                    final @JsonProperty("deleteErrors") Map<Path, String> deleteErrors,
                                    final @JsonProperty("catalogWriteError") String catalogWriteError,
                                    final @JsonProperty("resourceSaveError") String resourceSaveError,
                                    final @JsonProperty("totalMovedFiles") int totalMovedFiles,
                                    final @JsonProperty("totalRemovedFiles") int totalRemovedFiles,
                                    final @JsonProperty("totalFileErrors") int totalFileErrors) {
        _resourceScanRequestId = resourceScanRequestId;
        _movedFiles            = Optional.ofNullable(movedFiles).orElseGet(Collections::emptyMap);
        _removedFiles          = Optional.ofNullable(removedFiles).orElseGet(Collections::emptyMap);
        _backupErrors          = Optional.ofNullable(backupErrors).orElseGet(Collections::emptyMap);
        _moveErrors            = Optional.ofNullable(moveErrors).orElseGet(Collections::emptyMap);
        _deleteErrors          = Optional.ofNullable(deleteErrors).orElseGet(Collections::emptyMap);
        _catalogWriteError     = catalogWriteError;
        _resourceSaveError     = resourceSaveError;
        _totalMovedFiles       = totalMovedFiles == -1 ? _movedFiles.size() : totalMovedFiles;
        _totalRemovedFiles     = totalRemovedFiles == -1 ? _removedFiles.size() : totalRemovedFiles;
        _totalFileErrors       = totalFileErrors == -1 ?
                _backupErrors.size() + _moveErrors.size() + _deleteErrors.size() :
                totalFileErrors;
    }

    long _resourceScanRequestId;

    Map<File, File>               _movedFiles;
    Map<File, File>               _removedFiles;
    Map<Pair<Path, Path>, String> _backupErrors;
    Map<Pair<Path, Path>, String> _moveErrors;
    Map<Path, String>             _deleteErrors;
    String                        _catalogWriteError;
    String                        _resourceSaveError;
    int                           _totalMovedFiles;
    int                           _totalRemovedFiles;
    int                           _totalFileErrors;
}
