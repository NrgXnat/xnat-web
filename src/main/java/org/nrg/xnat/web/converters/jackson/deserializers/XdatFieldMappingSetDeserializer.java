package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatFieldMappingSet;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatFieldMappingSetDeserializer<T extends XdatFieldMappingSet> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 8663425353913482416L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatFieldMappingSetDeserializer() {
        this((Class<T>) XdatFieldMappingSet.class);
    }

    protected XdatFieldMappingSetDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "allow":
                // TODO: Handle the "allow" property here: java.util.ArrayList
                break;
            case "method":
                instance.setMethod(parser.getText());
                break;
            case "subSet":
                // TODO: Handle the "subSet" property here: java.util.ArrayList
                break;
            case "xdatFieldMappingSetId":
                instance.setXdatFieldMappingSetId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

