package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.WrkWorkflowdata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class WrkWorkflowdataSerializer<T extends WrkWorkflowdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -3878837757663055370L;

    @SuppressWarnings({"unchecked", "unused"})
    public WrkWorkflowdataSerializer() {
        this((Class<T>) WrkWorkflowdata.class);
    }

    protected WrkWorkflowdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "category", instance.getCategory());
        writeNonBlankField(generator, "comments", instance.getComments());
        writeNonBlankField(generator, "createUser", instance.getCreateUser());
        writeNonBlankField(generator, "currentStepId", instance.getCurrentStepId());
        // TODO: Write out the "currentStepLaunchTime" property here: Object
        writeNonBlankField(generator, "dataType", instance.getDataType());
        writeNonBlankField(generator, "description", instance.getDescription());
        writeNonBlankField(generator, "details", instance.getDetails());
        // TODO: Write out the "executionenvironment" property here: org.nrg.xdat.om.WrkAbstractexecutionenvironment
        writeNonBlankField(generator, "externalid", instance.getExternalid());
        writeNonBlankField(generator, "id", instance.getId());
        writeNonBlankField(generator, "jobid", instance.getJobid());
        writeNonBlankField(generator, "justification", instance.getJustification());
        // TODO: Write out the "launchTime" property here: Object
        writeNonBlankField(generator, "nextStepId", instance.getNextStepId());
        writeNonBlankField(generator, "percentagecomplete", instance.getPercentagecomplete());
        writeNonBlankField(generator, "pipelineName", instance.getPipelineName());
        writeNonBlankField(generator, "scanId", instance.getScanId());
        writeNonBlankField(generator, "src", instance.getSrc());
        writeNonBlankField(generator, "status", instance.getStatus());
        writeNonBlankField(generator, "stepDescription", instance.getStepDescription());
        writeNonBlankField(generator, "type", instance.getType());
        writeNonNullNumber(generator, "wrkWorkflowdataId", instance.getWrkWorkflowdataId());
    }
}

