package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatOtherdicomsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatOtherdicomsessiondataSerializer<T extends XnatOtherdicomsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = -1344288124579728086L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatOtherdicomsessiondataSerializer() {
        this((Class<T>) XnatOtherdicomsessiondata.class);
    }

    protected XnatOtherdicomsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

