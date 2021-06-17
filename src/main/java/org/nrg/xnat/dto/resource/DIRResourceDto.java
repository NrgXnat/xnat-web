
package org.nrg.xnat.dto.resource;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DIRResourceDto implements Serializable {

	private static final long serialVersionUID = -8756439953379130216L;

	private Long size;
	private String name;
	private boolean DIR;
	private String URI;

}