package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatCtqcscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatCtqcscandataDeserializer<T extends XnatCtqcscandata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 6936100590573652113L;

    @SuppressWarnings("unchecked")
    public XnatCtqcscandataDeserializer() {
        this((Class<T>) XnatCtqcscandata.class);
    }

    public XnatCtqcscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
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

