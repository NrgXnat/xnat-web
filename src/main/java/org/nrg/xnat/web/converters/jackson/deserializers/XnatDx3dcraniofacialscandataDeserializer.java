package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDx3dcraniofacialscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatDx3dcraniofacialscandataDeserializer<T extends XnatDx3dcraniofacialscandata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -4324909805245663327L;

    @SuppressWarnings("unchecked")
    public XnatDx3dcraniofacialscandataDeserializer() {
        this((Class<T>) XnatDx3dcraniofacialscandata.class);
    }

    public XnatDx3dcraniofacialscandataDeserializer(final Class<T> clazz) {
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

