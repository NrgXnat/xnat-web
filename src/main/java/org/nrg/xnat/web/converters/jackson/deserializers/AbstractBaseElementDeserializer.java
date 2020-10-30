package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.nrg.xdat.base.BaseElement;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public abstract class AbstractBaseElementDeserializer<T extends BaseElement> extends StdDeserializer<T> {
    protected AbstractBaseElementDeserializer(final Class<T> dataType) {
        super(dataType);
    }

    @Override
    public abstract T deserialize(final JsonParser parser, final DeserializationContext context) throws IOException;

    protected Date parseDate(final String date) {
        return Date.from(LocalDate.parse(date).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    protected String stringify(final JsonParser parser, final DeserializationContext context) throws IOException {
        if (parser.getCurrentToken() == JsonToken.START_ARRAY || parser.getCurrentToken() == JsonToken.START_OBJECT) {
            return context.readValue(parser, JsonNode.class).toString();
        } else if (parser.getCurrentToken() == JsonToken.VALUE_FALSE) {
            return Boolean.FALSE.toString();
        } else if (parser.getCurrentToken() == JsonToken.VALUE_TRUE) {
            return Boolean.TRUE.toString();
        } else if (parser.getCurrentToken() == JsonToken.VALUE_NULL) {
            return null;
        } else if (parser.getCurrentToken() == JsonToken.VALUE_NUMBER_FLOAT) {
            return Float.toString(parser.getFloatValue());
        } else if (parser.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
            return Integer.toString(parser.getIntValue());
        } else if (parser.getCurrentToken() == JsonToken.VALUE_STRING) {
            return parser.getText();
        } else {
            return parser.getValueAsString();
        }
    }
}
