package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEegscandataChannel;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatEegscandataChannelDeserializer<T extends XnatEegscandataChannel> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2567433593866550742L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatEegscandataChannelDeserializer() {
        this((Class<T>) XnatEegscandataChannel.class);
    }

    protected XnatEegscandataChannelDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "highcutoff":
                instance.setHighcutoff(parser.getText());
                break;
            case "lowcutoff":
                instance.setLowcutoff(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "notch":
                instance.setNotch(parser.getText());
                break;
            case "resolution":
                instance.setResolution(parser.getDoubleValue());
                break;
            case "xnatEegscandataChannelId":
                instance.setXnatEegscandataChannelId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

