package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAlgorithm;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatAlgorithmSerializer<T extends XnatAlgorithm> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -5150532412019771772L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatAlgorithmSerializer() {
        this((Class<T>) XnatAlgorithm.class);
    }

    protected XnatAlgorithmSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "family" property here: org.nrg.xdat.model.XnatDicomcodedvalueI
    	writeNonNullField(generator, "family", instance.getFamily());
        writeNonBlankField(generator, "name", instance.getName());
        // TODO: Write out the "namecode" property here: org.nrg.xdat.model.XnatDicomcodedvalueI
        writeNonNullField(generator, "nameCode", instance.getNamecode());
        writeNonBlankField(generator, "parameters", instance.getParameters());
        writeNonBlankField(generator, "source", instance.getSource());
        writeNonBlankField(generator, "version", instance.getVersion());
        writeNonNullNumber(generator, "xnatAlgorithmId", instance.getXnatAlgorithmId());
    }
}

