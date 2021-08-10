package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatProjectparticipant;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatProjectparticipantSerializer<T extends XnatProjectparticipant> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -8786707437345493974L;

    @SuppressWarnings("unchecked")
    public XnatProjectparticipantSerializer() {
        this((Class<T>) XnatProjectparticipant.class);
    }

    protected XnatProjectparticipantSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "group" property here: String
        // TODO: Write out the "label" property here: String
        // TODO: Write out the "project" property here: String
        // TODO: Write out the "projectData" property here: org.nrg.xdat.model.XnatProjectdataI
        // TODO: Write out the "projectDescription" property here: String
        // TODO: Write out the "projectDisplayID" property here: String
        // TODO: Write out the "projectName" property here: String
        // TODO: Write out the "projectSecondaryID" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "subjectId" property here: String
        // TODO: Write out the "xnatProjectparticipantId" property here: Integer
    }
}

