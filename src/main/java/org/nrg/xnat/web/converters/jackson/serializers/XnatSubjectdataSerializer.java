package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.model.XnatSubjectassessordataI;
import org.nrg.xdat.om.XnatSubjectdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatSubjectdataSerializer<T extends XnatSubjectdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 5781170250219703398L;

    @SuppressWarnings("unchecked")
    public XnatSubjectdataSerializer() {
        this((Class<T>) XnatSubjectdata.class);
    }

    protected XnatSubjectdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "id", instance.getId());
        writeNonBlankField(generator, "label", instance.getLabel());
        writeNonBlankField(generator, "project", instance.getProject());
        writeNonBlankField(generator, "group", instance.getGroup());
        writeNonBlankField(generator, "src", instance.getSrc());
        writeNonBlankField(generator, "initials", instance.getInitials());
        writeNonNullField(generator, "demographics", instance.getDemographics());
        writeNonNullField(generator, "sharing", instance.getSharing_share());
        writeNonNullField(generator, "resources", instance.getResources_resource());
        writeNonNullField(generator, "investigator", instance.getInvestigator());
        writeNonNullField(generator, "metadata", instance.getMetadata());
        writeNonNullField(generator, "addId", instance.getAddid());
        writeNonNullField(generator, "fields", instance.getFields_field());
        generator.writeArrayFieldStart("experiments");
        for (final XnatSubjectassessordataI experiment : instance.getExperiments_experiment()) {
            generator.writeString(experiment.getId());
        }
        generator.writeEndArray();
    }
}
