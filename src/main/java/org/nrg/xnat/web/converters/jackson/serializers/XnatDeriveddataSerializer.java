package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDeriveddata;

import java.io.IOException;

@Slf4j
public abstract class XnatDeriveddataSerializer<T extends XnatDeriveddata> extends XnatExperimentdataSerializer<T> {
    private static final long serialVersionUID = 2830363100592516226L;

    protected XnatDeriveddataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(instance, generator, provider);
        generator.writeObjectField("provenance", instance.getProvenance());
    }
}