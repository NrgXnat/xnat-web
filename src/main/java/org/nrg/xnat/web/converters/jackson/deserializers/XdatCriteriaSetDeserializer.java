package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatCriteriaSet;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatCriteriaSetDeserializer<T extends XdatCriteriaSet> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -4719713787940232032L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatCriteriaSetDeserializer() {
        this((Class<T>) XdatCriteriaSet.class);
    }

    protected XdatCriteriaSetDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "childSet":
                // TODO: Handle the "childSet" property here: java.util.ArrayList
                break;
            case "criteria":
                // TODO: Handle the "criteria" property here: java.util.ArrayList
                break;
            case "method":
                instance.setMethod(parser.getText());
                break;
            case "xdatCriteriaSetId":
                instance.setXdatCriteriaSetId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

