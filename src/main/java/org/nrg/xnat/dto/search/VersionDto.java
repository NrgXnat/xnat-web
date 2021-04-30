package org.nrg.xnat.dto.search;

import java.io.Serializable;
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
public class VersionDto implements Serializable {
	private static final long serialVersionUID = -2361324617208025730L;
	private String name;
	private String orderBy;
	private String lightColor;
	private String darkColor;
	private String defaultSortOrder;
	private List<DisplayFieldReferenceIDto> fields;
}
