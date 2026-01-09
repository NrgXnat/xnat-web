package org.nrg.xnat.ingest.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.ingest.model.pojo.FileItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.nrg.xdat.XDAT;
import org.nrg.xnat.ingest.model.pojo.XnatUriComponents;
import org.nrg.xnat.restlet.actions.SessionImporter;
import org.nrg.xnat.services.archive.impl.legacy.DefaultCatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FileCopyService {

    private final DefaultCatalogService defaultCatalogService;

    //possible data input regex patterns
    List<String> regexPatternsForInputs = Arrays.asList(
            ".*/projects/([^/]+)/resources/([^/]+).*",
            ".*/projects/([^/]+)/subjects/([^/]+)/resources/([^/]+).*",
            ".*/projects/([^/]+)/subjects/([^/]+)/experiments/([^/]+)/resources/([^/]+).*",
            ".*/projects/([^/]+)/subjects/([^/]+)/experiments/([^/]+)/scans/([^/]+)/resources/([^/]+).*");


    @Autowired
    public FileCopyService(DefaultCatalogService defaultCatalogService) {
        this.defaultCatalogService = defaultCatalogService;
    }


    public void processJsonFile(final FileItem[] items, UserI user) throws IOException, ServerException, ClientException {
        if (items == null) {
            return;
        }

        for (FileItem item : items) {
            processItem(item, user);
        }
    }

    private void processItem(FileItem item, UserI user) throws IOException, ServerException, ClientException {
        String type = item.getType();
        String absolutePath = item.getAbsolutePath();
        String destPath = item.getDestPath();

        if (absolutePath == null || absolutePath.isEmpty() || destPath == null || destPath.isEmpty()) {
            log.debug("Skipping item: {} - missing path information", item.getName());
            return;
        }

        Path source = Paths.get(absolutePath);
        Map<Path, Path> pathsForCopy = translatePathToArchivePath(destPath, user);
        Map.Entry<Path, Path> entry = pathsForCopy.entrySet().iterator().next();
        Path desinationPath = entry.getKey();
        Path resourcePath = entry.getValue();
        String resourcePathString = "/" + resourcePath.toString();

        if ("folder".equalsIgnoreCase(type)) {
            copyFolder(source, desinationPath);
            log.debug("Copied folder: {} -> {}", source, desinationPath);
            defaultCatalogService.refreshResourceCatalogs(user, Collections.singletonList(resourcePathString));
        } else if ("file".equalsIgnoreCase(type)) {
            desinationPath = desinationPath.resolve(source.subpath(source.getNameCount()-1, source.getNameCount()));
            copyFile(source, desinationPath);
            log.debug("Copied file: {} -> {}", source, desinationPath);
            defaultCatalogService.refreshResourceCatalogs(user, Collections.singletonList(resourcePathString));
        }
    }

    private void copyFile(Path source, Path destination) throws IOException {
        if (!Files.exists(source)) {
            log.debug("Source file does not exist: {}", source);
            return;
        }

        Files.createDirectories(destination.getParent());
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
    }

    private void copyFolder(Path source, Path destination) throws IOException {
        if (!Files.exists(source)) {
            log.error("Source folder does not exist: {}", source);
            return;
        }

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

    private Map<Path, Path> translatePathToArchivePath(final String destinationPath, UserI user) {
        XnatUriComponents uriComponents = parseUriWithRegex(destinationPath);

        final XnatProjectdata projectData = XnatProjectdata.getProjectByIDorAlias(uriComponents.getProjectId(), user,
                                                                                  false);

        Path archivePath = Paths.get(XDAT.getSiteConfigPreferences().getArchivePath(), uriComponents.getProjectId());
        Path resourcePath = Paths.get(XDAT.getSiteConfigPreferences().getArchivePath()).getFileName();

        if (StringUtils.isEmpty(uriComponents.getSubjectId())) {
            archivePath = archivePath.resolve("resources").resolve(uriComponents.getResourceId());
            resourcePath = resourcePath.resolve("projects").resolve(uriComponents.getProjectId());
        } else if (StringUtils.isEmpty(uriComponents.getExperimentId())) {
            archivePath = archivePath.resolve("subjects")
                    .resolve(uriComponents.getSubjectId())
                    .resolve(uriComponents.getResourceId());
            String subjectId = Objects.requireNonNull(XnatSubjectdata.GetSubjectByIdOrProjectlabelCaseInsensitive(
                    uriComponents.getProjectId(), uriComponents.getSubjectId(), user, false)).getId();
            resourcePath = resourcePath.resolve("subjects").resolve(subjectId);
        } else if (StringUtils.isEmpty(uriComponents.getScanId())) {
            archivePath = archivePath.resolve(projectData.getCurrentArc())
                    .resolve(uriComponents.getExperimentId())
                    .resolve("RESOURCES")
                    .resolve(uriComponents.getResourceId());
            String experimentId = Objects.requireNonNull(SessionImporter.getExperimentByIdOrLabel(
                    uriComponents.getProjectId(), uriComponents.getExperimentId(), user)).getId();
            resourcePath = resourcePath.resolve("experiments").resolve(experimentId);
        } else {
            archivePath = archivePath.resolve(projectData.getCurrentArc())
                    .resolve(uriComponents.getExperimentId())
                    .resolve("SCANS")
                    .resolve(uriComponents.getScanId())
                    .resolve(uriComponents.getResourceId());
            String experimentId = Objects.requireNonNull(SessionImporter.getExperimentByIdOrLabel(
                    uriComponents.getProjectId(), uriComponents.getExperimentId(), user)).getId();
            resourcePath = resourcePath.resolve("experiments").resolve(experimentId);
        }
        return Collections.singletonMap(archivePath, resourcePath);
    }

    private XnatUriComponents parseUriWithRegex(String uri) {
        if (uri == null || uri.isEmpty()) {
            return null;
        }

        Matcher m = null;
        for (String pattern: regexPatternsForInputs) {
            Pattern p = Pattern.compile(pattern);
            m = p.matcher(uri);
            if (m.matches()) {
                break;
            }
        }

        if (m != null) {
            switch(m.groupCount()) {
                case 5:
                    return new XnatUriComponents(
                            m.group(1),  // projectId
                            m.group(2),  // subjectId
                            m.group(3),  // experimentId
                            m.group(4),  // scanId
                            m.group(5)   // resourceId
                    );
                case 4:
                    return new XnatUriComponents(
                            m.group(1),  // projectId
                            m.group(2),  // subjectId
                            m.group(3),  // experimentId
                            m.group(4)   // resourceId
                    );
                case 3:
                    return new XnatUriComponents(
                            m.group(1),  // projectId
                            m.group(2),  // subjectId
                            m.group(3)   // resourceId
                    );
                case 2:
                    return new XnatUriComponents(
                            m.group(1),  // projectId
                            m.group(2)   // resourceId
                    );
            }
        }
        return null;
    }
}
