package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatEntry;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class CatEntryDeserializer<T extends CatEntry> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -4014803643452142183L;

    @SuppressWarnings({"unchecked", "unused"})
    public CatEntryDeserializer() {
        this((Class<T>) CatEntry.class);
    }

    protected CatEntryDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "cachepath":
                instance.setCachepath(parser.getText());
                break;
            case "catEntryId":
                instance.setCatEntryId(parser.getIntValue());
                break;
            case "content":
                instance.setContent(parser.getText());
                break;
            case "createdby":
                instance.setCreatedby(parser.getText());
                break;
            case "createdeventid":
                instance.setCreatedeventid(parser.getIntValue());
                break;
            case "createdtime":
                // TODO: Handle the "createdtime" property here: Object
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "digest":
                instance.setDigest(parser.getText());
                break;
            case "format":
                instance.setFormat(parser.getText());
                break;
            case "id":
                instance.setId(parser.getText());
                break;
            case "metafields_metafield":
                // TODO: Handle the "metafields_metafield" property here: java.util.List
                break;
            case "modifiedby":
                instance.setModifiedby(parser.getText());
                break;
            case "modifiedeventid":
                instance.setModifiedeventid(parser.getIntValue());
                break;
            case "modifiedtime":
                // TODO: Handle the "modifiedtime" property here: Object
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "tags_tag":
                // TODO: Handle the "tags_tag" property here: java.util.List
                break;
            case "uri":
                instance.setUri(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

