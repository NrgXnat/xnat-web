package org.nrg.xapi.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TriageDto implements Serializable {
	private static final long serialVersionUID = 4942375999863374160L;
	private String resource;
	private String uri;
	private String target;
	private String user;
	private String date;
	private String overwrite;
	private String eventReason;
	private String ftarget;
	private String format;
	private String content;
	private String fSource;
}
