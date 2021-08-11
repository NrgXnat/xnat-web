package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatResourcecatalogSerializer<T extends XnatResourcecatalog> extends XnatResourceSerializer<T> {
    private static final long serialVersionUID = -6692826681422182740L;

    @SuppressWarnings("unchecked")
    public XnatResourcecatalogSerializer() {
        this((Class<T>) XnatResourcecatalog.class);
    }

    protected XnatResourcecatalogSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T resource, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(resource, generator, provider);
        writeNonNullNumber(generator, "fileCount", resource.getFileCount());
        writeNonBlankField(generator, "label", resource.getLabel());
        writeNonBlankField(generator, "format", resource.getFormat());
        writeNonBlankField(generator, "content", resource.getContent());
        writeNonBlankField(generator, "description", resource.getDescription());
        writeNonBlankField(generator, "note", resource.getNote());
        writeNonNullNumber(generator, "xnatAbstractResourceId", resource.getXnatAbstractresourceId());
        writeNonBlankField(generator, "tags", resource.getTagString());
        writeNonNullField(generator, "fileSize", resource.getFileSize());
        writeNonNullField(generator, "resource", resource.getAbstractresource());
        writeNonNullField(generator, "files", resource.getCorrespondingFiles());
        writeNonNullField(generator, "uri", resource.getUri());
    }
}
