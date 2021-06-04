package org.nrg.xnat.dto.prearchive;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrearcSessionResourceDto implements Serializable {
	
	private static final long serialVersionUID = 54715293586709399L;
	
	private String category;
	private String cat_id;
	private String label;
	private Long file_count;
	private Long file_size;

}
