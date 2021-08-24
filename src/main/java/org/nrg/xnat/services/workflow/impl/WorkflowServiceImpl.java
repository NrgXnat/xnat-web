package org.nrg.xnat.services.workflow.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


import org.apache.commons.lang3.StringUtils;
import org.nrg.pipeline.xmlbeans.workflow.WorkflowData;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.WrkWorkflowdata;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.FieldNotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.services.workflow.WorkflowService;
import org.nrg.xnat.services.workflow.dto.WrkWorkflowdataDto;
import org.nrg.xnat.utils.WorkflowUtils;
import org.restlet.data.Status;
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

	@Override
	public void updateWorkflow(UserI user, String workflowId, WrkWorkflowdata workflowData) throws NotFoundException, InsufficientPrivilegesException, DataFormatException, ElementNotFoundException, FieldNotFoundException {
		 final WrkWorkflowdata workflow;
		if (StringUtils.isNotBlank(workflowId)) {
			 // Lookup the workflow by the ID provided by the user.
            workflow = (WrkWorkflowdata) WorkflowUtils.getUniqueWorkflow(user, workflowId);
            if (workflow == null) {
                // If we couldn't find the workflow, 404
            	throw new NotFoundException("Unable to find the specified workflow.");
            }
		}else {
			workflow = workflowData;
		}
		 // If the workflow exists, Make sure the user has permission to edit an existing workflow.
        if (workflow != null && !canUserEditWorkflow(user, workflow)) {
            // If the user is not allow to modify this workflow, 403
        	throw new InsufficientPrivilegesException("You are not allowed to make changes to this workflow.");
        }
        // Id, launch_time, data_type, and pipeline_name are all required in order to save a new workflow
        if (workflow == null && StringUtils.isAnyBlank(workflow.getStringProperty("id"), workflow.getStringProperty("launch_time"), workflow.getStringProperty("pipeline_name"), workflow.getStringProperty("data_type"))) {
           throw new DataFormatException("Id, launch_time, data_type and pipeline_name are all required (scan_id may be specified when appropriate but is not required).");
        }

        // Save the workflow
        try {
        SaveItemHelper.authorizedSave(workflow, user, false, false, EventUtils.DEFAULT_EVENT(user, "Workflow Update"));
        }catch (Exception e) {
        	log.error("You are not allowed to make changes to this workflow.");
		}
	}

	 private boolean canUserEditWorkflow(final UserI user, final WrkWorkflowdata workflow) {
	        return workflow.getInsertUser().getID().equals(user.getID()) || Roles.isSiteAdmin(user);
	    }
}
