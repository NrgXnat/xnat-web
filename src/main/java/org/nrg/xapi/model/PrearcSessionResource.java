package org.nrg.xapi.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrearcSessionResource implements Serializable {
	
	private static final long serialVersionUID = 54715293586709399L;

	private String category;
	private String catId;
	private String label;
	private Long fileCount;
	private Long fileSize;

}
