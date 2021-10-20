package org.nrg.xnat.archive;

import java.nio.file.Path;

public class CatalogOperationException extends ArchiveOperationException {
    public CatalogOperationException(final Path catalogPath, final String message) {
        super(catalogPath, message);
    }

    public CatalogOperationException(final Path catalogPath, final String message, final Throwable throwable) {
        super(catalogPath, message, throwable);
    }
}
