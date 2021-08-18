package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractresourceTag;

import java.io.IOException;

@Slf4j
public abstract class XnatAbstractresourceTagDeserializer<T extends XnatAbstractresourceTag> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -1744409420843743818L;

    protected XnatAbstractresourceTagDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "name":
                instance.setName(parser.getText());
                break;
            case "tag":
                instance.setTag(parser.getText());
                break;
            case "xnatAbstractresourceTagId":
                instance.setXnatAbstractresourceTagId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

