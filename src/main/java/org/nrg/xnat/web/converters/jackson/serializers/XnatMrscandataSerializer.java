package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatMrscandataSerializer<T extends XnatMrscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = -2280595938099074217L;

    @SuppressWarnings("unchecked")
    public XnatMrscandataSerializer() {
        this((Class<T>) XnatMrscandata.class);
    }

    protected XnatMrscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T scan, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(scan, generator, provider);
        // Handle all properties unique to mrScanData
    }
}
