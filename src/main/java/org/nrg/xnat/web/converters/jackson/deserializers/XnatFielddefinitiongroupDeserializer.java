package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatFielddefinitiongroup;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatFielddefinitiongroupDeserializer<T extends XnatFielddefinitiongroup> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 101061338839874876L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatFielddefinitiongroupDeserializer() {
        this((Class<T>) XnatFielddefinitiongroup.class);
    }

    protected XnatFielddefinitiongroupDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "dataType":
                instance.setDataType(parser.getText());
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "fields_field":
                // TODO: Handle the "fields_field" property here: java.util.List
                break;
            case "id":
                instance.setId(parser.getText());
                break;
            case "projectSpecific":
                instance.setProjectSpecific(parser.getBooleanValue());
                break;
            case "shareable":
                instance.setShareable(parser.getBooleanValue());
                break;
            case "xnatFielddefinitiongroupId":
                instance.setXnatFielddefinitiongroupId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

