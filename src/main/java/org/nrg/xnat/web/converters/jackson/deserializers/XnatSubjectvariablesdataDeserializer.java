package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectvariablesdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatSubjectvariablesdataDeserializer<T extends XnatSubjectvariablesdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 7272193982485819075L;

    @SuppressWarnings("unchecked")
    public XnatSubjectvariablesdataDeserializer() {
        this((Class<T>) XnatSubjectvariablesdata.class);
    }

    public XnatSubjectvariablesdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "subjectassessordata":
                // TODO: Handle the "subjectassessordata" property here: org.nrg.xdat.om.XnatSubjectassessordata
                break;
            case "variables_variable":
                // TODO: Handle the "variables_variable" property here: java.util.List
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

