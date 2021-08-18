package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.WrkWorkflowdata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class WrkWorkflowdataDeserializer<T extends WrkWorkflowdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 7524649093239070378L;

    @SuppressWarnings({"unchecked", "unused"})
    public WrkWorkflowdataDeserializer() {
        this((Class<T>) WrkWorkflowdata.class);
    }

    protected WrkWorkflowdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "category":
                instance.setCategory(parser.getText());
                break;
            case "comments":
                instance.setComments(parser.getText());
                break;
            case "createUser":
                instance.setCreateUser(parser.getText());
                break;
            case "currentStepId":
                instance.setCurrentStepId(parser.getText());
                break;
            case "currentStepLaunchTime":
                // TODO: Handle the "currentStepLaunchTime" property here: Object
                break;
            case "dataType":
                instance.setDataType(parser.getText());
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "details":
                instance.setDetails(parser.getText());
                break;
            case "executionenvironment":
                // TODO: Handle the "executionenvironment" property here: org.nrg.xdat.om.WrkAbstractexecutionenvironment
                break;
            case "externalid":
                instance.setExternalid(parser.getText());
                break;
            case "id":
                instance.setId(parser.getText());
                break;
            case "jobid":
                instance.setJobid(parser.getText());
                break;
            case "justification":
                instance.setJustification(parser.getText());
                break;
            case "launchTime":
                // TODO: Handle the "launchTime" property here: Object
                break;
            case "nextStepId":
                instance.setNextStepId(parser.getText());
                break;
            case "percentagecomplete":
                instance.setPercentagecomplete(parser.getText());
                break;
            case "pipelineName":
                instance.setPipelineName(parser.getText());
                break;
            case "scanId":
                instance.setScanId(parser.getText());
                break;
            case "src":
                instance.setSrc(parser.getText());
                break;
            case "status":
                instance.setStatus(parser.getText());
                break;
            case "stepDescription":
                instance.setStepDescription(parser.getText());
                break;
            case "type":
                instance.setType(parser.getText());
                break;
            case "wrkWorkflowdataId":
                instance.setWrkWorkflowdataId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

