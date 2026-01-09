package org.nrg.xnat.ingest.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class IngestUtils {

    public static Optional<Path> findFileInFolder(Path deviceRootPath, String fileName) {
        Path startPath = deviceRootPath;
        try (Stream<Path> walk = Files.walk(startPath)) {
            return walk
                    .filter(Files::isRegularFile) // Filter for regular files
                    .filter(path -> path.getFileName().toString().equals(fileName)) // Filter by file name
                    .findFirst(); // Return the first match
        } catch (IOException e) {
            log.error("Error while traversing directory: " + e.getMessage());
            return Optional.empty();
        }
    }
}
