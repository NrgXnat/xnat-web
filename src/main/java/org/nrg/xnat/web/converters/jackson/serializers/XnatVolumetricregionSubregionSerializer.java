package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatVolumetricregionSubregion;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatVolumetricregionSubregionSerializer<T extends XnatVolumetricregionSubregion> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 343442472636501850L;

    @SuppressWarnings("unchecked")
    public XnatVolumetricregionSubregionSerializer() {
        this((Class<T>) XnatVolumetricregionSubregion.class);
    }

    protected XnatVolumetricregionSubregionSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "voxels" property here: Double
        // TODO: Write out the "xnatVolumetricregionSubregionId" property here: Integer
    }
}

