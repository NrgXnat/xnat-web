package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractresourceTag;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatAbstractresourceTagDeserializer<T extends XnatAbstractresourceTag> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 4960181203224859292L;

    @SuppressWarnings("unchecked")
    public XnatAbstractresourceTagDeserializer() {
        this((Class<T>) XnatAbstractresourceTag.class);
    }

    public XnatAbstractresourceTagDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xnatAbstractresourceTagId":
                // TODO: Handle the "xnatAbstractresourceTagId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

