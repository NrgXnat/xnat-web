package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatRegionresourceLabel;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatRegionresourceLabelDeserializer<T extends XnatRegionresourceLabel> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -8634297781262193564L;

    @SuppressWarnings("unchecked")
    public XnatRegionresourceLabelDeserializer() {
        this((Class<T>) XnatRegionresourceLabel.class);
    }

    public XnatRegionresourceLabelDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "hemisphere":
                // TODO: Handle the "hemisphere" property here: String
                break;
            case "label":
                // TODO: Handle the "label" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xnatRegionresourceLabelId":
                // TODO: Handle the "xnatRegionresourceLabelId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

