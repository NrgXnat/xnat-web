package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDicomseries;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatDicomseriesSerializer<T extends XnatDicomseries> extends XnatAbstractResourceSerializer<T> {
    private static final long serialVersionUID = -6763127352367888535L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatDicomseriesSerializer() {
        this((Class<T>) XnatDicomseries.class);
    }

    protected XnatDicomseriesSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "cachepath", instance.getCachepath());
        writeNonBlankField(generator, "content", instance.getContent());
        writeNonBlankField(generator, "description", instance.getDescription());
        writeNonNullNumber(generator, "dimensions_volumes", instance.getDimensions_volumes());
        writeNonNullNumber(generator, "dimensions_x", instance.getDimensions_x());
        writeNonNullNumber(generator, "dimensions_y", instance.getDimensions_y());
        writeNonNullNumber(generator, "dimensions_z", instance.getDimensions_z());
        writeNonBlankField(generator, "format", instance.getFormat());
        // TODO: Write out the "imageset_image" property here: java.util.List
        writeNonBlankField(generator, "orientation", instance.getOrientation());
        writeNonBlankField(generator, "uid", instance.getUid());
        writeNonBlankField(generator, "voxelres_units", instance.getVoxelres_units());
        writeNonNullNumber(generator, "voxelres_x", instance.getVoxelres_x());
        writeNonNullNumber(generator, "voxelres_y", instance.getVoxelres_y());
        writeNonNullNumber(generator, "voxelres_z", instance.getVoxelres_z());
        super.serializeImpl(instance, generator, provider);
    }
}

