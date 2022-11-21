package org.nrg.xnat.services.archive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.util.*;

@Value
@Accessors(prefix = "_")
@Slf4j
public class ResourceScanReport {
    @Builder
    public ResourceScanReport(final long resourceScanRequestId, final int totalEntries, final Set<String> uids, final List<File> badFiles, final Map<File, String> mismatchedFiles, final Map<String, Map<File, String>> duplicates) {
        this(resourceScanRequestId, totalEntries, -1, -1, -1, -1, uids, badFiles, mismatchedFiles, duplicates);
    }

    @JsonCreator
    public ResourceScanReport(final @JsonProperty("resourceScanRequestId") long resourceScanRequestId,
                              final @JsonProperty("totalEntries") int totalEntries,
                              final @JsonProperty("totalUids") int totalUids,
                              final @JsonProperty("totalBadFiles") int totalBadFiles,
                              final @JsonProperty("totalMismatchedFiles") int totalMismatchedFiles,
                              final @JsonProperty("totalDuplicates") int totalDuplicates,
                              final @JsonProperty("uids") Set<String> uids,
                              final @JsonProperty("badFiles") List<File> badFiles,
                              final @JsonProperty("mismatchedFiles") Map<File, String> mismatchedFiles,
                              final @JsonProperty("duplicates") Map<String, Map<File, String>> duplicates) {
        Validate.isTrue(resourceScanRequestId > 0, "You must specify a valid resource scan request ID for each report");
        _resourceScanRequestId = resourceScanRequestId;
        _totalEntries          = totalEntries;
        _uids                  = Optional.ofNullable(uids).orElseGet(Collections::emptySet);
        _badFiles              = Optional.ofNullable(badFiles).orElseGet(Collections::emptyList);
        _mismatchedFiles       = Optional.ofNullable(mismatchedFiles).orElseGet(Collections::emptyMap);
        _duplicates            = Optional.ofNullable(duplicates).orElseGet(Collections::emptyMap);
        _totalUids             = totalUids == -1 ? _uids.size() : totalUids;
        _totalBadFiles         = totalBadFiles == -1 ? _badFiles.size() : totalBadFiles;
        _totalMismatchedFiles  = totalMismatchedFiles == -1 ? _mismatchedFiles.size() : totalMismatchedFiles;
        _totalDuplicates       = totalDuplicates == -1 ? _duplicates.size() : totalDuplicates;
    }

    long _resourceScanRequestId;

    int _totalEntries;

    int _totalUids;

    int _totalBadFiles;

    int _totalMismatchedFiles;

    int _totalDuplicates;

    Set<String> _uids;

    /**
     * Contains a list of files that couldn't be parsed as DICOM objects.
     */
    List<File> _badFiles;

    /**
     * Contains a map of files whose file names don't match the name generated from the DICOM metadata. The value in the
     * map is the generated file name. There may be other files where the file name doesn't match the generated file name,
     * but that also represent a duplicated UID, so it's presumed one of the other files for that UID does match the
     * generated file name.
     */
    Map<File, String> _mismatchedFiles;

    /**
     * Contains a map of SOP instance UIDs to a map of files and generated file names that are identified with each UID.
     * Each UID and file name  reference a list of multiple files that contain the same DICOM data. These files are
     * candidates for normalization and culling.
     */
    Map<String, Map<File, String>> _duplicates;
}
