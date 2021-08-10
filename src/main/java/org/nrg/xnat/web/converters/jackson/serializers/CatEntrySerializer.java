package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatEntry;

import java.io.IOException;

@Slf4j
public abstract class CatEntrySerializer<T extends CatEntry> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 4768992620733464154L;

    @SuppressWarnings("unchecked")
    public CatEntrySerializer() {
        this((Class<T>) CatEntry.class);
    }

    protected CatEntrySerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "cachepath" property here: String
        // TODO: Write out the "catEntryId" property here: Integer
        // TODO: Write out the "content" property here: String
        // TODO: Write out the "createdby" property here: String
        // TODO: Write out the "createdeventid" property here: Integer
        // TODO: Write out the "createdtime" property here: Object
        // TODO: Write out the "description" property here: String
        // TODO: Write out the "digest" property here: String
        // TODO: Write out the "format" property here: String
        // TODO: Write out the "metafields_metafield" property here: java.util.List
        // TODO: Write out the "modifiedby" property here: String
        // TODO: Write out the "modifiedeventid" property here: Integer
        // TODO: Write out the "modifiedtime" property here: Object
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "tags_tag" property here: java.util.List
    }
}

