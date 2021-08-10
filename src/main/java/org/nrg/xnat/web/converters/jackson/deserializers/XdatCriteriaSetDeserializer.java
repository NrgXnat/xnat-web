package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatCriteriaSet;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatCriteriaSetDeserializer<T extends XdatCriteriaSet> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 8983065949905735862L;

    @SuppressWarnings("unchecked")
    public XdatCriteriaSetDeserializer() {
        this((Class<T>) XdatCriteriaSet.class);
    }

    public XdatCriteriaSetDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "childSet":
                // TODO: Handle the "childSet" property here: java.util.ArrayList
                break;
            case "criteria":
                // TODO: Handle the "criteria" property here: java.util.ArrayList
                break;
            case "method":
                // TODO: Handle the "method" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xdatCriteriaSetId":
                // TODO: Handle the "xdatCriteriaSetId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

