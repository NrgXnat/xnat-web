package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatCtsessiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatCtsessiondataSerializer<T extends XnatCtsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = -2317154238007680655L;

    @SuppressWarnings("unchecked")
    public XnatCtsessiondataSerializer() {
        this((Class<T>) XnatCtsessiondata.class);
    }

    protected XnatCtsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(instance, generator, provider);
        writeNonBlankField(generator, "id", instance.getId());
        writeNonBlankField(generator, "label", instance.getLabel());
        writeNonBlankField(generator, "project", instance.getProject());
        writeNonBlankField(generator, "note", instance.getNote());
        writeNonBlankField(generator, "protocol", instance.getProtocol());
        writeNonBlankField(generator, "original", instance.getOriginal());
        writeNonNullField(generator, "date", instance.getDate());
        writeNonNullNumber(generator, "delay", instance.getDelay());
        writeNonNullNumber(generator, "version", instance.getVersion());
        writeNonBlankField(generator, "acquisitionSite", instance.getAcquisitionSite());
        writeNonBlankField(generator, "visit", instance.getVisit());
        writeNonBlankField(generator, "visitId", instance.getVisitId());
        writeNonBlankField(generator, "description", instance.getDescription());
        writeNonNullField(generator, "subjectId", instance.getSubjectId());
        // writeNonNullField(generator, "experiment", xnatCrsessiondata.getExperimentdata());
    }

}
