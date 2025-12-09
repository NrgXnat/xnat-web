package org.nrg.xnat.ingest.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// POJO Classes
public class FileItem {
    private String name;
    private String type;
    private String sourcePath;
    private String destPath;

    @JsonProperty("absolutepath")
    private String absolutepath;

    @JsonProperty("absolutePath")
    private String absolutePath;

    private List<FileItem> children;
    private String status;
    private Long size;
    private String lastModified;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getDestPath() {
        return destPath;
    }

    public void setDestPath(String destPath) {
        this.destPath = destPath;
    }

    public String getAbsolutepath() {
        return absolutepath;
    }

    public void setAbsolutepath(String absolutepath) {
        this.absolutepath = absolutepath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public List<FileItem> getChildren() {
        return children;
    }

    public void setChildren(List<FileItem> children) {
        this.children = children;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    // Helper method to get the absolute path (handles both field names)
    public String getEffectiveAbsolutePath() {
        if (absolutepath != null && !absolutepath.isEmpty()) {
            return absolutepath;
        }
        return absolutePath;
    }
}