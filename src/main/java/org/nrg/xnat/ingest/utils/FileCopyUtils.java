package org.nrg.xnat.ingest.utils;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.ingest.model.pojo.FileItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nrg.xdat.XDAT;
import org.nrg.xnat.ingest.model.pojo.XnatUriComponents;

@Slf4j
public class FileCopyUtils {

    //possible data input regex patterns
    List<String> regexPatternsForInputs = Arrays.asList(
            ".*/projects/([^/]+)/resources/([^/]+).*",
            ".*/projects/([^/]+)/subjects/([^/]+)/resources/([^/]+).*",
            ".*/projects/([^/]+)/subjects/([^/]+)/experiments/([^/]+)/resources/([^/]+).*",
            ".*/projects/([^/]+)/subjects/([^/]+)/experiments/([^/]+)/scans/([^/]+)/resources/([^/]+).*");


    public void processJsonFile(final FileItem[] items, UserI user) throws IOException {
        if (items ==null || items.length == 0) {
            return;
        }

        for (FileItem item : items) {
            processItem(item, user);
        }
    }

    private  void processItem(FileItem item, UserI user) throws IOException {
        String type = item.getType();
        String absolutePath = item.getEffectiveAbsolutePath();
        String destPath = item.getDestPath();

        if (absolutePath == null || absolutePath.isEmpty() ||
                destPath == null || destPath.isEmpty()) {
            log.debug("Skipping item: " + item.getName() + " - missing path information");
            return;
        }

        String xnatArchiveDestinationPath = translatePathToArchivePath(destPath, user);

        Path source = Paths.get(absolutePath);
        Path destination = Paths.get(xnatArchiveDestinationPath);

        if ("folder".equalsIgnoreCase(type)) {
            copyFolder(source, destination);
            log.debug("Copied folder: " + source + " -> " + destination);
        } else if ("file".equalsIgnoreCase(type)) {
            copyFile(source, destination);
            log.debug("Copied file: " + source + " -> " + destination);
        }

        // Process children recursively if they exist
        if (item.getChildren() != null && !item.getChildren().isEmpty()) {
            for (FileItem child : item.getChildren()) {
                processItem(child, user);
            }
        }
    }

    private  void copyFile(Path source, Path destination) throws IOException {
        if (!Files.exists(source)) {
            log.debug("Source file does not exist: " + source);
            return;
        }

        // Create parent directories if they don't exist
        Files.createDirectories(destination.getParent());

        // Copy file with replace existing option
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    }

    private  void copyFolder(Path source, Path destination) throws IOException {
        if (!Files.exists(source)) {
            log.error("Source folder does not exist: " + source);
            return;
        }

        // Create destination directory
        Files.createDirectories(destination);

        // Copy all files and subdirectories recursively
        Files.walk(source)
                .forEach(src -> {
                    try {
                        Path dest = destination.resolve(source.relativize(src));
                        if (Files.isDirectory(src)) {
                            Files.createDirectories(dest);
                        } else {
                            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (IOException e) {
                        log.error("Error copying: " + src + " - " + e.getMessage());
                    }
                });
    }

    private String translatePathToArchivePath(final String destinationPath, UserI user) {
        XnatUriComponents uriComponents = parseUriWithRegex(destinationPath);

        final XnatProjectdata projectData = XnatProjectdata.getProjectByIDorAlias(uriComponents.getProjectId(), user,
                                                                                  false);

        return new PathBuilder(XDAT.getSiteConfigPreferences().getArchivePath())
                .append(uriComponents.getProjectId())
                .append(projectData.getCurrentArc())
                .append(uriComponents.getExperimentId())
                .append("SCANS")
                .append(uriComponents.getScanId())
                .append(uriComponents.getResourceId())
                .build();
    }

    private XnatUriComponents parseUriWithRegex(String uri) {
        if (uri == null || uri.isEmpty()) {
            return null;
        }

        String pattern = ".*/projects/([^/]+)/subjects/([^/]+)/experiments/([^/]+)/scans/([^/]+)/resources/([^/]+).*";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(uri);

        if (m.matches()) {
            return new XnatUriComponents(
                    m.group(1),  // projectId
                    m.group(2),  // subjectId
                    m.group(3),  // experimentId
                    m.group(4),  // scanId
                    m.group(5)   // resourceId
            );
        }

        return null; // URI doesn't match expected pattern
    }

}
