package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatVolumetricregion;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatVolumetricregionDeserializer<T extends XnatVolumetricregion> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 5578523719088032666L;

    @SuppressWarnings("unchecked")
    public XnatVolumetricregionDeserializer() {
        this((Class<T>) XnatVolumetricregion.class);
    }

    public XnatVolumetricregionDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "hemisphere":
                // TODO: Handle the "hemisphere" property here: String
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "subregions_subregion":
                // TODO: Handle the "subregions_subregion" property here: java.util.List
                break;
            case "units":
                // TODO: Handle the "units" property here: String
                break;
            case "voxels":
                // TODO: Handle the "voxels" property here: Integer
                break;
            case "xnatVolumetricregionId":
                // TODO: Handle the "xnatVolumetricregionId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

