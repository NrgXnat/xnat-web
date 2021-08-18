package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatResourceseries;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatResourceseriesSerializer<T extends XnatResourceseries> extends XnatAbstractresourceSerializer<T> {
    private static final long serialVersionUID = 1290253542884083316L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatResourceseriesSerializer() {
        this((Class<T>) XnatResourceseries.class);
    }

    protected XnatResourceseriesSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "cachepath", instance.getCachepath());
        writeNonBlankField(generator, "content", instance.getContent());
        writeNonNullNumber(generator, "count", instance.getCount());
        writeNonBlankField(generator, "description", instance.getDescription());
        writeNonBlankField(generator, "format", instance.getFormat());
        writeNonBlankField(generator, "name", instance.getName());
        writeNonBlankField(generator, "path", instance.getPath());
        writeNonBlankField(generator, "pattern", instance.getPattern());
        super.serializeImpl(instance, generator, provider);
    }
}

