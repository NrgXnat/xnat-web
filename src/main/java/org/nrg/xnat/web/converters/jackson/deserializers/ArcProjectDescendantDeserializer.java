package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcProjectDescendant;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ArcProjectDescendantDeserializer<T extends ArcProjectDescendant> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -5322398695667766647L;

    @SuppressWarnings("unchecked")
    public ArcProjectDescendantDeserializer() {
        this((Class<T>) ArcProjectDescendant.class);
    }

    public ArcProjectDescendantDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "arcProjectDescendantId":
                // TODO: Handle the "arcProjectDescendantId" property here: Integer
                break;
            case "pipeline":
                // TODO: Handle the "pipeline" property here: java.util.List
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xsitype":
                // TODO: Handle the "xsitype" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

