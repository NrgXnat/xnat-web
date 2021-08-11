package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatProjectparticipant;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatProjectparticipantDeserializer<T extends XnatProjectparticipant> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 2113493703708157564L;

    @SuppressWarnings("unchecked")
    public XnatProjectparticipantDeserializer() {
        this((Class<T>) XnatProjectparticipant.class);
    }

    public XnatProjectparticipantDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "group":
                // TODO: Handle the "group" property here: String
                break;
            case "label":
                // TODO: Handle the "label" property here: String
                break;
            case "project":
                // TODO: Handle the "project" property here: String
                break;
            case "projectData":
                // TODO: Handle the "projectData" property here: org.nrg.xdat.model.XnatProjectdataI
                break;
            case "projectDescription":
                // TODO: Handle the "projectDescription" property here: String
                break;
            case "projectDisplayID":
                // TODO: Handle the "projectDisplayID" property here: String
                break;
            case "projectName":
                // TODO: Handle the "projectName" property here: String
                break;
            case "projectSecondaryID":
                // TODO: Handle the "projectSecondaryID" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "subjectId":
                // TODO: Handle the "subjectId" property here: String
                break;
            case "xnatProjectparticipantId":
                // TODO: Handle the "xnatProjectparticipantId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

