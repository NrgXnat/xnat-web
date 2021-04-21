package org.nrg.xnat.dto.search;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchElementDto implements Serializable {
	private static final long serialVersionUID = -3775531857512535678L;
	private String singular;
	private String plural;
	private Boolean secured;
	private String elementName;
	private Long count;

}
