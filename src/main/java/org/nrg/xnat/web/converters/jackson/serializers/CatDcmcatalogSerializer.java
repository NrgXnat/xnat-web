package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.CatDcmcatalog;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CatDcmcatalogSerializer<T extends CatDcmcatalog> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 4963718102273977718L;

    @SuppressWarnings("unchecked")
    public CatDcmcatalogSerializer() {
        this((Class<T>) CatDcmcatalog.class);
    }

    protected CatDcmcatalogSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "catalog" property here: org.nrg.xdat.om.CatCatalog
        // TODO: Write out the "dimensions_volumes" property here: Integer
        // TODO: Write out the "dimensions_x" property here: Integer
        // TODO: Write out the "dimensions_y" property here: Integer
        // TODO: Write out the "dimensions_z" property here: Integer
        // TODO: Write out the "orientation" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "voxelres_units" property here: String
        // TODO: Write out the "voxelres_x" property here: Double
        // TODO: Write out the "voxelres_y" property here: Double
        // TODO: Write out the "voxelres_z" property here: Double
    }
}

