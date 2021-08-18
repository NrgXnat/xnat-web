package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatOtherqcscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatOtherqcscandataDeserializer<T extends XnatOtherqcscandata> extends XnatQcscandataDeserializer<T> {
    private static final long serialVersionUID = 8030185264767426425L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatOtherqcscandataDeserializer() {
        this((Class<T>) XnatOtherqcscandata.class);
    }

    protected XnatOtherqcscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "other":
                instance.setOther(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

