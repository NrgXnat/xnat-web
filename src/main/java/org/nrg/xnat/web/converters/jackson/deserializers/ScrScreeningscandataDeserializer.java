package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ScrScreeningscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ScrScreeningscandataDeserializer<T extends ScrScreeningscandata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -5020399155301647042L;

    @SuppressWarnings("unchecked")
    public ScrScreeningscandataDeserializer() {
        this((Class<T>) ScrScreeningscandata.class);
    }

    public ScrScreeningscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "comments":
                // TODO: Handle the "comments" property here: String
                break;
            case "imagescanId":
                // TODO: Handle the "imagescanId" property here: String
                break;
            case "pass":
                // TODO: Handle the "pass" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "scrScreeningscandataId":
                // TODO: Handle the "scrScreeningscandataId" property here: Integer
                break;
            case "summary":
                // TODO: Handle the "summary" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

