package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatRtimagescandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatRtimagescandataDeserializer<T extends XnatRtimagescandata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 170740113172028903L;

    @SuppressWarnings("unchecked")
    public XnatRtimagescandataDeserializer() {
        this((Class<T>) XnatRtimagescandata.class);
    }

    public XnatRtimagescandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "imagescandata":
                // TODO: Handle the "imagescandata" property here: org.nrg.xdat.om.XnatImagescandata
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

