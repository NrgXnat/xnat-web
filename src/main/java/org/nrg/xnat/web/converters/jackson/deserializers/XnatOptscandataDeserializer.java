package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatOptscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatOptscandataDeserializer<T extends XnatOptscandata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -5191982512922662240L;

    @SuppressWarnings("unchecked")
    public XnatOptscandataDeserializer() {
        this((Class<T>) XnatOptscandata.class);
    }

    public XnatOptscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "dcmvalidation":
                // TODO: Handle the "dcmvalidation" property here: String
                break;
            case "dcmvalidation_status":
                // TODO: Handle the "dcmvalidation_status" property here: Boolean
                break;
            case "imagescandata":
                // TODO: Handle the "imagescandata" property here: org.nrg.xdat.om.XnatImagescandata
                break;
            case "parameters_fov_x":
                // TODO: Handle the "parameters_fov_x" property here: Integer
                break;
            case "parameters_fov_y":
                // TODO: Handle the "parameters_fov_y" property here: Integer
                break;
            case "parameters_illuminationPower":
                // TODO: Handle the "parameters_illuminationPower" property here: String
                break;
            case "parameters_illuminationWavelength":
                // TODO: Handle the "parameters_illuminationWavelength" property here: String
                break;
            case "parameters_imagetype":
                // TODO: Handle the "parameters_imagetype" property here: String
                break;
            case "parameters_laterality":
                // TODO: Handle the "parameters_laterality" property here: String
                break;
            case "parameters_voxelres_units":
                // TODO: Handle the "parameters_voxelres_units" property here: String
                break;
            case "parameters_voxelres_x":
                // TODO: Handle the "parameters_voxelres_x" property here: Double
                break;
            case "parameters_voxelres_y":
                // TODO: Handle the "parameters_voxelres_y" property here: Double
                break;
            case "parameters_voxelres_z":
                // TODO: Handle the "parameters_voxelres_z" property here: Double
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

