package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStudyprotocolCondition;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatStudyprotocolConditionDeserializer<T extends XnatStudyprotocolCondition> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -3200225143427234970L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatStudyprotocolConditionDeserializer() {
        this((Class<T>) XnatStudyprotocolCondition.class);
    }

    protected XnatStudyprotocolConditionDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "id":
                instance.setId(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "xnatStudyprotocolConditionId":
                instance.setXnatStudyprotocolConditionId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

