package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrassessordata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatMrassessordataSerializer<T extends XnatMrassessordata> extends XnatImageassessordataSerializer<T> {
    private static final long serialVersionUID = 2400152724531349723L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatMrassessordataSerializer() {
        this((Class<T>) XnatMrassessordata.class);
    }

    protected XnatMrassessordataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

