package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStatisticsdata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatStatisticsdataSerializer<T extends XnatStatisticsdata> extends XnatAbstractstatisticsSerializer<T> {
    private static final long serialVersionUID = -7781176853155821721L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatStatisticsdataSerializer() {
        this((Class<T>) XnatStatisticsdata.class);
    }

    protected XnatStatisticsdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "addfield" property here: java.util.List
        // TODO: Write out the "additionalstatistics" property here: java.util.List
        writeNonNullNumber(generator, "max", instance.getMax());
        writeNonNullNumber(generator, "mean", instance.getMean());
        writeNonNullNumber(generator, "min", instance.getMin());
        writeNonNullNumber(generator, "noOfVoxels", instance.getNoOfVoxels());
        writeNonNullNumber(generator, "snr", instance.getSnr());
        writeNonNullNumber(generator, "stddev", instance.getStddev());
        super.serializeImpl(instance, generator, provider);
    }
}

