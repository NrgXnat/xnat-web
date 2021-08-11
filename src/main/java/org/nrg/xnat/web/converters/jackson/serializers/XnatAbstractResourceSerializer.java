package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractresource;

import java.io.IOException;

@Slf4j
public abstract class XnatAbstractResourceSerializer<T extends XnatAbstractresource> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 7262248936715165602L;

    @SuppressWarnings("unchecked")
    public XnatAbstractResourceSerializer() {
        this((Class<T>) XnatAbstractresource.class);
    }

    protected XnatAbstractResourceSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T resource, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonNullNumber(generator, "fileCount", resource.getFileCount());
        writeNonBlankField(generator, "label", resource.getLabel());
        writeNonBlankField(generator, "format", resource.getFormat());
        writeNonBlankField(generator, "content", resource.getContent());
        writeNonNullNumber(generator, "xnatAbstractResourceId", resource.getXnatAbstractresourceId());
        writeNonBlankField(generator, "tags", resource.getTagString());
        writeNonNullField(generator, "fileSize", resource.getFileSize());
    }
}
