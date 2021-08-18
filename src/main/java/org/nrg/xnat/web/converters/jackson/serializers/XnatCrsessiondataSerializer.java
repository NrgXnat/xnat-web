package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatCrsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatCrsessiondataSerializer<T extends XnatCrsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = 6842289745928608910L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatCrsessiondataSerializer() {
        this((Class<T>) XnatCrsessiondata.class);
    }

    protected XnatCrsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

