package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDx3dcraniofacialscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatDx3dcraniofacialscandataDeserializer<T extends XnatDx3dcraniofacialscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = -6885310518709278227L;

    @SuppressWarnings("unchecked")
    public XnatDx3dcraniofacialscandataDeserializer() {
        this((Class<T>) XnatDx3dcraniofacialscandata.class);
    }

    public XnatDx3dcraniofacialscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // No class-specific properties to deserialize
        super.handleField(instance, field, parser, context);
    }
}

