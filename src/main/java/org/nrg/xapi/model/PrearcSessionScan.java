package org.nrg.xapi.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrearcSessionScan implements Serializable {
	private static final long serialVersionUID = -8701505763901154816L;
	private String xsiType;
	private String seriesDescription;
	private String ID;

}
