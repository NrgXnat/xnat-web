/*
 * web: org.nrg.xnat.helpers.resource.XnatResourceInfo
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.helpers.resource;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.nrg.xnat.services.archive.CatalogService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Provides a package for passing resources to methods on the {@link CatalogService} that insert resources into a
 * catalog. This descriptor includes a file or resource stream and optional path, content, and format specifiers.
 */
@Data
@Builder
public class XnatResourceInfo implements FileWriterWrapperI, Serializable {
    public static XnatResourceInfo.XnatResourceInfoBuilder copy(final XnatResourceInfo info) {
        return XnatResourceInfo.builder()
                               .name(info.getName())
                               .description(info.getDescription())
                               .username(info.getUsername())
                               .eventId(info.getEventId())
                               .created(info.getCreated())
                               .lastModified(info.getLastModified())
                               .tags(info.getTags())
                               .rename(info.getRename())
                               .file(info.getFile())
                               .format(info.getFormat())
                               .content(info.getContent())
                               .source(info.getSource())
                               .fileSize(info.getFileSize())
                               .nestedPath(info.getNestedPath())
                               .extract(info.getExtract())
                               .type(info.getType())
                               .metadata(info.getMetadata());
    }

    @Override
    public void write(final File outputFile) throws Exception {
        if (source == null) {
            throw new FileNotFoundException("File is empty");
        }
        try (final FileOutputStream output = new FileOutputStream(outputFile)) {
            if (fileSize > 2000000) {
                IOUtils.copyLarge(getInputStream(), output);
            } else {
                IOUtils.copy(getInputStream(), output);
            }
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return getSource().getInputStream();
    }

    @Override
    public void delete() {
        if (file != null) {
            file.delete();
        }
    }

    public static class XnatResourceInfoBuilder {
        public XnatResourceInfo.XnatResourceInfoBuilder file(final File file) {
            this.file = file;
            if (StringUtils.isBlank(name)) {
                name(file.getName());
            }
            source(new FileSystemResource(file));
            fileSize(file.length());
            return this;
        }

        public XnatResourceInfo.XnatResourceInfoBuilder multipartFile(final MultipartFile multipartFile) throws IOException {
            final File destination = Paths.get(System.getProperty("java.io.tmpdir"), multipartFile.getOriginalFilename()).toFile();
            multipartFile.transferTo(destination);
            file(destination);
            return this;
        }
    }

    private final String              name;
    private final String              rename;
    private final String              nestedPath;
    private final String              description;
    private final String              format;
    private final String              content;
    private final Number              eventId;
    private final Date                lastModified;
    private final Date                created;
    private final String              username;
    private final Boolean             extract;
    private final InputStreamSource   source;
    private final File                file;
    private final long                fileSize;
    private final UPLOAD_TYPE         type;
    @Singular
    private final List<String>        tags;
    @Singular("data")
    private final Map<String, String> metadata;
}
