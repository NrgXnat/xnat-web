package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatImageassessordata;

import java.io.IOException;

@Slf4j
public abstract class XnatImageassessordataDeserializer<T extends XnatImageassessordata> extends XnatDeriveddataDeserializer<T> {
    private static final long serialVersionUID = 2749117185396878158L;

    protected XnatImageassessordataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "id":
                instance.setId(parser.getText());
                break;
            case "label":
                instance.setLabel(parser.getText());
                break;
            case "project":
                instance.setProject(parser.getText());
                break;
            case "note":
                instance.setNote(parser.getText());
                break;
            case "protocol":
                instance.setProtocol(parser.getText());
                break;
            case "original":
                instance.setOriginal(parser.getText());
                break;
            case "date":
                instance.setDate(parseDate(parser.getText()));
                break;
            case "delay":
                instance.setDelay(parser.getIntValue());
                break;
            case "version":
                instance.setVersion(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
