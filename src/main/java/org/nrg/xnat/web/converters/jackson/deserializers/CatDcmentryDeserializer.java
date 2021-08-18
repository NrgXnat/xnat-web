package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatDcmentry;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class CatDcmentryDeserializer<T extends CatDcmentry> extends CatEntryDeserializer<T> {
    private static final long serialVersionUID = -3630665626183936680L;

    @SuppressWarnings({"unchecked", "unused"})
    public CatDcmentryDeserializer() {
        this((Class<T>) CatDcmentry.class);
    }

    protected CatDcmentryDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "instancenumber":
                instance.setInstancenumber(parser.getIntValue());
                break;
            case "uid":
                instance.setUid(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

