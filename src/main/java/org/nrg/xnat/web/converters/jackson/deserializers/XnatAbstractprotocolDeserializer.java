package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractprotocol;

import java.io.IOException;

@Slf4j
public abstract class XnatAbstractprotocolDeserializer<T extends XnatAbstractprotocol> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -8950581491368249478L;

    protected XnatAbstractprotocolDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "id":
                instance.setId(parser.getText());
                break;
            case "xnatAbstractProtocolId":
                instance.setXnatAbstractprotocolId(parser.getIntValue());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "dataType":
                instance.setDataType(parser.getText());
                break;
            case "user":
                instance.setUser(getUserI(parser.getText()));
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
