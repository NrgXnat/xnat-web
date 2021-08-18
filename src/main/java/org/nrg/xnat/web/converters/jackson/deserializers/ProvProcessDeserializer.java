package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ProvProcess;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ProvProcessDeserializer<T extends ProvProcess> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 5937257802247199332L;

    @SuppressWarnings({"unchecked", "unused"})
    public ProvProcessDeserializer() {
        this((Class<T>) ProvProcess.class);
    }

    protected ProvProcessDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "processstep":
                // TODO: Handle the "processstep" property here: java.util.List
                break;
            case "provProcessId":
                instance.setProvProcessId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

