package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatGenericdata;

import java.io.IOException;

@Slf4j
public abstract class XnatGenericdataSerializer<T extends XnatGenericdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -7772705614457677723L;

    @SuppressWarnings("unchecked")
    public XnatGenericdataSerializer() {
        this((Class<T>) XnatGenericdata.class);
    }

    protected XnatGenericdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "experimentdata" property here: org.nrg.xdat.om.XnatExperimentdata
        // TODO: Write out the "schemaElementName" property here: String
    }
}

