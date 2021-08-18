package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.IcrRoicollectiondataSeriesuid;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class IcrRoicollectiondataSeriesuidDeserializer<T extends IcrRoicollectiondataSeriesuid> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -1446109535714374292L;

    @SuppressWarnings({"unchecked", "unused"})
    public IcrRoicollectiondataSeriesuidDeserializer() {
        this((Class<T>) IcrRoicollectiondataSeriesuid.class);
    }

    protected IcrRoicollectiondataSeriesuidDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "icrRoicollectiondataSeriesuidId":
                instance.setIcrRoicollectiondataSeriesuidId(parser.getIntValue());
                break;
            case "seriesuid":
                instance.setSeriesuid(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

