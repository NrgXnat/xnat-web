package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectvariablesdata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatSubjectvariablesdataDeserializer<T extends XnatSubjectvariablesdata> extends XnatSubjectassessordataDeserializer<T> {
    private static final long serialVersionUID = -5218047536472296906L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSubjectvariablesdataDeserializer() {
        this((Class<T>) XnatSubjectvariablesdata.class);
    }

    protected XnatSubjectvariablesdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "variables_variable":
                // TODO: Handle the "variables_variable" property here: java.util.List
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

