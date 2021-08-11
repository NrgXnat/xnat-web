package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectmetadata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatSubjectmetadataDeserializer<T extends XnatSubjectmetadata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -549073060644868809L;

    @SuppressWarnings("unchecked")
    public XnatSubjectmetadataDeserializer() {
        this((Class<T>) XnatSubjectmetadata.class);
    }

    public XnatSubjectmetadataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "abstractsubjectmetadata":
                // TODO: Handle the "abstractsubjectmetadata" property here: org.nrg.xdat.om.XnatAbstractsubjectmetadata
                break;
            case "cohort":
                // TODO: Handle the "cohort" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

