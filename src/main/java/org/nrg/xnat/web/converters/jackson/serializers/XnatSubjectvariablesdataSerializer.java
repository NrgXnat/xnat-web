package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectvariablesdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatSubjectvariablesdataSerializer<T extends XnatSubjectvariablesdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -6871178784231535753L;

    @SuppressWarnings("unchecked")
    public XnatSubjectvariablesdataSerializer() {
        this((Class<T>) XnatSubjectvariablesdata.class);
    }

    protected XnatSubjectvariablesdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "subjectassessordata" property here: org.nrg.xdat.om.XnatSubjectassessordata
        // TODO: Write out the "variables_variable" property here: java.util.List
    }
}

