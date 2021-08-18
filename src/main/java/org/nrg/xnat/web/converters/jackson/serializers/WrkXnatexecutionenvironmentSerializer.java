package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.WrkXnatexecutionenvironment;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class WrkXnatexecutionenvironmentSerializer<T extends WrkXnatexecutionenvironment> extends WrkAbstractexecutionenvironmentSerializer<T> {
    private static final long serialVersionUID = -741154110603754954L;

    @SuppressWarnings({"unchecked", "unused"})
    public WrkXnatexecutionenvironmentSerializer() {
        this((Class<T>) WrkXnatexecutionenvironment.class);
    }

    protected WrkXnatexecutionenvironmentSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "catalogpath", instance.getCatalogpath());
        writeNonBlankField(generator, "datatype", instance.getDatatype());
        writeNonBlankField(generator, "host", instance.getHost());
        writeNonBlankField(generator, "id", instance.getId());
        writeNonBlankField(generator, "log", instance.getLog());
        // TODO: Write out the "notify" property here: java.util.List
        writeNonBlankField(generator, "parameterfile_path", instance.getParameterfile_path());
        writeNonBlankField(generator, "parameterfile_xml", instance.getParameterfile_xml());
        // TODO: Write out the "parameters_parameter" property here: java.util.List
        writeNonBlankField(generator, "pipeline", instance.getPipeline());
        writeNonBlankField(generator, "startat", instance.getStartat());
        writeNonNullBoolean(generator, "supressnotification", instance.getSupressnotification());
        writeNonBlankField(generator, "xnatuser", instance.getXnatuser());
        super.serializeImpl(instance, generator, provider);
    }
}

