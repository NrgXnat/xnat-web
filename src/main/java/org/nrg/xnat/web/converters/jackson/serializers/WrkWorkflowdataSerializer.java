package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.WrkWorkflowdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class WrkWorkflowdataSerializer<T extends WrkWorkflowdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -3878837757663055370L;

    @SuppressWarnings("unchecked")
    public WrkWorkflowdataSerializer() {
        this((Class<T>) WrkWorkflowdata.class);
    }

    protected WrkWorkflowdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "category" property here: String
        // TODO: Write out the "comments" property here: String
        // TODO: Write out the "createUser" property here: String
        // TODO: Write out the "currentStepId" property here: String
        // TODO: Write out the "currentStepLaunchTime" property here: Object
        // TODO: Write out the "currentStepLaunchTimeDate" property here: java.util.Date
        // TODO: Write out the "dataType" property here: String
        // TODO: Write out the "description" property here: String
        // TODO: Write out the "details" property here: String
        // TODO: Write out the "eventId" property here: Number
        // TODO: Write out the "executionenvironment" property here: org.nrg.xdat.model.WrkAbstractexecutionenvironmentI
        // TODO: Write out the "externalid" property here: String
        // TODO: Write out the "jobid" property here: String
        // TODO: Write out the "justification" property here: String
        // TODO: Write out the "launchTime" property here: Object
        // TODO: Write out the "launchTimeDate" property here: java.util.Date
        // TODO: Write out the "nextStepId" property here: String
        // TODO: Write out the "onlyPipelineName" property here: String
        // TODO: Write out the "percentagecomplete" property here: String
        // TODO: Write out the "pipelineName" property here: String
        // TODO: Write out the "scanId" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "status" property here: String
        // TODO: Write out the "stepDescription" property here: String
        // TODO: Write out the "type" property here: String
        // TODO: Write out the "userId" property here: Integer
        // TODO: Write out the "username" property here: String
        // TODO: Write out the "workflowId" property here: Integer
        // TODO: Write out the "wrkWorkflowdataId" property here: Integer
    }
}

