package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectvariablesdataVariable;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatSubjectvariablesdataVariableDeserializer<T extends XnatSubjectvariablesdataVariable> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 6800315396514434625L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSubjectvariablesdataVariableDeserializer() {
        this((Class<T>) XnatSubjectvariablesdataVariable.class);
    }

    protected XnatSubjectvariablesdataVariableDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "name":
                instance.setName(parser.getText());
                break;
            case "variable":
                instance.setVariable(parser.getText());
                break;
            case "xnatSubjectvariablesdataVariableId":
                instance.setXnatSubjectvariablesdataVariableId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

