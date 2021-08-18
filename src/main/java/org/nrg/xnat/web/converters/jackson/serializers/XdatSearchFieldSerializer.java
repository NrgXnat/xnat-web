package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatSearchField;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatSearchFieldSerializer<T extends XdatSearchField> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -3737501241855325874L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatSearchFieldSerializer() {
        this((Class<T>) XdatSearchField.class);
    }

    protected XdatSearchFieldSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "elementName", instance.getElementName());
        writeNonBlankField(generator, "fieldId", instance.getFieldId());
        writeNonBlankField(generator, "header", instance.getHeader());
        writeNonNullNumber(generator, "sequence", instance.getSequence());
        writeNonBlankField(generator, "type", instance.getType());
        writeNonBlankField(generator, "value", instance.getValue());
        writeNonNullBoolean(generator, "visible", instance.getVisible());
        writeNonNullNumber(generator, "xdatSearchFieldId", instance.getXdatSearchFieldId());
    }
}

