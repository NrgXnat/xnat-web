package org.nrg.xnat.dto.file;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResourceFileDto {
	private String fileContent;
	private String name;
	private Integer size;
	private String fileTags;
	private Integer catId;
	private String digest;
	private String uri;
	private String fileFormat;
	private String collection;
}
