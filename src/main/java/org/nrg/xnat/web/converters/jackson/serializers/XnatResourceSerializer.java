package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatResource;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatResourceSerializer<T extends XnatResource> extends XnatAbstractResourceSerializer<T> {
    private static final long serialVersionUID = 4446488002228061583L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatResourceSerializer() {
        this((Class<T>) XnatResource.class);
    }

    protected XnatResourceSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "cachepath", instance.getCachepath());
        writeNonBlankField(generator, "content", instance.getContent());
        writeNonBlankField(generator, "description", instance.getDescription());
        writeNonBlankField(generator, "format", instance.getFormat());
        // TODO: Write out the "provenance" property here: org.nrg.xdat.model.ProvProcessI
        writeNonBlankField(generator, "uri", instance.getUri());
        super.serializeImpl(instance, generator, provider);
    }
}

