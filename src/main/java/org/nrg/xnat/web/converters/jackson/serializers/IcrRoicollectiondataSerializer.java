package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.IcrRoicollectiondata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class IcrRoicollectiondataSerializer<T extends IcrRoicollectiondata> extends XnatImageassessordataSerializer<T> {
    private static final long serialVersionUID = -230240429727222810L;

    @SuppressWarnings({"unchecked", "unused"})
    public IcrRoicollectiondataSerializer() {
        this((Class<T>) IcrRoicollectiondata.class);
    }

    protected IcrRoicollectiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "collectiontype", instance.getCollectiontype());
        writeNonBlankField(generator, "description", instance.getDescription());
        writeNonBlankField(generator, "name", instance.getName());
        // TODO: Write out the "references_seriesuid" property here: java.util.List
        writeNonBlankField(generator, "subjectid", instance.getSubjectid());
        writeNonBlankField(generator, "uid", instance.getUid());
        super.serializeImpl(instance, generator, provider);
    }
}

