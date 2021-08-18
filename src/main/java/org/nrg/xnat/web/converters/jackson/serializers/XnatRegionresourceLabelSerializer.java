package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatRegionresourceLabel;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatRegionresourceLabelSerializer<T extends XnatRegionresourceLabel> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -3901434119594396892L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatRegionresourceLabelSerializer() {
        this((Class<T>) XnatRegionresourceLabel.class);
    }

    protected XnatRegionresourceLabelSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "hemisphere", instance.getHemisphere());
        // TODO: Write out the "id" property here: Object
        writeNonBlankField(generator, "label", instance.getLabel());
        writeNonNullNumber(generator, "xnatRegionresourceLabelId", instance.getXnatRegionresourceLabelId());
    }
}

