package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetqcscandataProcessingerror;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatPetqcscandataProcessingerrorDeserializer<T extends XnatPetqcscandataProcessingerror> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -4374805256040811096L;

    @SuppressWarnings("unchecked")
    public XnatPetqcscandataProcessingerrorDeserializer() {
        this((Class<T>) XnatPetqcscandataProcessingerror.class);
    }

    public XnatPetqcscandataProcessingerrorDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "processingerror":
                // TODO: Handle the "processingerror" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xnatPetqcscandataProcessingerrorId":
                // TODO: Handle the "xnatPetqcscandataProcessingerrorId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

