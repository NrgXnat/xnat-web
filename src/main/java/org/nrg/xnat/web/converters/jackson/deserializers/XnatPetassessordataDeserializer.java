package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetassessordata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatPetassessordataDeserializer<T extends XnatPetassessordata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2893869223964036534L;

    @SuppressWarnings("unchecked")
    public XnatPetassessordataDeserializer() {
        this((Class<T>) XnatPetassessordata.class);
    }

    public XnatPetassessordataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "imageassessordata":
                // TODO: Handle the "imageassessordata" property here: org.nrg.xdat.om.XnatImageassessordata
                break;
            case "petSessionData":
                // TODO: Handle the "petSessionData" property here: org.nrg.xdat.om.XnatPetsessiondata
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

