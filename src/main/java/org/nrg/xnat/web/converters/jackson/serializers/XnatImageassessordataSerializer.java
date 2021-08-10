package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatImageassessordata;

import java.io.IOException;

@Slf4j
public abstract class XnatImageassessordataSerializer<T extends XnatImageassessordata> extends XnatDeriveddataSerializer<T> {
    private static final long serialVersionUID = -5866321865427372083L;

    @SuppressWarnings("unchecked")
    public XnatImageassessordataSerializer() {
        this((Class<T>) XnatImageassessordata.class);
    }

    protected XnatImageassessordataSerializer(final Class<T> clazz) {
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
        writeNonBlankField(generator, "description", instance.getDescription());
        writeNonNullField(generator, "fields", instance.getFields_field());
        writeNonNullField(generator, "resources", instance.getResources_resource());
        writeNonNullField(generator, "experiment", instance.getExperimentdata());
        writeNonNullField(generator, "sessionData", instance.getImageSessionData());
        writeNonNullField(generator, "scans", instance.getImageSessionData().getScans_scan());
        writeNonNullField(generator, "outFile", instance.getOut_file());
    }
}
