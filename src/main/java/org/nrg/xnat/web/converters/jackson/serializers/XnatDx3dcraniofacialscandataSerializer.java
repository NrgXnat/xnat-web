package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDx3dcraniofacialscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatDx3dcraniofacialscandataSerializer<T extends XnatDx3dcraniofacialscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = -9129221644212368829L;

    @SuppressWarnings("unchecked")
    public XnatDx3dcraniofacialscandataSerializer() {
        this((Class<T>) XnatDx3dcraniofacialscandata.class);
    }

    protected XnatDx3dcraniofacialscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

