package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatRegionresource;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatRegionresourceSerializer<T extends XnatRegionresource> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -8286945782773125591L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatRegionresourceSerializer() {
        this((Class<T>) XnatRegionresource.class);
    }

    protected XnatRegionresourceSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "baseimage" property here: org.nrg.xdat.model.XnatAbstractresourceI
    	writeNonNullField(generator, "baseImage", instance.getBaseimage());
        writeNonBlankField(generator, "creatorFirstName", instance.getCreator_firstname());
        writeNonBlankField(generator, "creatorLastName", instance.getCreator_lastname());
        // TODO: Write out the "file" property here: org.nrg.xdat.model.XnatAbstractresourceI
        writeNonNullField(generator, "file", instance.getFile());
        writeNonBlankField(generator, "hemisphere", instance.getHemisphere());
        writeNonBlankField(generator, "name", instance.getName());
        writeNonBlankField(generator, "sessionId", instance.getSessionId());
        // TODO: Write out the "subregionlabels_label" property here: java.util.List
        writeNonNullField(generator, "subregionLabels", instance.getSubregionlabels_label());
        writeNonNullNumber(generator, "xnatRegionresourceId", instance.getXnatRegionresourceId());
    }
}

