package org.nrg.xnat.dto.search;

import java.io.Serializable;

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
public class XnatSearchElementDto implements Serializable {

	private static final long serialVersionUID = -7765541182699651606L;
	private String summary;
	private String fieldId;
	private String header;
	private Boolean requiresValue;
	private String elementName;
	private String type;
	private String description;
	private Integer src;
	private DisplayVersionDto displayVersion;

}
