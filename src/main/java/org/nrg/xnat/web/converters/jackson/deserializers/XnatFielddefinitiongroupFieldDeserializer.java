package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatFielddefinitiongroupField;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatFielddefinitiongroupFieldDeserializer<T extends XnatFielddefinitiongroupField> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -7545726840188669493L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatFielddefinitiongroupFieldDeserializer() {
        this((Class<T>) XnatFielddefinitiongroupField.class);
    }

    protected XnatFielddefinitiongroupFieldDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "datatype":
                instance.setDatatype(parser.getText());
                break;
            case "group":
                instance.setGroup(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "possiblevalues_possiblevalue":
                // TODO: Handle the "possiblevalues_possiblevalue" property here: java.util.List
                break;
            case "required":
                instance.setRequired(parser.getBooleanValue());
                break;
            case "sequence":
                instance.setSequence(parser.getIntValue());
                break;
            case "type":
                instance.setType(parser.getText());
                break;
            case "xmlpath":
                instance.setXmlpath(parser.getText());
                break;
            case "xnatFielddefinitiongroupFieldId":
                instance.setXnatFielddefinitiongroupFieldId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

