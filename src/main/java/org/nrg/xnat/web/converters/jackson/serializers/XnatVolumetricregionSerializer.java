package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatVolumetricregion;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatVolumetricregionSerializer<T extends XnatVolumetricregion> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 7384670888819046737L;

    @SuppressWarnings("unchecked")
    public XnatVolumetricregionSerializer() {
        this((Class<T>) XnatVolumetricregion.class);
    }

    protected XnatVolumetricregionSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "hemisphere" property here: String
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "subregions_subregion" property here: java.util.List
        // TODO: Write out the "units" property here: String
        // TODO: Write out the "voxels" property here: Integer
        // TODO: Write out the "xnatVolumetricregionId" property here: Integer
    }
}

