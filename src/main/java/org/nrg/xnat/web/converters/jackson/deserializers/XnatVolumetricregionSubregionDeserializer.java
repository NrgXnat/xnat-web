package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatVolumetricregionSubregion;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatVolumetricregionSubregionDeserializer<T extends XnatVolumetricregionSubregion> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 4447079599401461326L;

    @SuppressWarnings("unchecked")
    public XnatVolumetricregionSubregionDeserializer() {
        this((Class<T>) XnatVolumetricregionSubregion.class);
    }

    public XnatVolumetricregionSubregionDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "voxels":
                // TODO: Handle the "voxels" property here: Double
                break;
            case "xnatVolumetricregionSubregionId":
                // TODO: Handle the "xnatVolumetricregionSubregionId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

