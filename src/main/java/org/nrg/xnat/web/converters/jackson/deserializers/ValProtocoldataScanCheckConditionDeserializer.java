package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldataScanCheckCondition;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ValProtocoldataScanCheckConditionDeserializer<T extends ValProtocoldataScanCheckCondition> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -5785489394357743672L;

    @SuppressWarnings({"unchecked", "unused"})
    public ValProtocoldataScanCheckConditionDeserializer() {
        this((Class<T>) ValProtocoldataScanCheckCondition.class);
    }

    protected ValProtocoldataScanCheckConditionDeserializer(final Class<T> clazz) {
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
            case "valProtocoldataScanCheckConditionId":
                instance.setValProtocoldataScanCheckConditionId(parser.getIntValue());
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

