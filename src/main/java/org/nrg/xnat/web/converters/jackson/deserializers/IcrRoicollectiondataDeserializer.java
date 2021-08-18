package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.IcrRoicollectiondata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class IcrRoicollectiondataDeserializer<T extends IcrRoicollectiondata> extends XnatImageassessordataDeserializer<T> {
    private static final long serialVersionUID = 8715685888408948912L;

    @SuppressWarnings({"unchecked", "unused"})
    public IcrRoicollectiondataDeserializer() {
        this((Class<T>) IcrRoicollectiondata.class);
    }

    protected IcrRoicollectiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "collectiontype":
                instance.setCollectiontype(parser.getText());
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "references_seriesuid":
                // TODO: Handle the "references_seriesuid" property here: java.util.List
                break;
            case "subjectid":
                instance.setSubjectid(parser.getText());
                break;
            case "uid":
                instance.setUid(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

