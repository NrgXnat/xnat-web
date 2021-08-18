package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ProvProcessstepLibrary;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ProvProcessstepLibraryDeserializer<T extends ProvProcessstepLibrary> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -1616522092116714678L;

    @SuppressWarnings({"unchecked", "unused"})
    public ProvProcessstepLibraryDeserializer() {
        this((Class<T>) ProvProcessstepLibrary.class);
    }

    protected ProvProcessstepLibraryDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "library":
                instance.setLibrary(parser.getText());
                break;
            case "provProcessstepLibraryId":
                instance.setProvProcessstepLibraryId(parser.getIntValue());
                break;
            case "version":
                instance.setVersion(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

