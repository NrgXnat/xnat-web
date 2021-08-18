package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetassessordata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatPetassessordataSerializer<T extends XnatPetassessordata> extends XnatImageassessordataSerializer<T> {
    private static final long serialVersionUID = -5072607869899766952L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatPetassessordataSerializer() {
        this((Class<T>) XnatPetassessordata.class);
    }

    protected XnatPetassessordataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

