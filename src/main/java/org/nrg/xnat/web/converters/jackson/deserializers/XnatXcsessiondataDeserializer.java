package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatXcsessiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatXcsessiondataDeserializer<T extends XnatXcsessiondata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2693033580893933635L;

    @SuppressWarnings("unchecked")
    public XnatXcsessiondataDeserializer() {
        this((Class<T>) XnatXcsessiondata.class);
    }

    public XnatXcsessiondataDeserializer(final Class<T> clazz) {
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

