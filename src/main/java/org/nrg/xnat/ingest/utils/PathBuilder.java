package org.nrg.xnat.ingest.utils;

import java.io.File;

public class PathBuilder {
    private StringBuilder path;
    private String separator;

    public PathBuilder() {
        this.path = new StringBuilder();
        this.separator = File.separator;
    }

    public PathBuilder(String basePath) {
        this.path = new StringBuilder(basePath);
        this.separator = File.separator;
    }

    public PathBuilder withSeparator(String separator) {
        this.separator = separator;
        return this;
    }

    public PathBuilder append(String part) {
        if (part != null && !part.isEmpty()) {
            if (path.length() > 0 && !path.toString().endsWith(separator)) {
                path.append(separator);
            }
            path.append(part);
        }
        return this;
    }

    public PathBuilder appendAll(String... parts) {
        for (String part : parts) {
            append(part);
        }
        return this;
    }

    public String build() {
        return path.toString();
    }
}