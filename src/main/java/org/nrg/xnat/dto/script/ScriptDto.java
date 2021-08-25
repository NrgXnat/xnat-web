package org.nrg.xnat.dto.script;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScriptDto implements Serializable {
	private static final long serialVersionUID = -4748388533827418742L;

	private String scriptId;
	private String scriptLabel;
	private String language;
	private String description;

}
