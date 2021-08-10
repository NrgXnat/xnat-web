package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.WrkWorkflowdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class WrkWorkflowdataDeserializer<T extends WrkWorkflowdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -90560120335949862L;

    @SuppressWarnings("unchecked")
    public WrkWorkflowdataDeserializer() {
        this((Class<T>) WrkWorkflowdata.class);
    }

    public WrkWorkflowdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "category":
                // TODO: Handle the "category" property here: String
                break;
            case "comments":
                // TODO: Handle the "comments" property here: String
                break;
            case "createUser":
                // TODO: Handle the "createUser" property here: String
                break;
            case "currentStepId":
                // TODO: Handle the "currentStepId" property here: String
                break;
            case "currentStepLaunchTime":
                // TODO: Handle the "currentStepLaunchTime" property here: Object
                break;
            case "currentStepLaunchTimeDate":
                // TODO: Handle the "currentStepLaunchTimeDate" property here: java.util.Date
                break;
            case "dataType":
                // TODO: Handle the "dataType" property here: String
                break;
            case "description":
                // TODO: Handle the "description" property here: String
                break;
            case "details":
                // TODO: Handle the "details" property here: String
                break;
            case "eventId":
                // TODO: Handle the "eventId" property here: Number
                break;
            case "executionenvironment":
                // TODO: Handle the "executionenvironment" property here: org.nrg.xdat.model.WrkAbstractexecutionenvironmentI
                break;
            case "externalid":
                // TODO: Handle the "externalid" property here: String
                break;
            case "jobid":
                // TODO: Handle the "jobid" property here: String
                break;
            case "justification":
                // TODO: Handle the "justification" property here: String
                break;
            case "launchTime":
                // TODO: Handle the "launchTime" property here: Object
                break;
            case "launchTimeDate":
                // TODO: Handle the "launchTimeDate" property here: java.util.Date
                break;
            case "nextStepId":
                // TODO: Handle the "nextStepId" property here: String
                break;
            case "onlyPipelineName":
                // TODO: Handle the "onlyPipelineName" property here: String
                break;
            case "percentagecomplete":
                // TODO: Handle the "percentagecomplete" property here: String
                break;
            case "pipelineName":
                // TODO: Handle the "pipelineName" property here: String
                break;
            case "scanId":
                // TODO: Handle the "scanId" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "status":
                // TODO: Handle the "status" property here: String
                break;
            case "stepDescription":
                // TODO: Handle the "stepDescription" property here: String
                break;
            case "type":
                // TODO: Handle the "type" property here: String
                break;
            case "userId":
                // TODO: Handle the "userId" property here: Integer
                break;
            case "username":
                // TODO: Handle the "username" property here: String
                break;
            case "workflowId":
                // TODO: Handle the "workflowId" property here: Integer
                break;
            case "wrkWorkflowdataId":
                // TODO: Handle the "wrkWorkflowdataId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

