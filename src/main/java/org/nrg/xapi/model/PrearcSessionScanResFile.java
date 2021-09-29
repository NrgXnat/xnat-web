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
public class PrearcSessionScanResFile implements Serializable {
	private static final long serialVersionUID = -8701505763901154816L;
	private String name;
	private String uri;
	private Long size;
}
