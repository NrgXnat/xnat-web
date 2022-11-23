package org.nrg.xnat.services.archive.impl.hibernate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.nrg.action.ServerException;
import org.nrg.xdat.om.XnatResource;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.exception.InvalidReference;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.FileUtils;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.entities.ResourceScanRequest;
import org.nrg.xnat.services.archive.ResourceMitigationReport;
import org.nrg.xnat.services.archive.ResourceMitigationReport.ResourceMitigationReportBuilder;
import org.nrg.xnat.services.archive.ResourceScanReport;
import org.nrg.xnat.utils.CatalogUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This is not the class you're looking for.
 */
@Slf4j
public class ResourceRepairHelper implements Callable<ResourceMitigationReport> {
    private static final Comparator<File> FILES_BY_DATE = Comparator.comparing(file -> {
        try {
            return Files.readAttributes(file.toPath().toAbsolutePath(), BasicFileAttributes.class).creationTime();
        } catch (IOException e) {
            return null;
        }
    });

    private final ResourceScanRequest _request;
    private final Path _cachePath;
    private final PersistentWorkflowI _workflow;
    private final UserI _requestor;

    public ResourceRepairHelper(final ResourceScanRequest request,
                                final Path cachePath,
                                final PersistentWorkflowI workflow,
                                final UserI requestor) {
        _request = request;
        _cachePath = cachePath;
        _workflow = workflow;
        _requestor = requestor;
    }

    @Override
    public ResourceMitigationReport call() {
        final ResourceMitigationReportBuilder builder = ResourceMitigationReport.builder();
        builder.resourceScanRequestId(_request.getId());

        final ResourceScanReport scanReport = _request.getScanReport();

        final XnatResourcecatalog catalogResource = getCatalogResource();
        final CatalogUtils.CatalogData catalogData = getCatalogData(catalogResource);
        final Map<String, CatalogUtils.CatalogMapEntry> catalogMap = CatalogUtils.buildCatalogMap(catalogData);
        final Path sourcePath = Paths.get(catalogData.catPath);

        final Function<File, Path> backupMapper = file -> _cachePath.resolve(sourcePath.relativize(file.toPath()));
        final Function<Map.Entry<File, String>, Path> renameMapper = entry -> sourcePath.resolve(entry.getValue());

        try (final PrintWriter writer = new PrintWriter(new FileWriter(_cachePath.resolve("repair-" + FileUtils.getMsTimestamp() + ".log").toFile()))) {
            writer.println("Beginning repair from resource scan request " + _request.getId() + " for resource " + _request.getResourceId() + "\n");
            writer.println("Have the following items:");

            // ALL files get backed up: renames get backed and then renamed, all remaining are deleted after renaming finished.
            final Map<Path, Path> backups = new HashMap<>();
            final Map<Path, Path> moves = new HashMap<>();

            final List<File> badFiles = scanReport.getBadFiles();
            writer.format(" * %d bad files (unparsable, etc.)\n", badFiles.size());
            if (!badFiles.isEmpty()) {
                badFiles.forEach(badFile -> writer.println("    - " + badFile));
                writer.println("\nNote: bad files are not removed from the resource folder and are recorded for later review and possible mitigation.");
            }

            final Map<File, String> mismatchedFiles = scanReport.getMismatchedFiles();
            writer.format(" * %d mismatched files\n", mismatchedFiles.size());
            if (!mismatchedFiles.isEmpty()) {
                backups.putAll(mismatchedFiles.keySet().stream().collect(Collectors.toMap(File::toPath, backupMapper)));
                moves.putAll(mismatchedFiles.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toPath(), renameMapper)));
            }

            final Map<String, Map<File, String>> duplicates = scanReport.getDuplicates();
            writer.format(" * %d UIDs with duplicate files, %d duplicate files total\n\nIssues:\n\n", duplicates.size(), duplicates.values().stream().mapToInt(Map::size).sum());
            if (!duplicates.isEmpty()) {
                duplicates.forEach((uid, map) -> {
                    final Set<String> calculatedNames = new HashSet<>(map.values());
                    if (calculatedNames.size() > 1) {
                        writer.format(" * There are %d calculated names for UID %s, I can't fix this myself.", calculatedNames.size(), uid);
                    } else {
                        backups.putAll(map.keySet().stream().collect(Collectors.toMap(File::toPath, backupMapper)));
                        map.keySet().stream().max(FILES_BY_DATE).ifPresent(file -> moves.put(file.toPath(), sourcePath.resolve(map.get(file))));
                    }
                });
            }

            builder.removedFiles(backups.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toAbsolutePath().toFile(), entry -> entry.getValue().toAbsolutePath().toFile())));
            builder.movedFiles(moves.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().toAbsolutePath().toFile(), entry -> entry.getValue().toAbsolutePath().toFile())));

            writer.format("Actions:\n\nI have %d files to backup, %d files to rename\n\nBacking up files\n", backups.size(), moves.size());
            final Map<Pair<Path, Path>, String> backupErrors = new HashMap<>();
            final Map<Pair<Path, Path>, String> moveErrors = new HashMap<>();
            final Map<Path, String> deleteErrors = new HashMap<>();

            final List<Path> deletes = new ArrayList<>();
            backups.forEach((source, target) -> {
                try {
                    Files.copy(source, target);
                    writer.format(" * %s backed up to %s\n", source.toAbsolutePath(), target.toAbsolutePath());
                    // Don't add files we're going to move to the deletes list.
                    if (!moves.containsKey(source)) {
                        deletes.add(source);
                    }
                } catch (IOException e) {
                    backupErrors.put(Pair.of(source, target), makeErrorMessage(e));
                }
            });

            // Run deletes so files are cleared for potential moves with the same names.
            if (!deletes.isEmpty()) {
                writer.println("\nDeleting files");
                deletes.forEach(file -> {
                    try {
                        Files.delete(file);
                        removeFromCatalog(file, catalogData, catalogMap, sourcePath);
                    } catch (IOException e) {
                        deleteErrors.put(file, makeErrorMessage(e));
                    }
                });
            }

            if (!moves.isEmpty()) {
                writer.println("\nRenaming files");
                moves.forEach((source, target) -> {
                    try {
                        // We may end up with situations where a file should be
                        // "moved" onto itself. Don't do that.
                        if (!source.equals(target)) {
                            updateCatalogEntry(source, target, catalogMap, sourcePath);
                            Files.move(source, target);
                        }
                        writer.format(" * %s renamed to %s\n", source.toAbsolutePath(), target.toAbsolutePath());
                        deletes.remove(source);
                    } catch (IOException | InvalidReference e) {
                        moveErrors.put(Pair.of(source, target), makeErrorMessage(e));
                    }
                });
            }

            if (!backupErrors.isEmpty()) {
                writer.format("\n\nBackup errors:");
                backupErrors.forEach((pair, message) -> writer.println(" *  From: " + pair.getKey().toAbsolutePath() + "\n      To: " + pair.getValue().toAbsolutePath() + "\n   " + message));
                writer.println();
                builder.backupErrors(backupErrors);
            }
            if (!moveErrors.isEmpty()) {
                writer.format("\n\nMove errors:\n");
                moveErrors.forEach((pair, message) -> writer.println(" *  From: " + pair.getKey().toAbsolutePath() + "\n      To: " + pair.getValue().toAbsolutePath() + "\n   " + message));
                writer.println();
                builder.moveErrors(moveErrors);
            }
            if (!deleteErrors.isEmpty()) {
                writer.format("\n\nDelete errors:\n");
                deleteErrors.forEach((path, message) -> writer.println(" * " + path.toAbsolutePath() + " " + message));
                writer.println();
                builder.deleteErrors(deleteErrors);
            }

            // Write the catalog
            try {
                CatalogUtils.writeCatalogToFile(catalogData);
            } catch (Exception e) {
                log.error("Unable to save catalog after resource repair on {}", catalogResource.getUri(), e);
                builder.catalogWriteError(String.format("Unable to write catalog %s: %s %s. To access this data, " +
                        "you will need to perform a catalog refresh. It would be ideal to then download and " +
                        "re-import the data or pull scan data from headers so it is properly understood as DICOM.",
                        catalogResource.getUri(), e.getClass().getSimpleName(), e.getMessage()));
            }

            // Populate resource statistics
            if (CatalogUtils.populateStats(catalogResource, catalogData.catPath)) {
                try {
                    SaveItemHelper.authorizedSave(catalogResource, _requestor,
                            false, false, _workflow.buildEvent());
                } catch (Exception e) {
                    log.error("Unable to update resource statistics for {}",
                            catalogResource.getXnatAbstractresourceId(), e);
                    builder.resourceSaveError(String.format("Unable to update resource statistics on %s: %s %s. Running " +
                            "a catalog refresh requesting the populateStats operation will hopefully fix the issue",
                            catalogResource.getXnatAbstractresourceId(), e.getClass().getSimpleName(), e.getMessage()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return builder.build();
    }

    /**
     * An error message from exception
     *
     * @param e the exception
     * @return the error message
     */
    private String makeErrorMessage(Exception e) {
        return "Error: (" + e.getClass().getSimpleName() + ") " + e.getMessage();
    }

    /**
     * Get catalog resource XFT object
     *
     * @return the XnatResourcecatalog
     */
    private XnatResourcecatalog getCatalogResource() {
        final XnatResource resource = XnatResource.getXnatResourcesByXnatAbstractresourceId(_request.getResourceId(),
                null, false);
        if (!(resource instanceof XnatResourcecatalog)) {
            throw new RuntimeException(_request.getResourceId() + " is not a catalog resource");
        }
        return (XnatResourcecatalog) resource;
    }

    /**
     * Get catalog data for resource
     *
     * @param catalogResource the catalog resource
     * @return the catalog data object
     */
    private CatalogUtils.CatalogData getCatalogData(final XnatResourcecatalog catalogResource) {
        final CatalogUtils.CatalogData catalogData;
        try {
            catalogData = CatalogUtils.CatalogData.get(catalogResource, _request.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Catalog file does not exist for resource " +
                            catalogResource.getXnatAbstractresourceId()));
        } catch (ServerException e) {
            throw new RuntimeException(String.format("%s (%s)", e.getMessage(),
                    catalogResource.getXnatAbstractresourceId()));
        }
        return catalogData;
    }

    /**
     * Remove entry from catalog
     *
     * @param file the file whose catalog entry should be removed
     * @param catalogData the catalog data object
     * @param catalogMap map of relative paths to catalog entries
     * @param sourcePath the catalog path
     */
    private void removeFromCatalog(final Path file,
                                   final CatalogUtils.CatalogData catalogData,
                                   final Map<String, CatalogUtils.CatalogMapEntry> catalogMap,
                                   final Path sourcePath) {
        final CatalogUtils.CatalogMapEntry catalogMapEntry = catalogMap.get(sourcePath.relativize(file).toString());
        if (catalogMapEntry != null) {
            // If catalogMapEntry were null, we wouldn't really care bc we're removing it anyway
            catalogData.catBean.getEntries_entry().remove(catalogMapEntry.entry);
        }
    }

    /**
     * Update catalog entry with new URI and ID corresponding to a new filename
     *
     * @param source the original file
     * @param target the renamed file
     * @param catalogMap map of relative paths to catalog entries
     * @param sourcePath the catalog path
     *
     * @throws InvalidReference if an entry for the original file cannot be located in the catalog
     */
    private void updateCatalogEntry(final Path source,
                                    final Path target,
                                    final Map<String, CatalogUtils.CatalogMapEntry> catalogMap,
                                    final Path sourcePath) throws InvalidReference {
        final CatalogUtils.CatalogMapEntry catalogMapEntry = catalogMap
                .get(sourcePath.relativize(source).toString());
        if (catalogMapEntry == null) {
            throw new InvalidReference("Unable to locate catalog entry for original DICOM file " + source);
        }
        final String relativePath = sourcePath.relativize(target).toString();
        catalogMapEntry.entry.setUri(relativePath);
        catalogMapEntry.entry.setId(relativePath);
    }
}
