package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.IcrRoicollectiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class IcrRoicollectiondataSerializer<T extends IcrRoicollectiondata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 2087028224568776484L;

    @SuppressWarnings("unchecked")
    public IcrRoicollectiondataSerializer() {
        this((Class<T>) IcrRoicollectiondata.class);
    }

    protected IcrRoicollectiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "collectiontype" property here: String
        // TODO: Write out the "description" property here: String
        // TODO: Write out the "imageassessordata" property here: org.nrg.xdat.om.XnatImageassessordata
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "references_seriesuid" property here: java.util.List
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "subjectid" property here: String
    }
}

