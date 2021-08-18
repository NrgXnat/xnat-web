package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatCtscandataFocalspot;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatCtscandataFocalspotDeserializer<T extends XnatCtscandataFocalspot> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1101402760537308644L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatCtscandataFocalspotDeserializer() {
        this((Class<T>) XnatCtscandataFocalspot.class);
    }

    protected XnatCtscandataFocalspotDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "focalspot":
                instance.setFocalspot(parser.getDoubleValue());
                break;
            case "xnatCtscandataFocalspotId":
                instance.setXnatCtscandataFocalspotId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

