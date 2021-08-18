package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDx3dcraniofacialsessiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatDx3dcraniofacialsessiondataDeserializer<T extends XnatDx3dcraniofacialsessiondata> extends XnatImagesessiondataDeserializer<T> {
    private static final long serialVersionUID = 2103313965741638598L;

    @SuppressWarnings("unchecked")
    public XnatDx3dcraniofacialsessiondataDeserializer() {
        this((Class<T>) XnatDx3dcraniofacialsessiondata.class);
    }

    public XnatDx3dcraniofacialsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

