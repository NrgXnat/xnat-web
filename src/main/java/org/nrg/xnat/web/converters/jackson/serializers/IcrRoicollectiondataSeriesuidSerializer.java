package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.IcrRoicollectiondataSeriesuid;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class IcrRoicollectiondataSeriesuidSerializer<T extends IcrRoicollectiondataSeriesuid> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -1995015025750297678L;

    @SuppressWarnings("unchecked")
    public IcrRoicollectiondataSeriesuidSerializer() {
        this((Class<T>) IcrRoicollectiondataSeriesuid.class);
    }

    protected IcrRoicollectiondataSeriesuidSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "icrRoicollectiondataSeriesuidId" property here: Integer
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "seriesuid" property here: String
    }
}

