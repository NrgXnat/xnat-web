package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAlgorithm;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatAlgorithmSerializer<T extends XnatAlgorithm> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -5150532412019771772L;

    @SuppressWarnings("unchecked")
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
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "namecode" property here: org.nrg.xdat.model.XnatDicomcodedvalueI
        // TODO: Write out the "parameters" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "source" property here: String
        // TODO: Write out the "version" property here: String
        // TODO: Write out the "xnatAlgorithmId" property here: Integer
    }
}

