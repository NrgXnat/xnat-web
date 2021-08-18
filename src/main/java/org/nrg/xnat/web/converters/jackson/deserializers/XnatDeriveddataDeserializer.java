package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDeriveddata;

import java.io.IOException;

@Slf4j
public abstract class XnatDeriveddataDeserializer<T extends XnatDeriveddata> extends XnatExperimentdataDeserializer<T> {
    private static final long serialVersionUID = -4152269491457760788L;

    protected XnatDeriveddataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "provenance":
                // TODO: Handle the "provenance" property here: org.nrg.xdat.model.ProvProcessI
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

