package org.nrg.xapi.model.xft;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class TriageEntry implements Serializable {
    private static final long serialVersionUID = 4942375999863374160L;

    private String resource;
    private String uri;
    private String target;
    private String user;
    private String date;
    private String overwrite;
    private String eventReason;
    private String fileTarget;
    private String format;
    private String content;
    private String fSource;
}
