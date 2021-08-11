package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetscandataFrame;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatPetscandataFrameDeserializer<T extends XnatPetscandataFrame> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 979295650940050854L;

    @SuppressWarnings("unchecked")
    public XnatPetscandataFrameDeserializer() {
        this((Class<T>) XnatPetscandataFrame.class);
    }

    public XnatPetscandataFrameDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "length":
                // TODO: Handle the "length" property here: Double
                break;
            case "number":
                // TODO: Handle the "number" property here: Object
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "starttime":
                // TODO: Handle the "starttime" property here: Double
                break;
            case "units":
                // TODO: Handle the "units" property here: String
                break;
            case "xnatPetscandataFrameId":
                // TODO: Handle the "xnatPetscandataFrameId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

