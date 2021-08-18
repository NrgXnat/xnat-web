package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMgsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatMgsessiondataSerializer<T extends XnatMgsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = 3493234657519484552L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatMgsessiondataSerializer() {
        this((Class<T>) XnatMgsessiondata.class);
    }

    protected XnatMgsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

