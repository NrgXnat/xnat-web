package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStudyprotocolGroup;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatStudyprotocolGroupDeserializer<T extends XnatStudyprotocolGroup> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -7993510485067847115L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatStudyprotocolGroupDeserializer() {
        this((Class<T>) XnatStudyprotocolGroup.class);
    }

    protected XnatStudyprotocolGroupDeserializer(final Class<T> clazz) {
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
            case "xnatStudyprotocolGroupId":
                instance.setXnatStudyprotocolGroupId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

