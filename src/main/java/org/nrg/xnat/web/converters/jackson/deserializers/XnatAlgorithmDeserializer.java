package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAlgorithm;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatAlgorithmDeserializer<T extends XnatAlgorithm> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 377961451058435628L;

    @SuppressWarnings("unchecked")
    public XnatAlgorithmDeserializer() {
        this((Class<T>) XnatAlgorithm.class);
    }

    public XnatAlgorithmDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "family":
                // TODO: Handle the "family" property here: org.nrg.xdat.model.XnatDicomcodedvalueI
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "namecode":
                // TODO: Handle the "namecode" property here: org.nrg.xdat.model.XnatDicomcodedvalueI
                break;
            case "parameters":
                // TODO: Handle the "parameters" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "source":
                // TODO: Handle the "source" property here: String
                break;
            case "version":
                // TODO: Handle the "version" property here: String
                break;
            case "xnatAlgorithmId":
                // TODO: Handle the "xnatAlgorithmId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

