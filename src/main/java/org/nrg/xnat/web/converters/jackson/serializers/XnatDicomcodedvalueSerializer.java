package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDicomcodedvalue;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatDicomcodedvalueSerializer<T extends XnatDicomcodedvalue> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 700137957784738766L;

    @SuppressWarnings("unchecked")
    public XnatDicomcodedvalueSerializer() {
        this((Class<T>) XnatDicomcodedvalue.class);
    }

    protected XnatDicomcodedvalueSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "designator" property here: String
        // TODO: Write out the "meaning" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "value" property here: String
        // TODO: Write out the "version" property here: String
        // TODO: Write out the "xnatDicomcodedvalueId" property here: Integer
    }
}

