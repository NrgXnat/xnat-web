package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatResourceseries;

import java.io.IOException;

@Slf4j
public abstract class XnatResourceseriesDeserializer<T extends XnatResourceseries> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 4190523622669206366L;

    protected XnatResourceseriesDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "abstractresource":
                // TODO: Handle the "abstractresource" property here: org.nrg.xdat.om.XnatAbstractresource
                break;
            case "cachepath":
                // TODO: Handle the "cachepath" property here: String
                break;
            case "content":
                // TODO: Handle the "content" property here: String
                break;
            case "count":
                // TODO: Handle the "count" property here: Integer
                break;
            case "description":
                // TODO: Handle the "description" property here: String
                break;
            case "format":
                // TODO: Handle the "format" property here: String
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "path":
                // TODO: Handle the "path" property here: String
                break;
            case "pattern":
                // TODO: Handle the "pattern" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "unresolvedPaths":
                // TODO: Handle the "unresolvedPaths" property here: java.util.ArrayList
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

