package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDicomseries;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatDicomseriesSerializer<T extends XnatDicomseries> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 3396470327475524284L;

    @SuppressWarnings("unchecked")
    public XnatDicomseriesSerializer() {
        this((Class<T>) XnatDicomseries.class);
    }

    protected XnatDicomseriesSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "abstractresource" property here: org.nrg.xdat.om.XnatAbstractresource
        // TODO: Write out the "cachepath" property here: String
        // TODO: Write out the "content" property here: String
        // TODO: Write out the "description" property here: String
        // TODO: Write out the "dimensions_volumes" property here: Integer
        // TODO: Write out the "dimensions_x" property here: Integer
        // TODO: Write out the "dimensions_y" property here: Integer
        // TODO: Write out the "dimensions_z" property here: Integer
        // TODO: Write out the "format" property here: String
        // TODO: Write out the "imageset_image" property here: java.util.List
        // TODO: Write out the "label" property here: String
        // TODO: Write out the "orientation" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "unresolvedPaths" property here: java.util.ArrayList
        // TODO: Write out the "voxelres_units" property here: String
        // TODO: Write out the "voxelres_x" property here: Double
        // TODO: Write out the "voxelres_y" property here: Double
        // TODO: Write out the "voxelres_z" property here: Double
    }
}

