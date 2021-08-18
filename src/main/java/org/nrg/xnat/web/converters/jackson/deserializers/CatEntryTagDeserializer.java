package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatEntryTag;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class CatEntryTagDeserializer<T extends CatEntryTag> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -177081133173841174L;

    @SuppressWarnings({"unchecked", "unused"})
    public CatEntryTagDeserializer() {
        this((Class<T>) CatEntryTag.class);
    }

    protected CatEntryTagDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "catEntryTagId":
                instance.setCatEntryTagId(parser.getIntValue());
                break;
            case "tag":
                instance.setTag(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

