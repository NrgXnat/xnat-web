package org.nrg.xnat.dto.prearchive;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrearcSessionScanDto implements Serializable {
	private static final long serialVersionUID = -8701505763901154816L;
	private String xsiType;
	private String series_description;
	private String ID;

}
