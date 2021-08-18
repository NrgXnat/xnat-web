package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.IcrRoicollectiondataSeriesuid;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class IcrRoicollectiondataSeriesuidSerializer<T extends IcrRoicollectiondataSeriesuid> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -1995015025750297678L;

    @SuppressWarnings({"unchecked", "unused"})
    public IcrRoicollectiondataSeriesuidSerializer() {
        this((Class<T>) IcrRoicollectiondataSeriesuid.class);
    }

    protected IcrRoicollectiondataSeriesuidSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "icrRoicollectiondataSeriesuidId", instance.getIcrRoicollectiondataSeriesuidId());
        writeNonBlankField(generator, "seriesuid", instance.getSeriesuid());
    }
}

