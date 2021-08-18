package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValAdditionalval;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ValAdditionalvalDeserializer<T extends ValAdditionalval> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 222899452428190901L;

    @SuppressWarnings({"unchecked", "unused"})
    public ValAdditionalvalDeserializer() {
        this((Class<T>) ValAdditionalval.class);
    }

    protected ValAdditionalvalDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "valAdditionalvalId":
                instance.setValAdditionalvalId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

