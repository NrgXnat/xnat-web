package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatDcmentry;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CatDcmentryDeserializer<T extends CatDcmentry> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -5168165575249015540L;

    @SuppressWarnings("unchecked")
    public CatDcmentryDeserializer() {
        this((Class<T>) CatDcmentry.class);
    }

    public CatDcmentryDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "entry":
                // TODO: Handle the "entry" property here: org.nrg.xdat.om.CatEntry
                break;
            case "instancenumber":
                // TODO: Handle the "instancenumber" property here: Integer
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

