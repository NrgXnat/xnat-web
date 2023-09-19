package org.nrg.xnat.tracking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;

import java.util.Date;

@EventLogSubType("BulkLaunchLog")
@JsonInclude
@Data
@EqualsAndHashCode(callSuper = true)
public class BulkLaunchLog extends EventLog {
    private Integer total;
    private Integer steps;
    private boolean initialEvent = false;
    private boolean success = false;
    private boolean completed = false;
    private WorkflowLog workflowLog;

    public BulkLaunchLog() {
        setType("BulkLaunchLog");
    }

    @JsonIgnore
    public void setWorkflow(PersistentWorkflowI workflow) {
        workflowLog = new WorkflowLog(workflow);
        final String status = workflow.getStatus();
        if (status.startsWith(PersistentWorkflowUtils.FAILED)) {
            success = false;
            completed = true;
        } else if (status.equals(PersistentWorkflowUtils.COMPLETE)) {
            success = true;
            completed = true;
        }
    }

    @JsonInclude
    @Data
    @NoArgsConstructor
    public static class WorkflowLog {
        private Integer id;
        private String itemId;
        private String status;
        private String details;
        private String containerId;
        private String pipelineName;
        private String justification;
        private String src;
        private String externalId;
        private String type;
        private String category;
        private String itemType;
        private String currentStepId;
        private String stepDescription;
        private Date launchTime;
        private Date currentStepLaunchTime;
        private String percentageComplete;
        private String jobId;
        private String scanId;

        public WorkflowLog(PersistentWorkflowI workflow) {
            this.id = workflow.getWorkflowId();
            this.itemId = workflow.getId();
            this.status = workflow.getStatus();
            this.details = workflow.getDetails();
            this.containerId = workflow.getComments();
            this.pipelineName = workflow.getPipelineName();
            this.justification = workflow.getJustification();
            this.src = workflow.getSrc();
            this.externalId = workflow.getExternalid();
            this.type = workflow.getType();
            this.category = workflow.getCategory();
            this.itemType = workflow.getDataType();
            this.currentStepId = workflow.getCurrentStepId();
            this.stepDescription = workflow.getStepDescription();
            this.launchTime = workflow.getLaunchTimeDate();
            this.currentStepLaunchTime = workflow.getCurrentStepLaunchTimeDate();
            this.percentageComplete = workflow.getPercentagecomplete();
            this.jobId = workflow.getJobid();
            this.scanId = workflow.getScanId();
        }
    }
}
