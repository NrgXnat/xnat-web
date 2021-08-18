package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatEntryMetafield;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class CatEntryMetafieldDeserializer<T extends CatEntryMetafield> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -4269911633062410420L;

    @SuppressWarnings({"unchecked", "unused"})
    public CatEntryMetafieldDeserializer() {
        this((Class<T>) CatEntryMetafield.class);
    }

    protected CatEntryMetafieldDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "catEntryMetafieldId":
                instance.setCatEntryMetafieldId(parser.getIntValue());
                break;
            case "metafield":
                instance.setMetafield(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

