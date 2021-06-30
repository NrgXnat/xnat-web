package org.nrg.xnat.extensions.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TriageFileUtil {
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
