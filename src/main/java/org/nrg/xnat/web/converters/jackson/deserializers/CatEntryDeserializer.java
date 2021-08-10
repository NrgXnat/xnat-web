package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatEntry;

import java.io.IOException;

@Slf4j
public abstract class CatEntryDeserializer<T extends CatEntry> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 4819360655425440410L;

    protected CatEntryDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "cachepath":
                // TODO: Handle the "cachepath" property here: String
                break;
            case "catEntryId":
                // TODO: Handle the "catEntryId" property here: Integer
                break;
            case "content":
                // TODO: Handle the "content" property here: String
                break;
            case "createdby":
                // TODO: Handle the "createdby" property here: String
                break;
            case "createdeventid":
                // TODO: Handle the "createdeventid" property here: Integer
                break;
            case "createdtime":
                // TODO: Handle the "createdtime" property here: Object
                break;
            case "description":
                // TODO: Handle the "description" property here: String
                break;
            case "digest":
                // TODO: Handle the "digest" property here: String
                break;
            case "format":
                // TODO: Handle the "format" property here: String
                break;
            case "metafields_metafield":
                // TODO: Handle the "metafields_metafield" property here: java.util.List
                break;
            case "modifiedby":
                // TODO: Handle the "modifiedby" property here: String
                break;
            case "modifiedeventid":
                // TODO: Handle the "modifiedeventid" property here: Integer
                break;
            case "modifiedtime":
                // TODO: Handle the "modifiedtime" property here: Object
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "tags_tag":
                // TODO: Handle the "tags_tag" property here: java.util.List
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

