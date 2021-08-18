package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatProjectparticipant;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatProjectparticipantDeserializer<T extends XnatProjectparticipant> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -8963361587975047800L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatProjectparticipantDeserializer() {
        this((Class<T>) XnatProjectparticipant.class);
    }

    protected XnatProjectparticipantDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "group":
                instance.setGroup(parser.getText());
                break;
            case "label":
                instance.setLabel(parser.getText());
                break;
            case "project":
                instance.setProject(parser.getText());
                break;
            case "subjectId":
                instance.setSubjectId(parser.getText());
                break;
            case "xnatProjectparticipantId":
                instance.setXnatProjectparticipantId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

