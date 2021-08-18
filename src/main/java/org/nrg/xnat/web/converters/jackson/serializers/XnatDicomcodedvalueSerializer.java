package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDicomcodedvalue;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatDicomcodedvalueSerializer<T extends XnatDicomcodedvalue> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 700137957784738766L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatDicomcodedvalueSerializer() {
        this((Class<T>) XnatDicomcodedvalue.class);
    }

    protected XnatDicomcodedvalueSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "designator", instance.getDesignator());
        writeNonBlankField(generator, "meaning", instance.getMeaning());
        writeNonBlankField(generator, "value", instance.getValue());
        writeNonBlankField(generator, "version", instance.getVersion());
        writeNonNullNumber(generator, "xnatDicomcodedvalueId", instance.getXnatDicomcodedvalueId());
    }
}

