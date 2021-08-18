package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatImageassessordata;

import java.io.IOException;

@Slf4j
public abstract class XnatImageassessordataDeserializer<T extends XnatImageassessordata> extends XnatDeriveddataDeserializer<T> {
    private static final long serialVersionUID = 2093349878306252288L;

    protected XnatImageassessordataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "imagesessionId":
                instance.setImagesessionId(parser.getText());
                break;
            case "in_file":
                // TODO: Handle the "in_file" property here: java.util.List
                break;
            case "out_file":
                // TODO: Handle the "out_file" property here: java.util.List
                break;
            case "parameters_addparam":
                // TODO: Handle the "parameters_addparam" property here: java.util.List
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

