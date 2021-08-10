package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatXcscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatXcscandataSerializer<T extends XnatXcscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = -2280595938099074217L;

    @SuppressWarnings("unchecked")
    public XnatXcscandataSerializer() {
        this((Class<T>) XnatXcscandata.class);
    }

    protected XnatXcscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T scan, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(scan, generator, provider);
    }
}
