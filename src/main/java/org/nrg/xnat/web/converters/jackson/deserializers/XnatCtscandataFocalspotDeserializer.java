package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatCtscandataFocalspot;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatCtscandataFocalspotDeserializer<T extends XnatCtscandataFocalspot> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 640682921526742841L;

    @SuppressWarnings("unchecked")
    public XnatCtscandataFocalspotDeserializer() {
        this((Class<T>) XnatCtscandataFocalspot.class);
    }

    public XnatCtscandataFocalspotDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "focalspot":
                // TODO: Handle the "focalspot" property here: Double
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xnatCtscandataFocalspotId":
                // TODO: Handle the "xnatCtscandataFocalspotId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

