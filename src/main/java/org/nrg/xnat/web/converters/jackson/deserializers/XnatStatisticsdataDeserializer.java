package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStatisticsdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatStatisticsdataDeserializer<T extends XnatStatisticsdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -5981992353397891927L;

    @SuppressWarnings("unchecked")
    public XnatStatisticsdataDeserializer() {
        this((Class<T>) XnatStatisticsdata.class);
    }

    public XnatStatisticsdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "abstractstatistics":
                // TODO: Handle the "abstractstatistics" property here: org.nrg.xdat.om.XnatAbstractstatistics
                break;
            case "addfield":
                // TODO: Handle the "addfield" property here: java.util.List
                break;
            case "additionalstatistics":
                // TODO: Handle the "additionalstatistics" property here: java.util.List
                break;
            case "mean":
                // TODO: Handle the "mean" property here: Double
                break;
            case "noOfVoxels":
                // TODO: Handle the "noOfVoxels" property here: Integer
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "stddev":
                // TODO: Handle the "stddev" property here: Double
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

