package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatResourceSerializer<T extends XnatResource> extends XnatAbstractResourceSerializer<T> {
    private static final long serialVersionUID = -427794813436644000L;

    @SuppressWarnings("unchecked")
    public XnatResourceSerializer() {
        this((Class<T>) XnatResource.class);
    }

    protected XnatResourceSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final XnatResource resource, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(resource, generator, provider);
        writeNonNullNumber(generator, "fileCount", resource.getFileCount());
        writeNonBlankField(generator, "label", resource.getLabel());
        writeNonBlankField(generator, "format", resource.getFormat());
        writeNonBlankField(generator, "content", resource.getContent());
        writeNonBlankField(generator, "description", resource.getDescription());
        writeNonBlankField(generator, "note", resource.getNote());
        writeNonNullNumber(generator, "xnatAbstractResourceId", resource.getXnatAbstractresourceId());
        writeNonNullField(generator, "tags", resource.getTags_tag());
        writeNonNullField(generator, "fileSize", resource.getFileSize());
        writeNonNullField(generator, "resource", resource.getAbstractresource());
        writeNonNullField(generator, "files", resource.getCorrespondingFiles());
        writeNonNullField(generator, "uri", resource.getUri());
    }
}
