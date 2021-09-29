package org.nrg.xapi.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchElement implements Serializable {
	private static final long serialVersionUID = -3775531857512535678L;
	private String singular;
	private String plural;
	private Boolean secured;
	private String elementName;
	private Long count;

}
