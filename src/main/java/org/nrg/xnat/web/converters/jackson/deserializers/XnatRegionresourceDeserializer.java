package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatRegionresource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatRegionresourceDeserializer<T extends XnatRegionresource> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2594977814706062363L;

    @SuppressWarnings("unchecked")
    public XnatRegionresourceDeserializer() {
        this((Class<T>) XnatRegionresource.class);
    }

    public XnatRegionresourceDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "baseimage":
                // TODO: Handle the "baseimage" property here: org.nrg.xdat.model.XnatAbstractresourceI
                break;
            case "creator_firstname":
                // TODO: Handle the "creator_firstname" property here: String
                break;
            case "creator_lastname":
                // TODO: Handle the "creator_lastname" property here: String
                break;
            case "file":
                // TODO: Handle the "file" property here: org.nrg.xdat.om.XnatAbstractresource
                break;
            case "hemisphere":
                // TODO: Handle the "hemisphere" property here: String
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "sessionId":
                // TODO: Handle the "sessionId" property here: String
                break;
            case "subregionlabels_label":
                // TODO: Handle the "subregionlabels_label" property here: java.util.List
                break;
            case "xnatRegionresourceId":
                // TODO: Handle the "xnatRegionresourceId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

