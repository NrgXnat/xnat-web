package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStatisticsdataAdditionalstatistics;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatStatisticsdataAdditionalstatisticsDeserializer<T extends XnatStatisticsdataAdditionalstatistics> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -1396891189228034037L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatStatisticsdataAdditionalstatisticsDeserializer() {
        this((Class<T>) XnatStatisticsdataAdditionalstatistics.class);
    }

    protected XnatStatisticsdataAdditionalstatisticsDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "additionalstatistics":
                instance.setAdditionalstatistics(parser.getDoubleValue());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "xnatStatisticsdataAdditionalstatisticsId":
                instance.setXnatStatisticsdataAdditionalstatisticsId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

