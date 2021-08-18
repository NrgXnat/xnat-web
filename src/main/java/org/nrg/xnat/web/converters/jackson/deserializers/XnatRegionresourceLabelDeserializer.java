package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatRegionresourceLabel;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatRegionresourceLabelDeserializer<T extends XnatRegionresourceLabel> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 3866575301863800908L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatRegionresourceLabelDeserializer() {
        this((Class<T>) XnatRegionresourceLabel.class);
    }

    protected XnatRegionresourceLabelDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "hemisphere":
                instance.setHemisphere(parser.getText());
                break;
            case "id":
                // TODO: Handle the "id" property here: Object
                break;
            case "label":
                instance.setLabel(parser.getText());
                break;
            case "xnatRegionresourceLabelId":
                instance.setXnatRegionresourceLabelId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

