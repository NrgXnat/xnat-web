package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatContrastbolus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatContrastbolusDeserializer<T extends XnatContrastbolus> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 7550447834814006352L;

    @SuppressWarnings("unchecked")
    public XnatContrastbolusDeserializer() {
        this((Class<T>) XnatContrastbolus.class);
    }

    public XnatContrastbolusDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "activeingredient":
                // TODO: Handle the "activeingredient" property here: String
                break;
            case "agent":
                // TODO: Handle the "agent" property here: String
                break;
            case "concentration":
                // TODO: Handle the "concentration" property here: Double
                break;
            case "flowduration":
                // TODO: Handle the "flowduration" property here: Double
                break;
            case "flowrate":
                // TODO: Handle the "flowrate" property here: Double
                break;
            case "route":
                // TODO: Handle the "route" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "totaldose":
                // TODO: Handle the "totaldose" property here: Double
                break;
            case "volume":
                // TODO: Handle the "volume" property here: Double
                break;
            case "xnatContrastbolusId":
                // TODO: Handle the "xnatContrastbolusId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

