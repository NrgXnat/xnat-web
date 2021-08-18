package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatCtsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatCtsessiondataSerializer<T extends XnatCtsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = -3466753123124025416L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatCtsessiondataSerializer() {
        this((Class<T>) XnatCtsessiondata.class);
    }

    protected XnatCtsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

