package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractprotocol;

import java.io.IOException;

@Slf4j
public abstract class XnatAbstractprotocolDeserializer<T extends XnatAbstractprotocol> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 769619859928799874L;

    protected XnatAbstractprotocolDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "dataType":
                instance.setDataType(parser.getText());
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "id":
                instance.setId(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "xnatAbstractprotocolId":
                instance.setXnatAbstractprotocolId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

