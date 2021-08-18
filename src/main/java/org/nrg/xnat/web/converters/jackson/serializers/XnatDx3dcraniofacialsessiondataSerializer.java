package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDx3dcraniofacialsessiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatDx3dcraniofacialsessiondataSerializer<T extends XnatDx3dcraniofacialsessiondata> extends XnatImagesessiondataSerializer<T> {
    private static final long serialVersionUID = -3671015328038616267L;

    @SuppressWarnings("unchecked")
    public XnatDx3dcraniofacialsessiondataSerializer() {
        this((Class<T>) XnatDx3dcraniofacialsessiondata.class);
    }

    protected XnatDx3dcraniofacialsessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // No class-specific properties to serialize
        super.serializeImpl(instance, generator, provider);
    }
}

