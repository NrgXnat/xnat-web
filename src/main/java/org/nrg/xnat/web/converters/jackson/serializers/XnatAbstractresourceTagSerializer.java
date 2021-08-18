package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractresourceTag;

import java.io.IOException;

@Slf4j
public abstract class XnatAbstractresourceTagSerializer<T extends XnatAbstractresourceTag> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 3787202951991668353L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatAbstractresourceTagSerializer() {
        this((Class<T>) XnatAbstractresourceTag.class);
    }

    protected XnatAbstractresourceTagSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "name", instance.getName());
        writeNonBlankField(generator, "tag", instance.getTag());
        writeNonNullNumber(generator, "xnatAbstractresourceTagId", instance.getXnatAbstractresourceTagId());
    }
}

