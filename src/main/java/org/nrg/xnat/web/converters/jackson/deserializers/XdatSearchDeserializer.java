package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatSearch;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatSearchDeserializer<T extends XdatSearch> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -8391285857313722096L;

    @SuppressWarnings("unchecked")
    public XdatSearchDeserializer() {
        this((Class<T>) XdatSearch.class);
    }

    public XdatSearchDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "page":
                // TODO: Handle the "page" property here: Integer
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "storedSearch":
                // TODO: Handle the "storedSearch" property here: org.nrg.xdat.om.XdatStoredSearchI
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

