package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDicomcodedvalue;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatDicomcodedvalueDeserializer<T extends XnatDicomcodedvalue> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -1073951386948362060L;

    @SuppressWarnings("unchecked")
    public XnatDicomcodedvalueDeserializer() {
        this((Class<T>) XnatDicomcodedvalue.class);
    }

    public XnatDicomcodedvalueDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "designator":
                // TODO: Handle the "designator" property here: String
                break;
            case "meaning":
                // TODO: Handle the "meaning" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "value":
                // TODO: Handle the "value" property here: String
                break;
            case "version":
                // TODO: Handle the "version" property here: String
                break;
            case "xnatDicomcodedvalueId":
                // TODO: Handle the "xnatDicomcodedvalueId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

