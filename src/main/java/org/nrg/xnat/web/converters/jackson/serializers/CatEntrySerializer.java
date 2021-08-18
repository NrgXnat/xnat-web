package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatEntry;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class CatEntrySerializer<T extends CatEntry> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -598287755946568580L;

    @SuppressWarnings({"unchecked", "unused"})
    public CatEntrySerializer() {
        this((Class<T>) CatEntry.class);
    }

    protected CatEntrySerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "cachepath", instance.getCachepath());
        writeNonNullNumber(generator, "catEntryId", instance.getCatEntryId());
        writeNonBlankField(generator, "content", instance.getContent());
        writeNonBlankField(generator, "createdby", instance.getCreatedby());
        writeNonNullNumber(generator, "createdeventid", instance.getCreatedeventid());
        // TODO: Write out the "createdtime" property here: Object
        writeNonBlankField(generator, "description", instance.getDescription());
        writeNonBlankField(generator, "digest", instance.getDigest());
        writeNonBlankField(generator, "format", instance.getFormat());
        writeNonBlankField(generator, "id", instance.getId());
        // TODO: Write out the "metafields_metafield" property here: java.util.List
        writeNonBlankField(generator, "modifiedby", instance.getModifiedby());
        writeNonNullNumber(generator, "modifiedeventid", instance.getModifiedeventid());
        // TODO: Write out the "modifiedtime" property here: Object
        writeNonBlankField(generator, "name", instance.getName());
        // TODO: Write out the "tags_tag" property here: java.util.List
        writeNonBlankField(generator, "uri", instance.getUri());
    }
}

