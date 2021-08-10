package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatOtherqcscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatOtherqcscandataDeserializer<T extends XnatOtherqcscandata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 3367773591718567874L;

    @SuppressWarnings("unchecked")
    public XnatOtherqcscandataDeserializer() {
        this((Class<T>) XnatOtherqcscandata.class);
    }

    public XnatOtherqcscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "other":
                // TODO: Handle the "other" property here: String
                break;
            case "qcscandata":
                // TODO: Handle the "qcscandata" property here: org.nrg.xdat.om.XnatQcscandata
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

