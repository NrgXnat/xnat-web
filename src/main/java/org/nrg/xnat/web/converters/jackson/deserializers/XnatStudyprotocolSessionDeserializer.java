package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStudyprotocolSession;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatStudyprotocolSessionDeserializer<T extends XnatStudyprotocolSession> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -75645628750624637L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatStudyprotocolSessionDeserializer() {
        this((Class<T>) XnatStudyprotocolSession.class);
    }

    protected XnatStudyprotocolSessionDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "id":
                instance.setId(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "xnatStudyprotocolSessionId":
                instance.setXnatStudyprotocolSessionId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

