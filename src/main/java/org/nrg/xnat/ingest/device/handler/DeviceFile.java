package org.nrg.xnat.ingest.device.handler;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Getter
public class DeviceFile {
    // Getters and setters
    private Path filePath;
    private String fileName;
    private long fileSize;
    private String fileType;
    private Map<String, Object> metadata;

    public DeviceFile(Path filePath) throws IOException {
        this.filePath = filePath;
        this.fileName = filePath.getFileName().toString();
        this.fileSize = Files.size(filePath);
        this.fileType = getFileExtension(fileName);
        this.metadata = new HashMap<>();
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1).toLowerCase() : "";
    }
    
    public void setMetadata(String key, Object value) { metadata.put(key, value); }
    public Object getMetadata(String key) { return metadata.get(key); }
}
