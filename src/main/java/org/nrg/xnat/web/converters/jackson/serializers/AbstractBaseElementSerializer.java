package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.base.BaseElement;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public abstract class AbstractBaseElementSerializer<T extends BaseElement> extends StdSerializer<T> {
    protected AbstractBaseElementSerializer(final Class<T> dataType) {
        super(dataType);
        _dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    }

    protected abstract void serializeImpl(final T element, final JsonGenerator generator, final SerializerProvider provider) throws IOException;

    @Override
    public void serialize(final T element, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        if (element != null) {
            generator.writeStartObject();
            serializeImpl(element, generator, provider);
            generator.writeEndObject();
        }
    }

    protected void writeNonBlankField(final JsonGenerator generator, final String name, final String value) throws IOException {
        if (StringUtils.isNotBlank(value)) {
            generator.writeStringField(name, value);
        }
    }

    /**
     * Writes a field named <b>name</b> with the the value inserted directly as JSON, i.e. no escaping of significant characters.
     * This allows the "conversion" of JSON stored as a string into JSON directly in the serialized output.
     *
     * @param generator The generator for the serialization operation.
     * @param name      The name of the field to write.
     * @param value     The value to be inserted.
     *
     * @throws IOException When an error occurs during the serialization write operations.
     */
    protected void writeNonBlankJson(final JsonGenerator generator, final String name, final String value) throws IOException {
        if (StringUtils.isNotBlank(value)) {
            generator.writeFieldName(name);
            generator.writeRawValue(value);
        }
    }

    protected void writeNonNullField(final JsonGenerator generator, final String name, final Object value) throws IOException {
        if (value != null) {
            generator.writeObjectField(name, value);
        }
    }

    protected void writeNonNullBoolean(final JsonGenerator generator, final String name, final Boolean value) throws IOException {
        if (value != null) {
            generator.writeBooleanField(name, value);
        }
    }

    protected void writeNonNullNumber(final JsonGenerator generator, final String name, final Number value) throws IOException {
        if (value != null) {
            if (value instanceof Integer) {
                generator.writeNumberField(name, (Integer) value);
            } else if (value instanceof Long) {
                generator.writeNumberField(name, (Long) value);
            } else if (value instanceof Float) {
                generator.writeNumberField(name, (float) value);
            } else if (value instanceof Double) {
                generator.writeNumberField(name, (Double) value);
            } else if (value instanceof Short) {
                generator.writeNumberField(name, (Short) value);
            } else {
                generator.writeStringField(name, value.toString());
            }
        }
    }

    protected void writeNonNullDate(final JsonGenerator generator, final String name, final Date date) throws IOException {
        if (date != null) {
            generator.writeObjectField(name, _dateFormatter.format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
        }
    }

    private final DateTimeFormatter _dateFormatter;
}
