package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStatisticsdata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatStatisticsdataDeserializer<T extends XnatStatisticsdata> extends XnatAbstractstatisticsDeserializer<T> {
    private static final long serialVersionUID = -6374194849341151499L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatStatisticsdataDeserializer() {
        this((Class<T>) XnatStatisticsdata.class);
    }

    protected XnatStatisticsdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "addfield":
                // TODO: Handle the "addfield" property here: java.util.List
                break;
            case "additionalstatistics":
                // TODO: Handle the "additionalstatistics" property here: java.util.List
                break;
            case "max":
                instance.setMax(parser.getDoubleValue());
                break;
            case "mean":
                instance.setMean(parser.getDoubleValue());
                break;
            case "min":
                instance.setMin(parser.getDoubleValue());
                break;
            case "noOfVoxels":
                instance.setNoOfVoxels(parser.getIntValue());
                break;
            case "snr":
                instance.setSnr(parser.getDoubleValue());
                break;
            case "stddev":
                instance.setStddev(parser.getDoubleValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

