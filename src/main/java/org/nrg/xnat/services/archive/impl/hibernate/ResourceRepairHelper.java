package org.nrg.xnat.services.archive.impl.hibernate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.nrg.xnat.entities.ResourceScanRequest;
import org.nrg.xnat.services.archive.ResourceMitigationReport;
import org.nrg.xnat.services.archive.ResourceMitigationReport.ResourceMitigationReportBuilder;
import org.nrg.xnat.services.archive.ResourceScanReport;

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
    private final Path                _cachePath;
    private final String              _repairId;

    public ResourceRepairHelper(final ResourceScanRequest request, final Path cachePath, final String repairId) {
        _request   = request;
        _cachePath = cachePath;
        _repairId  = repairId;
    }

    @Override
    public ResourceMitigationReport call() {
        final ResourceMitigationReportBuilder builder = ResourceMitigationReport.builder();
        builder.resourceScanRequestId(_request.getId());

        final ResourceScanReport scanReport = _request.getScanReport();

        final Path sourcePath = Paths.get(_request.getResourceUri()).getParent();

        final Function<File, Path>                    backupMapper = file -> _cachePath.resolve(sourcePath.relativize(file.toPath()));
        final Function<Map.Entry<File, String>, Path> renameMapper = entry -> sourcePath.resolve(entry.getValue());

        try (final PrintWriter writer = new PrintWriter(new FileWriter(_cachePath.resolve(_repairId + ".log").toFile()))) {
            writer.println("Beginning repair from resource scan request " + _request.getId() + " for resource " + _request.getResourceId() + "\n");
            writer.println("Have the following items:");

            // ALL files get backed up: renames get backed and then renamed, all remaining are deleted after renaming finished.
            final Map<Path, Path> backups = new HashMap<>();
            final Map<Path, Path> moves   = new HashMap<>();

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
            final Map<Pair<Path, Path>, Throwable> backupErrors = new HashMap<>();
            final Map<Pair<Path, Path>, Throwable> moveErrors   = new HashMap<>();
            final Map<Path, Throwable>             deleteErrors = new HashMap<>();

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
                    backupErrors.put(Pair.of(source, target), e);
                }
            });

            // Run deletes so files are cleared for potential moves with the same names.
            if (!deletes.isEmpty()) {
                writer.println("\nDeleting files");
                deletes.forEach(file -> {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        deleteErrors.put(file, e);
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
                            Files.move(source, target);
                        }
                        writer.format(" * %s renamed to %s\n", source.toAbsolutePath(), target.toAbsolutePath());
                        deletes.remove(source);
                    } catch (IOException e) {
                        moveErrors.put(Pair.of(source, target), e);
                    }
                });
            }

            if (!backupErrors.isEmpty()) {
                writer.format("\n\nBackup errors:");
                backupErrors.forEach((pair, throwable) -> writer.println(" *  From: " + pair.getKey().toAbsolutePath() + "\n      To: " + pair.getValue().toAbsolutePath() + "\n   Error: (" + throwable.getClass().getSimpleName() + ") " + throwable.getMessage()));
                writer.println();
            }
            if (!moveErrors.isEmpty()) {
                writer.format("\n\nMove errors:\n");
                moveErrors.forEach((pair, throwable) -> writer.println(" *  From: " + pair.getKey().toAbsolutePath() + "\n      To: " + pair.getValue().toAbsolutePath() + "\n   Error: (" + throwable.getClass().getSimpleName() + ") " + throwable.getMessage()));
                writer.println();
            }
            if (!deleteErrors.isEmpty()) {
                writer.format("\n\nDelete errors:\n");
                deleteErrors.forEach((path, throwable) -> writer.println(" * " + path.toAbsolutePath() + ": (" + throwable.getClass().getSimpleName() + ") " + throwable.getMessage()));
                writer.println();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return builder.build();
    }
}
