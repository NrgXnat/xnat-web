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
public class DisplayVersionDto {

	private String elementName;
	private List<VersionDto> versions;
}
