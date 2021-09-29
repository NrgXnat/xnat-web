package org.nrg.xapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceFile {
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
