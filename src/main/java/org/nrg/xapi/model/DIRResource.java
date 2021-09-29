package org.nrg.xapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DIRResource {

	private static final long serialVersionUID = -8756439953379130216L;
	private Long size;
	private String name;
	private boolean DIR;
	private String URI;
}
