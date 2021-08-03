package org.nrg.xnat.services.script.trigger.utils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScriptTriggerUtils implements Serializable{
	private static final long serialVersionUID = -3695911063166369038L;
	private String id ;
	private String  triggerId;
	private String scope ;
	private String entityId;
	private String srcEventClass;
	private String  event;
	private Map<String, List<String>> eventFilters;
	private String scriptId;
	private String description;
}
