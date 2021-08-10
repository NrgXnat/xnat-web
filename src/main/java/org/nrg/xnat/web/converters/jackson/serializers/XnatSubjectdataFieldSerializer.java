package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectdataField;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatSubjectdataFieldSerializer<T extends XnatSubjectdataField> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 8848543886827005457L;

    @SuppressWarnings("unchecked")
    public XnatSubjectdataFieldSerializer() {
        this((Class<T>) XnatSubjectdataField.class);
    }

    protected XnatSubjectdataFieldSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "field" property here: String
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xnatSubjectdataFieldId" property here: Integer
    }
}

