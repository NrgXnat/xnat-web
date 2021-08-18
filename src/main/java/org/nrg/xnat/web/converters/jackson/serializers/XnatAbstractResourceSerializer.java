package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractresource;

import java.io.IOException;

@Slf4j
public abstract class XnatAbstractResourceSerializer<T extends XnatAbstractresource> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -4233245037929255353L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatAbstractResourceSerializer() {
        this((Class<T>) XnatAbstractresource.class);
    }

    protected XnatAbstractResourceSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "fileCount", instance.getFileCount());
        // TODO: Write out the "fileSize" property here: Object
        writeNonBlankField(generator, "label", instance.getLabel());
        writeNonBlankField(generator, "note", instance.getNote());
        // TODO: Write out the "tags_tag" property here: java.util.List
        writeNonNullNumber(generator, "xnatAbstractresourceId", instance.getXnatAbstractresourceId());
    }
}

