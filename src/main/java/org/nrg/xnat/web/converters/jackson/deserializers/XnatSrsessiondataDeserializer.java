package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSrsessiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatSrsessiondataDeserializer<T extends XnatSrsessiondata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 8308072540872037578L;

    @SuppressWarnings("unchecked")
    public XnatSrsessiondataDeserializer() {
        this((Class<T>) XnatSrsessiondata.class);
    }

    public XnatSrsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "imagesessiondata":
                // TODO: Handle the "imagesessiondata" property here: org.nrg.xdat.om.XnatImagesessiondata
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

