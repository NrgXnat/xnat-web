package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStudyprotocol;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatStudyprotocolDeserializer<T extends XnatStudyprotocol> extends XnatAbstractprotocolDeserializer<T> {
    private static final long serialVersionUID = 2538976743458570850L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatStudyprotocolDeserializer() {
        this((Class<T>) XnatStudyprotocol.class);
    }

    protected XnatStudyprotocolDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "acqconditions_condition":
                // TODO: Handle the "acqconditions_condition" property here: java.util.List
                break;
            case "imagesessiontypes_session":
                // TODO: Handle the "imagesessiontypes_session" property here: java.util.List
                break;
            case "subjectgroups_group":
                // TODO: Handle the "subjectgroups_group" property here: java.util.List
                break;
            case "subjectvariables_variable":
                // TODO: Handle the "subjectvariables_variable" property here: java.util.List
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

