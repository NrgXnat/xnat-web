package org.nrg.xnat.services.workflow.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


import org.apache.commons.lang3.StringUtils;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.WrkWorkflowdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.workflow.WorkflowService;
import org.nrg.xnat.services.workflow.dto.WrkWorkflowdataDto;
import org.nrg.xnat.utils.WorkflowUtils;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WorkflowServiceImpl implements WorkflowService{
	
	@Override
	public List<WrkWorkflowdataDto> findAllWrkWorkflowdata(UserI user) throws NotFoundException {
		return getWorkFlowData(WrkWorkflowdata.getAllWrkWorkflowdatas(user, false));
	}

	private List<WrkWorkflowdataDto> getWorkFlowData(ArrayList<WrkWorkflowdata> allWrkWorkflowdatas) throws NotFoundException {
		log.info("filter out of workflow data ");
		if(Objects.isNull(allWrkWorkflowdatas) && allWrkWorkflowdatas.size()==0) {
			throw new NotFoundException("Wrk Workflow data wasn't found");
		}
		List<WrkWorkflowdataDto> workflowdataDtos = new ArrayList<>();
		allWrkWorkflowdatas.stream().forEach(wkdata->{
			 workflowdataDtos.add(WrkWorkflowdataDto.builder().pipelineName(wkdata.getPipelineName())
						.lunchTime((Date)wkdata.getLaunchTime())
						.id(wkdata.getId())
						.scanId(wkdata.getScanId()).build());
		});
		return workflowdataDtos;
		
	}

	@Override
	public WrkWorkflowdata findWrkWorkflowdata(UserI user, String workflowId) throws DataFormatException, NotFoundException {
		if(StringUtils.isBlank(workflowId)) {
			throw new DataFormatException("workflow id wasn't found {} "+  workflowId);
		}
		WrkWorkflowdata workflowdata = (WrkWorkflowdata) WorkflowUtils.getUniqueWorkflow(user, workflowId);
		if(Objects.isNull(workflowdata)) {
			throw new NotFoundException("Wrk Workflow data wasn't found");
		}
		return workflowdata;
	}

}
