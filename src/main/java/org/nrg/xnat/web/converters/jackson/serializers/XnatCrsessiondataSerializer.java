package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatCrsessiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatCrsessiondataSerializer<T extends XnatCrsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = 8658472985438578233L;

    @SuppressWarnings("unchecked")
    public XnatCrsessiondataSerializer() {
        this((Class<T>) XnatCrsessiondata.class);
    }

    protected XnatCrsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(instance, generator, provider);
    }
}
