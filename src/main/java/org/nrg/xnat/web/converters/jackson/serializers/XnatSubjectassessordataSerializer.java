package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatSubjectassessordata;

import java.io.IOException;

@Slf4j
public abstract class XnatSubjectassessordataSerializer<T extends XnatSubjectassessordata> extends XnatExperimentdataSerializer<T> {
    private static final long serialVersionUID = -7627758235719693208L;

    @SuppressWarnings("unchecked")
    public XnatSubjectassessordataSerializer() {
        this((Class<T>) XnatSubjectassessordata.class);
    }

    protected XnatSubjectassessordataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(instance, generator, provider);
        writeNonNullField(generator, "subjectId", instance.getSubjectId());
        writeNonNullNumber(generator, "age", instance.getAge());
    }
}
