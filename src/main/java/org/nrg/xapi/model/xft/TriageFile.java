package org.nrg.xapi.model.xft;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class TriageFile implements Serializable {
    private static final long serialVersionUID = 2341940606079560992L;

    private String name;
    private String uri;
    private String target;
    private String user;
    private String date;
    private String overwrite;
    private String eventReason;
    private String fileTarget;
    private String format;
    private String content;
    private long   size;
}
