package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcProject;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ArcProjectDeserializer<T extends ArcProject> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 2687825671261474280L;

    @SuppressWarnings("unchecked")
    public ArcProjectDeserializer() {
        this((Class<T>) ArcProject.class);
    }

    protected ArcProjectDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "id":
                instance.setId(parser.getText());
                break;
            case "currentArc":
                instance.setCurrentArc(parser.getText());
                break;
            case "prearchiveCode":
                instance.setPrearchiveCode(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
