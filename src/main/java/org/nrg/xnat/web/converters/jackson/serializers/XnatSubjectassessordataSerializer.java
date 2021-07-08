package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatSubjectassessordata;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class XnatSubjectassessordataSerializer extends AbstractBaseElementSerializer<XnatSubjectassessordata> {
	private static final long serialVersionUID = -7627758235719693208L;

	public XnatSubjectassessordataSerializer() {
        super(XnatSubjectassessordata.class);
    }

    @Override
    protected void serializeImpl(final XnatSubjectassessordata xnatSubjectassessordata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonNullField(generator, "subjectId", xnatSubjectassessordata.getSubjectId());
        writeNonNullNumber(generator, "age", xnatSubjectassessordata.getAge());
        writeNonBlankField(generator, "xsiType", xnatSubjectassessordata.getXSIType());
        provider.findValueSerializer(XnatExperimentdata.class).serialize(this, generator, provider);
    }
}
