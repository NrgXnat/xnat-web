package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatResourceseries;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatResourceseriesDeserializer<T extends XnatResourceseries> extends XnatAbstractResourceDeserializer<T> {
    private static final long serialVersionUID = 7267609873090380586L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatResourceseriesDeserializer() {
        this((Class<T>) XnatResourceseries.class);
    }

    protected XnatResourceseriesDeserializer(final Class<T> clazz) {
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
            case "count":
                instance.setCount(parser.getIntValue());
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "format":
                instance.setFormat(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "path":
                instance.setPath(parser.getText());
                break;
            case "pattern":
                instance.setPattern(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

