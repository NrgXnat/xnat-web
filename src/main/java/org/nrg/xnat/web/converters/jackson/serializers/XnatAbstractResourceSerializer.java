package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.security.UserI;

import java.io.IOException;

@Slf4j
public class XnatAbstractResourceSerializer extends AbstractBaseElementSerializer<XnatAbstractresource> {
    public XnatAbstractResourceSerializer() {
        super(XnatAbstractresource.class);
    }

    @Override
    protected void serializeImpl(final XnatAbstractresource resource, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
    	writeNonNullNumber(generator, "fileCount", resource.getFileCount());
        writeNonBlankField(generator, "label", resource.getLabel());
        writeNonBlankField(generator, "format", resource.getFormat());
        writeNonBlankField(generator, "content", resource.getContent());
        writeNonNullNumber(generator, "xnatAbstractResourceId", resource.getXnatAbstractresourceId());
        writeNonBlankField(generator, "tags", resource.getTagString());
        writeNonNullField(generator, "fileSize", resource.getFileSize());
        
       

        final UserI insertUser = resource.getInsertUser();
        if (insertUser != null) {
            writeNonBlankField(generator, "createdBy", insertUser.getUsername());
        }
    }
}
