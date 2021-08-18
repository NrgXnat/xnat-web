package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectassessordata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatSubjectassessordataSerializer<T extends XnatSubjectassessordata> extends XnatExperimentdataSerializer<T> {
    private static final long serialVersionUID = -8253430233861767560L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSubjectassessordataSerializer() {
        this((Class<T>) XnatSubjectassessordata.class);
    }

    protected XnatSubjectassessordataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonNullNumber(generator, "age", instance.getAge());
        writeNonNullField(generator, "subjectId", instance.getSubjectId());
        super.serializeImpl(instance, generator, provider);
    }
}
