package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatRegionresource;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatRegionresourceDeserializer<T extends XnatRegionresource> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -6384721418363550725L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatRegionresourceDeserializer() {
        this((Class<T>) XnatRegionresource.class);
    }

    protected XnatRegionresourceDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "baseimage":
                // TODO: Handle the "baseimage" property here: org.nrg.xdat.model.XnatAbstractresourceI
                break;
            case "creator_firstname":
                instance.setCreator_firstname(parser.getText());
                break;
            case "creator_lastname":
                instance.setCreator_lastname(parser.getText());
                break;
            case "file":
                // TODO: Handle the "file" property here: org.nrg.xdat.model.XnatAbstractresourceI
                break;
            case "hemisphere":
                instance.setHemisphere(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "sessionId":
                instance.setSessionId(parser.getText());
                break;
            case "subregionlabels_label":
                // TODO: Handle the "subregionlabels_label" property here: java.util.List
                break;
            case "xnatRegionresourceId":
                instance.setXnatRegionresourceId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

