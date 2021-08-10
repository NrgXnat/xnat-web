package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatFieldMappingSet;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatFieldMappingSetDeserializer<T extends XdatFieldMappingSet> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 6301794381703416516L;

    @SuppressWarnings("unchecked")
    public XdatFieldMappingSetDeserializer() {
        this((Class<T>) XdatFieldMappingSet.class);
    }

    public XdatFieldMappingSetDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "allow":
                // TODO: Handle the "allow" property here: java.util.ArrayList
                break;
            case "method":
                // TODO: Handle the "method" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "subSet":
                // TODO: Handle the "subSet" property here: java.util.ArrayList
                break;
            case "xdatFieldMappingSetId":
                // TODO: Handle the "xdatFieldMappingSetId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

