package org.nrg.xnat.ingest.device.handler;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ManifestEntry {
    // Getters and setters
    private String fileName;
    private String relativePath;
    private Map<String, Object> attributes;

    public ManifestEntry(String fileName, String relativePath) {
        this.fileName = fileName;
        this.relativePath = relativePath;
        this.attributes = new HashMap<>();
    }

    public void setAttribute(String key, Object value) { attributes.put(key, value); }
    public Object getAttribute(String key) { return attributes.get(key); }
}
