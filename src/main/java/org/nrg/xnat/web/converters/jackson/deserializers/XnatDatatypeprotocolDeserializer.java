package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDatatypeprotocol;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatDatatypeprotocolDeserializer<T extends XnatDatatypeprotocol> extends XnatAbstractprotocolDeserializer<T> {
    private static final long serialVersionUID = 3313417256437766930L;

    @SuppressWarnings("unchecked")
    public XnatDatatypeprotocolDeserializer() {
        super((Class<T>) XnatDatatypeprotocol.class);
    }

    protected XnatDatatypeprotocolDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "id":
                instance.setId(parser.getText());
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "xnatAbstractProtocolId":
                instance.setXnatAbstractprotocolId(parser.getIntValue());
                break;
            case "dataType":
                instance.setDataType(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
