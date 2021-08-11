package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatImageresource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatImageresourceDeserializer<T extends XnatImageresource> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -3231980128341491673L;

    @SuppressWarnings("unchecked")
    public XnatImageresourceDeserializer() {
        this((Class<T>) XnatImageresource.class);
    }

    public XnatImageresourceDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "dimensions_volumes":
                // TODO: Handle the "dimensions_volumes" property here: Integer
                break;
            case "dimensions_x":
                // TODO: Handle the "dimensions_x" property here: Integer
                break;
            case "dimensions_y":
                // TODO: Handle the "dimensions_y" property here: Integer
                break;
            case "dimensions_z":
                // TODO: Handle the "dimensions_z" property here: Integer
                break;
            case "orientation":
                // TODO: Handle the "orientation" property here: String
                break;
            case "resource":
                // TODO: Handle the "resource" property here: org.nrg.xdat.om.XnatResource
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "voxelres_units":
                // TODO: Handle the "voxelres_units" property here: String
                break;
            case "voxelres_x":
                // TODO: Handle the "voxelres_x" property here: Double
                break;
            case "voxelres_y":
                // TODO: Handle the "voxelres_y" property here: Double
                break;
            case "voxelres_z":
                // TODO: Handle the "voxelres_z" property here: Double
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

