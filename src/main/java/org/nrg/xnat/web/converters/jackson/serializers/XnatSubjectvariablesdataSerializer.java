package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectvariablesdata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatSubjectvariablesdataSerializer<T extends XnatSubjectvariablesdata> extends XnatSubjectassessordataSerializer<T> {
    private static final long serialVersionUID = -5631358071313424072L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSubjectvariablesdataSerializer() {
        this((Class<T>) XnatSubjectvariablesdata.class);
    }

    protected XnatSubjectvariablesdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "variables_variable" property here: java.util.List
        super.serializeImpl(instance, generator, provider);
    }
}

