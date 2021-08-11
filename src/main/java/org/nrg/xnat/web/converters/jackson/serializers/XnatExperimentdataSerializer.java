package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatExperimentdata;

import java.io.IOException;

@Slf4j
public abstract class XnatExperimentdataSerializer<T extends XnatExperimentdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -745536640121672797L;

    @SuppressWarnings("unchecked")
    public XnatExperimentdataSerializer() {
        this((Class<T>) XnatExperimentdata.class);
    }

    protected XnatExperimentdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "id", instance.getId());
        writeNonBlankField(generator, "label", instance.getLabel());
        writeNonBlankField(generator, "protocol", instance.getProtocol());
        writeNonNullNumber(generator, "version", instance.getVersion());
        writeNonBlankField(generator, "visit", instance.getVisit());
        writeNonBlankField(generator, "visitId", instance.getVisitId());
        writeNonBlankField(generator, "project", instance.getProject());
        writeNonBlankField(generator, "acquisitionSite", instance.getAcquisitionSite());
        writeNonBlankField(generator, "note", instance.getNote());
        writeNonNullField(generator, "duration", instance.getDuration());
        writeNonNullField(generator, "time", instance.getTime());
        writeNonNullField(generator, "date", instance.getDate());
        writeNonNullField(generator, "fields", instance.getFields_field());
        writeNonNullField(generator, "resources", instance.getResources_resource());
        writeNonNullField(generator, "sharing", instance.getSharing_share());
        generator.writeObjectField("investigator", instance.getInvestigator());
        generator.writeObjectField("validation", instance.getValidation());
    }
}
