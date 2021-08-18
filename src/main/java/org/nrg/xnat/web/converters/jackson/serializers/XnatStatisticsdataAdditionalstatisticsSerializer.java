package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStatisticsdataAdditionalstatistics;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatStatisticsdataAdditionalstatisticsSerializer<T extends XnatStatisticsdataAdditionalstatistics> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -570756789408898989L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatStatisticsdataAdditionalstatisticsSerializer() {
        this((Class<T>) XnatStatisticsdataAdditionalstatistics.class);
    }

    protected XnatStatisticsdataAdditionalstatisticsSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "additionalstatistics", instance.getAdditionalstatistics());
        writeNonBlankField(generator, "name", instance.getName());
        writeNonNullNumber(generator, "xnatStatisticsdataAdditionalstatisticsId", instance.getXnatStatisticsdataAdditionalstatisticsId());
    }
}

