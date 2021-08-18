package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatActionType;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatActionTypeDeserializer<T extends XdatActionType> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2476397430425184290L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatActionTypeDeserializer() {
        this((Class<T>) XdatActionType.class);
    }

    protected XdatActionTypeDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "actionName":
                instance.setActionName(parser.getText());
                break;
            case "displayName":
                instance.setDisplayName(parser.getText());
                break;
            case "sequence":
                instance.setSequence(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

