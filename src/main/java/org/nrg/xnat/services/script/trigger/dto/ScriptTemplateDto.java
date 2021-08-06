package org.nrg.xnat.services.script.trigger.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ScriptTemplateDto implements Serializable{
	private static final long serialVersionUID = -5957094360089835698L;
	private String name;
	private String decription;
}
