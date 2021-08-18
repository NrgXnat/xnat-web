package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatVolumetricregion;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatVolumetricregionSerializer<T extends XnatVolumetricregion> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 7384670888819046737L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatVolumetricregionSerializer() {
        this((Class<T>) XnatVolumetricregion.class);
    }

    protected XnatVolumetricregionSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "hemisphere", instance.getHemisphere());
        writeNonBlankField(generator, "name", instance.getName());
        // TODO: Write out the "subregions_subregion" property here: java.util.List
        writeNonBlankField(generator, "units", instance.getUnits());
        writeNonNullNumber(generator, "voxels", instance.getVoxels());
        writeNonNullNumber(generator, "xnatVolumetricregionId", instance.getXnatVolumetricregionId());
    }
}

