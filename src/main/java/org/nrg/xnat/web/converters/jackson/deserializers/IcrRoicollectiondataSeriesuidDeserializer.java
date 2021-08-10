package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.IcrRoicollectiondataSeriesuid;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class IcrRoicollectiondataSeriesuidDeserializer<T extends IcrRoicollectiondataSeriesuid> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 681690407387274586L;

    @SuppressWarnings("unchecked")
    public IcrRoicollectiondataSeriesuidDeserializer() {
        this((Class<T>) IcrRoicollectiondataSeriesuid.class);
    }

    public IcrRoicollectiondataSeriesuidDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "icrRoicollectiondataSeriesuidId":
                // TODO: Handle the "icrRoicollectiondataSeriesuidId" property here: Integer
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "seriesuid":
                // TODO: Handle the "seriesuid" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

