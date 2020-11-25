package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatResourcecatalog;

import java.io.IOException;

@Slf4j
public class XnatResourcecatalogSerializer extends AbstractBaseElementSerializer<XnatResourcecatalog> {
    public XnatResourcecatalogSerializer() {
        super(XnatResourcecatalog.class);
    }

    @Override
    protected void serializeImpl(final XnatResourcecatalog resource, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonNullNumber(generator, "fileCount", resource.getFileCount());
        writeNonBlankField(generator, "label", resource.getLabel());
        writeNonBlankField(generator, "format", resource.getFormat());
        writeNonBlankField(generator, "content", resource.getContent());
        writeNonNullNumber(generator, "xnatAbstractResourceId", resource.getXnatAbstractresourceId());
        writeNonBlankField(generator, "tags", resource.getTagString());
        writeNonNullField(generator, "fileSize", resource.getFileSize());
        writeNonNullField(generator, "resource", resource.getAbstractresource());
        writeNonNullField(generator, "files", resource.getCorrespondingFiles());
        writeNonNullField(generator, "uri", resource.getUri());
    }
}
