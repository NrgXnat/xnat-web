package org.nrg.xnat.ingest.device.handler;

import java.util.HashMap;
import java.util.Map;

public class ManifestEntry {
    private String fileName;
    private String relativePath;

    private Map<String, Object> attributes;

    public ManifestEntry(String fileName, String relativePath) {
        this.fileName = fileName;
        this.relativePath = relativePath;
        this.attributes = new HashMap<>();
    }

    // Getters and setters
    public String getFileName() { return fileName; }
    public String getRelativePath() { return relativePath; }
    public Map<String, Object> getAttributes() { return attributes; }

    public void setAttribute(String key, Object value) { attributes.put(key, value); }
    public Object getAttribute(String key) { return attributes.get(key); }
}
