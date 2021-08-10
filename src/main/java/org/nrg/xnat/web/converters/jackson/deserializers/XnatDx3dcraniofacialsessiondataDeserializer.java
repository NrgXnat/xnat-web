package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDx3dcraniofacialsessiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatDx3dcraniofacialsessiondataDeserializer<T extends XnatDx3dcraniofacialsessiondata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1549989845215527787L;

    @SuppressWarnings("unchecked")
    public XnatDx3dcraniofacialsessiondataDeserializer() {
        this((Class<T>) XnatDx3dcraniofacialsessiondata.class);
    }

    public XnatDx3dcraniofacialsessiondataDeserializer(final Class<T> clazz) {
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

