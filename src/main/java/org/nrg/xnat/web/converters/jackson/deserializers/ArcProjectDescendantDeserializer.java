package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcProjectDescendant;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ArcProjectDescendantDeserializer<T extends ArcProjectDescendant> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -6571465769663938208L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcProjectDescendantDeserializer() {
        this((Class<T>) ArcProjectDescendant.class);
    }

    protected ArcProjectDescendantDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "arcProjectDescendantId":
                instance.setArcProjectDescendantId(parser.getIntValue());
                break;
            case "pipeline":
                // TODO: Handle the "pipeline" property here: java.util.List
                break;
            case "xsitype":
                instance.setXsitype(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

