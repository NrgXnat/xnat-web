package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.model.XnatAddfieldI;
import org.nrg.xft.security.UserI;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Getter
@Accessors(prefix = "_")
@Slf4j
public abstract class AbstractBaseElementSerializer<T extends BaseElement> extends StdSerializer<T> {
    private static final long serialVersionUID = -436288323864228414L;

    private static final DateTimeFormatter DATE_FORMATTER  = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final  String            FIELD_VOXEL_RES = "voxelRes";
    public static final  String            FIELD_FOV       = "fov";

    private final Class<T> _serializableType;

    public AbstractBaseElementSerializer(final Class<T> clazz) {
        super(clazz);
        _serializableType = clazz;
        log.info("Created the {} serializer for handling instances of the {} class", getClass().getName(), _serializableType.getName());
    }

    protected abstract void serializeImpl(final T element, final JsonGenerator generator, final SerializerProvider provider) throws IOException;

    @Override
    public void serialize(final T element, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        generator.writeStartObject();

        serializeImpl(element, generator, provider);

        writeNonBlankField(generator, "xsiType", element.getXSIType());
        writeNonNullDate(generator, "created", element.getInsertDate());
        final UserI insertUser = element.getInsertUser();
        if (insertUser != null) {
            writeNonBlankField(generator, "createdBy", insertUser.getUsername());
        }
        writeNonNullDate(generator, "lastModified", element.getItem().getLastModified());
        final UserI lastModifiedUser = element.getItem().getUser();
        if (lastModifiedUser != null) {
            writeNonBlankField(generator, "lastModifiedBy", lastModifiedUser.getUsername());
        }
        generator.writeEndObject();
    }

    protected void writeNonBlankField(final JsonGenerator generator, final String name, final String value) throws IOException {
        if (StringUtils.isNotBlank(value)) {
            generator.writeStringField(name, value);
        }
    }

    /**
     * Writes a field named <b>name</b> with the value inserted directly as JSON, i.e. no escaping of significant characters.
     * This allows the "conversion" of JSON stored as a string into JSON directly in the serialized output.
     *
     * @param generator The generator for the serialization operation.
     * @param name      The name of the field to write.
     * @param value     The value to be inserted.
     *
     * @throws IOException When an error occurs during the serialization write operations.
     */
    @SuppressWarnings("unused")
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
            generator.writeObjectField(name, DATE_FORMATTER.format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
        }
    }

    protected void writeVoxelRes(final JsonGenerator generator, final Double x, final Double y, final Double z, final String units) throws IOException {
        if (ObjectUtils.allNotNull(x, y, z, units)) {
            generator.writeObjectFieldStart(FIELD_VOXEL_RES);
            generator.writeNumberField("x", x);
            generator.writeNumberField("y", y);
            generator.writeNumberField("y", z);
            generator.writeStringField("units", units);
            generator.writeEndObject();
        }
    }

    protected void writeFov(final JsonGenerator generator, final Integer x, final Integer y) throws IOException {
        if (ObjectUtils.allNotNull(x, y)) {
            generator.writeObjectFieldStart(FIELD_FOV);
            generator.writeNumberField("x", x);
            generator.writeNumberField("y", y);
            generator.writeEndObject();
        }
    }

    protected void writeDcmValidation(final JsonGenerator generator, final String validation, final Boolean status) throws IOException {
        if (ObjectUtils.anyNotNull(validation, status)) {
            generator.writeObjectFieldStart("dcmValidation");
            writeNonBlankField(generator, "value", validation);
            writeNonNullBoolean(generator, "status", status);
            generator.writeEndObject();
        }
    }
    
    protected void writeParametersAddpParam(final JsonGenerator generator, final List<XnatAddfieldI> parametersAddParams) throws IOException {
    	if (parametersAddParams != null && !parametersAddParams.isEmpty()) {
            generator.writeArrayFieldStart("parametersAddParam");
            for (final XnatAddfieldI parametersAddParam : parametersAddParams) {
                generator.writeObject(parametersAddParam);
            }
            generator.writeEndArray();
        }
    }
}
