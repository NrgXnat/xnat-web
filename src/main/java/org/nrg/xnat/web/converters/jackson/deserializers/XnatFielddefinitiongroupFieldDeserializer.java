package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatFielddefinitiongroupField;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatFielddefinitiongroupFieldDeserializer<T extends XnatFielddefinitiongroupField> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -9068897545679996759L;

    @SuppressWarnings("unchecked")
    public XnatFielddefinitiongroupFieldDeserializer() {
        this((Class<T>) XnatFielddefinitiongroupField.class);
    }

    public XnatFielddefinitiongroupFieldDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "cleanedXMLPath":
                // TODO: Handle the "cleanedXMLPath" property here: String
                break;
            case "datatype":
                // TODO: Handle the "datatype" property here: String
                break;
            case "group":
                // TODO: Handle the "group" property here: String
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "possiblevalues_possiblevalue":
                // TODO: Handle the "possiblevalues_possiblevalue" property here: java.util.List
                break;
            case "required":
                // TODO: Handle the "required" property here: Boolean
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "sequence":
                // TODO: Handle the "sequence" property here: Integer
                break;
            case "type":
                // TODO: Handle the "type" property here: String
                break;
            case "xmlpath":
                // TODO: Handle the "xmlpath" property here: String
                break;
            case "xnatFielddefinitiongroupFieldId":
                // TODO: Handle the "xnatFielddefinitiongroupFieldId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

