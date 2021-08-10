package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDicomseriesImage;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatDicomseriesImageDeserializer<T extends XnatDicomseriesImage> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 9198378698160654118L;

    @SuppressWarnings("unchecked")
    public XnatDicomseriesImageDeserializer() {
        this((Class<T>) XnatDicomseriesImage.class);
    }

    public XnatDicomseriesImageDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "instanceNumber":
                // TODO: Handle the "instanceNumber" property here: Integer
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "sopInstanceUid":
                // TODO: Handle the "sopInstanceUid" property here: String
                break;
            case "xnatDicomseriesImageId":
                // TODO: Handle the "xnatDicomseriesImageId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

