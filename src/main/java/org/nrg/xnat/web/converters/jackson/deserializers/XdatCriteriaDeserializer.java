package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatCriteria;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatCriteriaDeserializer<T extends XdatCriteria> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 659462844025728948L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatCriteriaDeserializer() {
        this((Class<T>) XdatCriteria.class);
    }

    protected XdatCriteriaDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "comparisonType":
                instance.setComparisonType(parser.getText());
                break;
            case "customSearch":
                instance.setCustomSearch(parser.getText());
                break;
            case "overrideValueFormatting":
                instance.setOverrideValueFormatting(parser.getBooleanValue());
                break;
            case "schemaField":
                instance.setSchemaField(parser.getText());
                break;
            case "value":
                instance.setValue(parser.getText());
                break;
            case "xdatCriteriaId":
                instance.setXdatCriteriaId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

