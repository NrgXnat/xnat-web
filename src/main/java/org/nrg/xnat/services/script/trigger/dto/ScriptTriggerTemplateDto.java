package org.nrg.xnat.services.script.trigger.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScriptTriggerTemplateDto implements Serializable {
	private static final long serialVersionUID = 4423815186737550548L;
	private String name;
	private String decription;
	private String scriptId;
	private String dataType;
	private String event;
}
