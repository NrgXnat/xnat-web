package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectmetadata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatSubjectmetadataSerializer<T extends XnatSubjectmetadata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 1232894851422323787L;

    @SuppressWarnings("unchecked")
    public XnatSubjectmetadataSerializer() {
        this((Class<T>) XnatSubjectmetadata.class);
    }

    protected XnatSubjectmetadataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "abstractsubjectmetadata" property here: org.nrg.xdat.om.XnatAbstractsubjectmetadata
        // TODO: Write out the "cohort" property here: String
        // TODO: Write out the "schemaElementName" property here: String
    }
}

