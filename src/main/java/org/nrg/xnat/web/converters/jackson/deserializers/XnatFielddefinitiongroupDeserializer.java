package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.model.XnatFielddefinitiongroupFieldI;
import org.nrg.xdat.om.XnatExperimentdataField;
import org.nrg.xdat.om.XnatFielddefinitiongroup;
import org.nrg.xdat.om.XnatFielddefinitiongroupField;

import java.io.IOException;
import java.util.Map;

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
            case "fields":
                // TODO: Handle the "fields_field" property here: java.util.List
            	final Map<String, String> fields = parser.readValueAs(MAP_STRING_STRING);
                fields.forEach((key, value) -> {
                    final XnatFielddefinitiongroupField definitiongroupField = new XnatFielddefinitiongroupField();
                    definitiongroupField.setName(key);
                   // definitiongroupField.setField(value);
                    try {
                        instance.setFields_field(definitiongroupField);
                    } catch (Exception e) {
                        log.error("Tried to set a field on an experiment with name {} and field {} but failed", key, value, e);
                    }
                });
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

