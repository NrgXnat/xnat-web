package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatImagescandataShare;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatImagescandataShareDeserializer<T extends XnatImagescandataShare> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -1332467359488341086L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatImagescandataShareDeserializer() {
        this((Class<T>) XnatImagescandataShare.class);
    }

    protected XnatImagescandataShareDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "label":
                instance.setLabel(parser.getText());
                break;
            case "project":
                instance.setProject(parser.getText());
                break;
            case "share":
                instance.setShare(parser.getText());
                break;
            case "xnatImagescandataShareId":
                instance.setXnatImagescandataShareId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

