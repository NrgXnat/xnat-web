package org.nrg.xapi.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TriageFileDto {
	private String name;
	private String uri;
	private String target;
	private String user;
	private String date;
	private String overwrite;
	private String eventReason;
	private String ftarget;
	private String format;
	private String content;
	private long size;
	
}
