package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatCtqcscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatCtqcscandataDeserializer<T extends XnatCtqcscandata> extends XnatQcscandataDeserializer<T> {
    private static final long serialVersionUID = 8790485988090843076L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatCtqcscandataDeserializer() {
        this((Class<T>) XnatCtqcscandata.class);
    }

    protected XnatCtqcscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "ct":
                instance.setCt(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

