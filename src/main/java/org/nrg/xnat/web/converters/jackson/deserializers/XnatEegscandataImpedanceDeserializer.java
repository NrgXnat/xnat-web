package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEegscandataImpedance;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatEegscandataImpedanceDeserializer<T extends XnatEegscandataImpedance> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 4688828987313821374L;

    @SuppressWarnings("unchecked")
    public XnatEegscandataImpedanceDeserializer() {
        this((Class<T>) XnatEegscandataImpedance.class);
    }

    public XnatEegscandataImpedanceDeserializer(final Class<T> clazz) {
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
            case "value":
                // TODO: Handle the "value" property here: String
                break;
            case "xnatEegscandataImpedanceId":
                // TODO: Handle the "xnatEegscandataImpedanceId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

