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
public class DisplayVersionDto implements Serializable {
	private static final long serialVersionUID = -8652407066308164382L;
	private String elementName;
	private List<VersionDto> versions;
}
