package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectmetadata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatSubjectmetadataDeserializer<T extends XnatSubjectmetadata> extends XnatAbstractsubjectmetadataDeserializer<T> {
    private static final long serialVersionUID = -5786162254756362493L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSubjectmetadataDeserializer() {
        this((Class<T>) XnatSubjectmetadata.class);
    }

    protected XnatSubjectmetadataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "cohort":
                instance.setCohort(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

