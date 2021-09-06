package org.nrg.xapi.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeatureDefinitionDto implements Serializable {
	private static final long serialVersionUID = -1357797427762108637L;
	private String key;
	private String name;
	private String description;
	private boolean enabled;
	private boolean banned;
}
