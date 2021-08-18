package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStudyprotocolVariable;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatStudyprotocolVariableDeserializer<T extends XnatStudyprotocolVariable> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 3797283871460295800L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatStudyprotocolVariableDeserializer() {
        this((Class<T>) XnatStudyprotocolVariable.class);
    }

    protected XnatStudyprotocolVariableDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "id":
                instance.setId(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "xnatStudyprotocolVariableId":
                instance.setXnatStudyprotocolVariableId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

