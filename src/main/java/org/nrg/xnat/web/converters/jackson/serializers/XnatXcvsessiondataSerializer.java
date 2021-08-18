package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatXcvsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatXcvsessiondataSerializer<T extends XnatXcvsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = -2735417058442153690L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatXcvsessiondataSerializer() {
        this((Class<T>) XnatXcvsessiondata.class);
    }

    protected XnatXcvsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

