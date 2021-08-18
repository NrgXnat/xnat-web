package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDeriveddata;

import java.io.IOException;

@Slf4j
public abstract class XnatDeriveddataSerializer<T extends XnatDeriveddata> extends XnatExperimentdataSerializer<T> {
    private static final long serialVersionUID = -8918838813954958775L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatDeriveddataSerializer() {
        this((Class<T>) XnatDeriveddata.class);
    }

    protected XnatDeriveddataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "provenance" property here: org.nrg.xdat.model.ProvProcessI
        super.serializeImpl(instance, generator, provider);
    }
}

