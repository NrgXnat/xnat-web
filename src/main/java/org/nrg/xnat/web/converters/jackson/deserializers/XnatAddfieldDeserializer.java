package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAddfield;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatAddfieldDeserializer<T extends XnatAddfield> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 5089594519885655040L;

    @SuppressWarnings("unchecked")
    public XnatAddfieldDeserializer() {
        this((Class<T>) XnatAddfield.class);
    }

    public XnatAddfieldDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "addfield":
                // TODO: Handle the "addfield" property here: Object
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xnatAddfieldId":
                // TODO: Handle the "xnatAddfieldId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

