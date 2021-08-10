package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStudyprotocolGroup;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatStudyprotocolGroupSerializer<T extends XnatStudyprotocolGroup> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 2340862010304839247L;

    @SuppressWarnings("unchecked")
    public XnatStudyprotocolGroupSerializer() {
        this((Class<T>) XnatStudyprotocolGroup.class);
    }

    protected XnatStudyprotocolGroupSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "description" property here: String
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xnatStudyprotocolGroupId" property here: Integer
    }
}

