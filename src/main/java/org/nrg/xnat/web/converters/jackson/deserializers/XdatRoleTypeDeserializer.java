package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatRoleType;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatRoleTypeDeserializer<T extends XdatRoleType> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1561234778886578504L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatRoleTypeDeserializer() {
        this((Class<T>) XdatRoleType.class);
    }

    protected XdatRoleTypeDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "allowedActions_allowedAction":
                // TODO: Handle the "allowedActions_allowedAction" property here: java.util.ArrayList
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "roleName":
                instance.setRoleName(parser.getText());
                break;
            case "sequence":
                instance.setSequence(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

