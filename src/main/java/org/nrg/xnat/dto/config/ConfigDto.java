package org.nrg.xnat.dto.config;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@SuppressWarnings("serial")
@Data
@Builder
public class ConfigDto implements Serializable {
	
	private String mode;
	private String list;
	private boolean enabled;
	@NotNull
	private String contents;

	public ConfigDto() {
	}

	public ConfigDto(String mode, String list, boolean enabled, @NotNull String contents) {
		super();
		this.mode = mode;
		this.list = list;
		this.enabled = enabled;
		this.contents = contents;
	}
	
}
