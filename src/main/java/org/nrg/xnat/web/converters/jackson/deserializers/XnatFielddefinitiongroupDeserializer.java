package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatFielddefinitiongroup;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatFielddefinitiongroupDeserializer<T extends XnatFielddefinitiongroup> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 6255046688760205608L;

    @SuppressWarnings("unchecked")
    public XnatFielddefinitiongroupDeserializer() {
        this((Class<T>) XnatFielddefinitiongroup.class);
    }

    public XnatFielddefinitiongroupDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "dataType":
                // TODO: Handle the "dataType" property here: String
                break;
            case "description":
                // TODO: Handle the "description" property here: String
                break;
            case "fields_field":
                // TODO: Handle the "fields_field" property here: java.util.List
                break;
            case "projectSpecific":
                // TODO: Handle the "projectSpecific" property here: Boolean
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "shareable":
                // TODO: Handle the "shareable" property here: Boolean
                break;
            case "xnatFielddefinitiongroupId":
                // TODO: Handle the "xnatFielddefinitiongroupId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

