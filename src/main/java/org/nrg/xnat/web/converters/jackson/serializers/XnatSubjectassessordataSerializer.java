package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatSubjectassessordata;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

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
    protected void serializeImpl(final XnatSubjectassessordata xnatSubjectassessordata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(xnatSubjectassessordata, generator, provider);
        writeNonNullField(generator, "subjectId", xnatSubjectassessordata.getSubjectId());
        writeNonNullNumber(generator, "age", xnatSubjectassessordata.getAge());
        generator.writeFieldName("experiment");
        provider.findValueSerializer(XnatExperimentdata.class).serialize(xnatSubjectassessordata, generator, provider);
    }
}
