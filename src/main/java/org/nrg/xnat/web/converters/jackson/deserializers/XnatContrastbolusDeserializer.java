package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatContrastbolus;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatContrastbolusDeserializer<T extends XnatContrastbolus> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 4371780905971215243L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatContrastbolusDeserializer() {
        this((Class<T>) XnatContrastbolus.class);
    }

    protected XnatContrastbolusDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "activeingredient":
                instance.setActiveingredient(parser.getText());
                break;
            case "agent":
                instance.setAgent(parser.getText());
                break;
            case "concentration":
                instance.setConcentration(parser.getDoubleValue());
                break;
            case "flowduration":
                instance.setFlowduration(parser.getDoubleValue());
                break;
            case "flowrate":
                instance.setFlowrate(parser.getDoubleValue());
                break;
            case "route":
                instance.setRoute(parser.getText());
                break;
            case "totaldose":
                instance.setTotaldose(parser.getDoubleValue());
                break;
            case "volume":
                instance.setVolume(parser.getDoubleValue());
                break;
            case "xnatContrastbolusId":
                instance.setXnatContrastbolusId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

