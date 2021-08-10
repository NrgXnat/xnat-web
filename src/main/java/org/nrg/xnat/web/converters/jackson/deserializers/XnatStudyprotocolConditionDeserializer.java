package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStudyprotocolCondition;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatStudyprotocolConditionDeserializer<T extends XnatStudyprotocolCondition> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 13448782788469024L;

    @SuppressWarnings("unchecked")
    public XnatStudyprotocolConditionDeserializer() {
        this((Class<T>) XnatStudyprotocolCondition.class);
    }

    public XnatStudyprotocolConditionDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "description":
                // TODO: Handle the "description" property here: String
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xnatStudyprotocolConditionId":
                // TODO: Handle the "xnatStudyprotocolConditionId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

