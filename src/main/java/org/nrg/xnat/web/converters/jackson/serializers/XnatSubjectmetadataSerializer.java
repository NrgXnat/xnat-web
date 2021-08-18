package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectmetadata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatSubjectmetadataSerializer<T extends XnatSubjectmetadata> extends XnatAbstractsubjectmetadataSerializer<T> {
    private static final long serialVersionUID = 6635115524884320423L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSubjectmetadataSerializer() {
        this((Class<T>) XnatSubjectmetadata.class);
    }

    protected XnatSubjectmetadataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "cohort", instance.getCohort());
        super.serializeImpl(instance, generator, provider);
    }
}

