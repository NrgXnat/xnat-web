package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatCriteria;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatCriteriaDeserializer<T extends XdatCriteria> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -821567185803157144L;

    @SuppressWarnings("unchecked")
    public XdatCriteriaDeserializer() {
        this((Class<T>) XdatCriteria.class);
    }

    public XdatCriteriaDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "comparisonType":
                // TODO: Handle the "comparisonType" property here: String
                break;
            case "customSearch":
                // TODO: Handle the "customSearch" property here: String
                break;
            case "overrideValueFormatting":
                // TODO: Handle the "overrideValueFormatting" property here: Boolean
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "schemaField":
                // TODO: Handle the "schemaField" property here: String
                break;
            case "value":
                // TODO: Handle the "value" property here: String
                break;
            case "xdatCriteriaId":
                // TODO: Handle the "xdatCriteriaId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

