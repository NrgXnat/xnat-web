package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStatisticsdataAdditionalstatistics;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatStatisticsdataAdditionalstatisticsDeserializer<T extends XnatStatisticsdataAdditionalstatistics> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 8389710894420269767L;

    @SuppressWarnings("unchecked")
    public XnatStatisticsdataAdditionalstatisticsDeserializer() {
        this((Class<T>) XnatStatisticsdataAdditionalstatistics.class);
    }

    public XnatStatisticsdataAdditionalstatisticsDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "additionalstatistics":
                // TODO: Handle the "additionalstatistics" property here: Double
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xnatStatisticsdataAdditionalstatisticsId":
                // TODO: Handle the "xnatStatisticsdataAdditionalstatisticsId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

