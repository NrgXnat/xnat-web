package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcProperty;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ArcPropertyDeserializer<T extends ArcProperty> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 5758334811927907661L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcPropertyDeserializer() {
        this((Class<T>) ArcProperty.class);
    }

    protected ArcPropertyDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "arcPropertyId":
                instance.setArcPropertyId(parser.getIntValue());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "property":
                instance.setProperty(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

