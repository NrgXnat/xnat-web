package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatImageresourceseries;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatImageresourceseriesSerializer<T extends XnatImageresourceseries> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 2272788918790401746L;

    @SuppressWarnings("unchecked")
    public XnatImageresourceseriesSerializer() {
        this((Class<T>) XnatImageresourceseries.class);
    }

    protected XnatImageresourceseriesSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "dimensions_volumes" property here: Integer
        // TODO: Write out the "dimensions_x" property here: Integer
        // TODO: Write out the "dimensions_y" property here: Integer
        // TODO: Write out the "dimensions_z" property here: Integer
        // TODO: Write out the "orientation" property here: String
        // TODO: Write out the "resourceseries" property here: org.nrg.xdat.om.XnatResourceseries
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "voxelres_units" property here: String
        // TODO: Write out the "voxelres_x" property here: Double
        // TODO: Write out the "voxelres_y" property here: Double
        // TODO: Write out the "voxelres_z" property here: Double
    }
}

