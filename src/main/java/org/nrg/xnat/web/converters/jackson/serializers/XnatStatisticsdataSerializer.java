package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStatisticsdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatStatisticsdataSerializer<T extends XnatStatisticsdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 6399023170881223864L;

    @SuppressWarnings("unchecked")
    public XnatStatisticsdataSerializer() {
        this((Class<T>) XnatStatisticsdata.class);
    }

    protected XnatStatisticsdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "abstractstatistics" property here: org.nrg.xdat.om.XnatAbstractstatistics
        // TODO: Write out the "addfield" property here: java.util.List
        // TODO: Write out the "additionalstatistics" property here: java.util.List
        // TODO: Write out the "mean" property here: Double
        // TODO: Write out the "noOfVoxels" property here: Integer
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "stddev" property here: Double
    }
}

