package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPublicationresource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatPublicationresourceDeserializer<T extends XnatPublicationresource> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2613006007730533091L;

    @SuppressWarnings("unchecked")
    public XnatPublicationresourceDeserializer() {
        this((Class<T>) XnatPublicationresource.class);
    }

    public XnatPublicationresourceDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "abstract":
                // TODO: Handle the "abstract" property here: String
                break;
            case "abstractresource":
                // TODO: Handle the "abstractresource" property here: org.nrg.xdat.om.XnatAbstractresource
                break;
            case "citation":
                // TODO: Handle the "citation" property here: String
                break;
            case "commentary":
                // TODO: Handle the "commentary" property here: String
                break;
            case "isprimary":
                // TODO: Handle the "isprimary" property here: Boolean
                break;
            case "label":
                // TODO: Handle the "label" property here: String
                break;
            case "medline":
                // TODO: Handle the "medline" property here: String
                break;
            case "other":
                // TODO: Handle the "other" property here: String
                break;
            case "pubmed":
                // TODO: Handle the "pubmed" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "title":
                // TODO: Handle the "title" property here: String
                break;
            case "type":
                // TODO: Handle the "type" property here: String
                break;
            case "unresolvedPaths":
                // TODO: Handle the "unresolvedPaths" property here: java.util.ArrayList
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

