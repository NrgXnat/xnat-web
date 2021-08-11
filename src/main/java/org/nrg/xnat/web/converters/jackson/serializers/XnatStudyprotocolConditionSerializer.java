package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStudyprotocolCondition;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatStudyprotocolConditionSerializer<T extends XnatStudyprotocolCondition> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -1538727363268083086L;

    @SuppressWarnings("unchecked")
    public XnatStudyprotocolConditionSerializer() {
        this((Class<T>) XnatStudyprotocolCondition.class);
    }

    protected XnatStudyprotocolConditionSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "description" property here: String
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xnatStudyprotocolConditionId" property here: Integer
    }
}

