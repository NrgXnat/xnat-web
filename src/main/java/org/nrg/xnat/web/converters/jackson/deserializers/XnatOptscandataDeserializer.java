package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatOptscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatOptscandataDeserializer<T extends XnatOptscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = 5997048004406365702L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatOptscandataDeserializer() {
        this((Class<T>) XnatOptscandata.class);
    }

    protected XnatOptscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "dcmvalidation":
                instance.setDcmvalidation(parser.getText());
                break;
            case "dcmvalidation_status":
                instance.setDcmvalidation_status(parser.getBooleanValue());
                break;
            case "parameters_fov_x":
                instance.setParameters_fov_x(parser.getIntValue());
                break;
            case "parameters_fov_y":
                instance.setParameters_fov_y(parser.getIntValue());
                break;
            case "parameters_illuminationPower":
                instance.setParameters_illuminationPower(parser.getText());
                break;
            case "parameters_illuminationWavelength":
                instance.setParameters_illuminationWavelength(parser.getText());
                break;
            case "parameters_imagetype":
                instance.setParameters_imagetype(parser.getText());
                break;
            case "parameters_laterality":
                instance.setParameters_laterality(parser.getText());
                break;
            case "parameters_voxelres_units":
                instance.setParameters_voxelres_units(parser.getText());
                break;
            case "parameters_voxelres_x":
                instance.setParameters_voxelres_x(parser.getDoubleValue());
                break;
            case "parameters_voxelres_y":
                instance.setParameters_voxelres_y(parser.getDoubleValue());
                break;
            case "parameters_voxelres_z":
                instance.setParameters_voxelres_z(parser.getDoubleValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

