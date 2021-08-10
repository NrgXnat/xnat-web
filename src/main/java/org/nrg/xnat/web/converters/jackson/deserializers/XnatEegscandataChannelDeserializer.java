package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEegscandataChannel;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatEegscandataChannelDeserializer<T extends XnatEegscandataChannel> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -7486402119272249219L;

    @SuppressWarnings("unchecked")
    public XnatEegscandataChannelDeserializer() {
        this((Class<T>) XnatEegscandataChannel.class);
    }

    public XnatEegscandataChannelDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "highcutoff":
                // TODO: Handle the "highcutoff" property here: String
                break;
            case "lowcutoff":
                // TODO: Handle the "lowcutoff" property here: String
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "notch":
                // TODO: Handle the "notch" property here: String
                break;
            case "resolution":
                // TODO: Handle the "resolution" property here: Double
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xnatEegscandataChannelId":
                // TODO: Handle the "xnatEegscandataChannelId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

