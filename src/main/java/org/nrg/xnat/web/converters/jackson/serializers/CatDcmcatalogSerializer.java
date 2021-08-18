package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatDcmcatalog;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class CatDcmcatalogSerializer<T extends CatDcmcatalog> extends CatCatalogSerializer<T> {
    private static final long serialVersionUID = 6684769029046869432L;

    @SuppressWarnings({"unchecked", "unused"})
    public CatDcmcatalogSerializer() {
        this((Class<T>) CatDcmcatalog.class);
    }

    protected CatDcmcatalogSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "dimensions_volumes", instance.getDimensions_volumes());
        writeNonNullNumber(generator, "dimensions_x", instance.getDimensions_x());
        writeNonNullNumber(generator, "dimensions_y", instance.getDimensions_y());
        writeNonNullNumber(generator, "dimensions_z", instance.getDimensions_z());
        writeNonBlankField(generator, "orientation", instance.getOrientation());
        writeNonBlankField(generator, "uid", instance.getUid());
        writeNonBlankField(generator, "voxelres_units", instance.getVoxelres_units());
        writeNonNullNumber(generator, "voxelres_x", instance.getVoxelres_x());
        writeNonNullNumber(generator, "voxelres_y", instance.getVoxelres_y());
        writeNonNullNumber(generator, "voxelres_z", instance.getVoxelres_z());
        super.serializeImpl(instance, generator, provider);
    }
}

