package org.nrg.xnat.services.workflow.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WrkWorkflowdataDto implements Serializable {
	
	private static final long serialVersionUID = -8798562374691057681L;
	private String pipelineName;
	private Date lunchTime;
	private String id;
	private String scanId;

}
