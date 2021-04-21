package org.nrg.xnat.dto.search;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class VersionDto {

	private String name;
	private String orderBy;
	private String lightColor;
	private String darkColor;
	private String defaultSortOrder;
	private List<DisplayFieldReferenceIDto> fields;
}
