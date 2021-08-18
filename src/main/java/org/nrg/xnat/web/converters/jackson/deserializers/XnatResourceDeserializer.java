package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatResource;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatResourceDeserializer<T extends XnatResource> extends XnatAbstractresourceDeserializer<T> {
    private static final long serialVersionUID = -5896021405676432843L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatResourceDeserializer() {
        this((Class<T>) XnatResource.class);
    }

    protected XnatResourceDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "cachepath":
                instance.setCachepath(parser.getText());
                break;
            case "content":
                instance.setContent(parser.getText());
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "format":
                instance.setFormat(parser.getText());
                break;
            case "provenance":
                // TODO: Handle the "provenance" property here: org.nrg.xdat.model.ProvProcessI
                break;
            case "uri":
                instance.setUri(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

