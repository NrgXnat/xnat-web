package org.nrg.xnat.dto.prearchive;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrearcSessionScanResFileDto implements Serializable {
	private static final long serialVersionUID = -8701505763901154816L;
	private String name;
	private String uri;
	private Long size;
}
