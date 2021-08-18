package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcFieldspecification;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ArcFieldspecificationDeserializer<T extends ArcFieldspecification> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 8174446330290809622L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcFieldspecificationDeserializer() {
        this((Class<T>) ArcFieldspecification.class);
    }

    protected ArcFieldspecificationDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "arcFieldspecificationId":
                instance.setArcFieldspecificationId(parser.getIntValue());
                break;
            case "fieldspecification":
                instance.setFieldspecification(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

