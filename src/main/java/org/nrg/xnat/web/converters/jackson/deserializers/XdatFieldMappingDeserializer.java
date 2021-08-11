package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatFieldMapping;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatFieldMappingDeserializer<T extends XdatFieldMapping> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2040888106326906417L;

    @SuppressWarnings("unchecked")
    public XdatFieldMappingDeserializer() {
        this((Class<T>) XdatFieldMapping.class);
    }

    public XdatFieldMappingDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "activeElement":
                // TODO: Handle the "activeElement" property here: Boolean
                break;
            case "comparisonType":
                // TODO: Handle the "comparisonType" property here: String
                break;
            case "createElement":
                // TODO: Handle the "createElement" property here: Boolean
                break;
            case "deleteElement":
                // TODO: Handle the "deleteElement" property here: Boolean
                break;
            case "editElement":
                // TODO: Handle the "editElement" property here: Boolean
                break;
            case "field":
                // TODO: Handle the "field" property here: String
                break;
            case "fieldValue":
                // TODO: Handle the "fieldValue" property here: String
                break;
            case "readElement":
                // TODO: Handle the "readElement" property here: Boolean
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xdatFieldMappingId":
                // TODO: Handle the "xdatFieldMappingId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

