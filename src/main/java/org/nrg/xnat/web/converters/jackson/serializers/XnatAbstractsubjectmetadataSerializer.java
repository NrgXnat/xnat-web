package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractsubjectmetadata;

import java.io.IOException;

@Slf4j
public abstract class XnatAbstractsubjectmetadataSerializer<T extends XnatAbstractsubjectmetadata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -2002556991961905005L;

    @SuppressWarnings("unchecked")
    public XnatAbstractsubjectmetadataSerializer() {
        this((Class<T>) XnatAbstractsubjectmetadata.class);
    }

    protected XnatAbstractsubjectmetadataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xnatAbstractsubjectmetadataId" property here: Integer
    }
}

