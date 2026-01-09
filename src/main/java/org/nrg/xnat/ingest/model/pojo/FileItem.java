package org.nrg.xnat.ingest.model.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FileItem {
    private String name;
    private String type;
    private String sourcePath;
    private String destPath;
    @JsonProperty("absolutePath")
    private String absolutePath;
    private List<FileItem> children;
    private String status;
    private Long size;
}