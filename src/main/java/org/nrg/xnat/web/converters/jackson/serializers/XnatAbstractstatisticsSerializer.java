package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractstatistics;

import java.io.IOException;

@Slf4j
public abstract class XnatAbstractstatisticsSerializer<T extends XnatAbstractstatistics> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 8234550960994856077L;

    @SuppressWarnings("unchecked")
    public XnatAbstractstatisticsSerializer() {
        this((Class<T>) XnatAbstractstatistics.class);
    }

    protected XnatAbstractstatisticsSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xnatAbstractstatisticsId" property here: Integer
    }
}

