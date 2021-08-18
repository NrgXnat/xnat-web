package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldataCondition;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ValProtocoldataConditionDeserializer<T extends ValProtocoldataCondition> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 3080641546055036222L;

    @SuppressWarnings({"unchecked", "unused"})
    public ValProtocoldataConditionDeserializer() {
        this((Class<T>) ValProtocoldataCondition.class);
    }

    protected ValProtocoldataConditionDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "diagnosis":
                instance.setDiagnosis(parser.getText());
                break;
            case "id":
                instance.setId(parser.getText());
                break;
            case "status":
                instance.setStatus(parser.getText());
                break;
            case "valProtocoldataConditionId":
                instance.setValProtocoldataConditionId(parser.getIntValue());
                break;
            case "verified":
                instance.setVerified(parser.getText());
                break;
            case "xmlpath":
                instance.setXmlpath(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

