package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.IcrRoicollectiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class IcrRoicollectiondataDeserializer<T extends IcrRoicollectiondata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 2331230546280414702L;

    @SuppressWarnings("unchecked")
    public IcrRoicollectiondataDeserializer() {
        this((Class<T>) IcrRoicollectiondata.class);
    }

    public IcrRoicollectiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "collectiontype":
                // TODO: Handle the "collectiontype" property here: String
                break;
            case "description":
                // TODO: Handle the "description" property here: String
                break;
            case "imageassessordata":
                // TODO: Handle the "imageassessordata" property here: org.nrg.xdat.om.XnatImageassessordata
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "references_seriesuid":
                // TODO: Handle the "references_seriesuid" property here: java.util.List
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "subjectid":
                // TODO: Handle the "subjectid" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

