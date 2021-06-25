package org.nrg.xnat.extensions.util;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudyRoutingUtil implements Serializable {
	private static final long serialVersionUID = -2489157896180259236L;
	private String studyInstanceUid;
	private String project;
	private String subject;
	private String label;
	private String user;
	private String created;
	private String accessed;
}
