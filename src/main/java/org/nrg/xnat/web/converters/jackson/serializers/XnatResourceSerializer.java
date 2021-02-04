package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.om.XnatResource;
import org.nrg.xdat.om.XnatResourcecatalog;

import java.io.IOException;

@Slf4j
public class XnatResourceSerializer extends AbstractBaseElementSerializer<XnatResource> {
    public XnatResourceSerializer() {
        super(XnatResource.class);
    }

    @Override
    protected void serializeImpl(final XnatResource resource, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
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
