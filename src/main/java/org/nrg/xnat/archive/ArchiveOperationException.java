package org.nrg.xnat.archive;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.nio.file.Path;

@Getter
@Accessors(prefix = "_")
public class ArchiveOperationException extends Exception {
    private final Path _path;

    public ArchiveOperationException(final Path path, final String message) {
        super(message);
        _path = path;
    }

    public ArchiveOperationException(final Path path, final String message, final Throwable throwable) {
        super(message, throwable);
        _path = path;
    }
}
