package org.nrg.xnat.export.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Mohana Ramaratmam
 *
 */
public class ExportUtils {

    @Nonnull
    public static String buildExportEventId(final @Nullable String project, final @Nullable String timestamp, final @Nonnull String exportHandler) {
        // JAVA8: This should be a default method implementation on the ArchiveOperationListener interface, probably as toString().
        return StringUtils.joinWith("/", project, timestamp, exportHandler);
    }

}
