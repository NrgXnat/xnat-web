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
        try (Stream<Path> walk = Files.walk(deviceRootPath)) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equals(fileName))
                    .findFirst();
        } catch (IOException e) {
            log.error("Error while traversing directory: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
