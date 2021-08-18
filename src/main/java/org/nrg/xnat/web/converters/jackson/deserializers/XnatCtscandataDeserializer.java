package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatCtscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatCtscandataDeserializer<T extends XnatCtscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = 4202216273150128038L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatCtscandataDeserializer() {
        this((Class<T>) XnatCtscandata.class);
    }

    protected XnatCtscandataDeserializer(final Class<T> clazz) {
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
            case "parameters_acquisitionnumber":
                instance.setParameters_acquisitionnumber(parser.getIntValue());
                break;
            case "parameters_collectiondiameter":
                instance.setParameters_collectiondiameter(parser.getDoubleValue());
                break;
            case "parameters_collimationwidth_single":
                instance.setParameters_collimationwidth_single(parser.getDoubleValue());
                break;
            case "parameters_collimationwidth_total":
                instance.setParameters_collimationwidth_total(parser.getDoubleValue());
                break;
            case "parameters_contrastbolus":
                // TODO: Handle the "parameters_contrastbolus" property here: org.nrg.xdat.om.XnatContrastbolus
                break;
            case "parameters_convolutionkernel":
                instance.setParameters_convolutionkernel(parser.getText());
                break;
            case "parameters_ctdivol":
                instance.setParameters_ctdivol(parser.getDoubleValue());
                break;
            case "parameters_derivation":
                instance.setParameters_derivation(parser.getText());
                break;
            case "parameters_distancesourcetodetector":
                instance.setParameters_distancesourcetodetector(parser.getDoubleValue());
                break;
            case "parameters_distancesourcetopatient":
                instance.setParameters_distancesourcetopatient(parser.getDoubleValue());
                break;
            case "parameters_estimateddosesaving":
                instance.setParameters_estimateddosesaving(parser.getDoubleValue());
                break;
            case "parameters_estimateddosesaving_modulation":
                instance.setParameters_estimateddosesaving_modulation(parser.getText());
                break;
            case "parameters_exposure":
                instance.setParameters_exposure(parser.getDoubleValue());
                break;
            case "parameters_exposuretime":
                instance.setParameters_exposuretime(parser.getDoubleValue());
                break;
            case "parameters_filter":
                instance.setParameters_filter(parser.getText());
                break;
            case "parameters_focalspots_focalspot":
                // TODO: Handle the "parameters_focalspots_focalspot" property here: java.util.List
                break;
            case "parameters_fov_x":
                instance.setParameters_fov_x(parser.getIntValue());
                break;
            case "parameters_fov_y":
                instance.setParameters_fov_y(parser.getIntValue());
                break;
            case "parameters_gantrytilt":
                instance.setParameters_gantrytilt(parser.getDoubleValue());
                break;
            case "parameters_generatorpower":
                instance.setParameters_generatorpower(parser.getDoubleValue());
                break;
            case "parameters_imagetype":
                instance.setParameters_imagetype(parser.getText());
                break;
            case "parameters_kvp":
                instance.setParameters_kvp(parser.getDoubleValue());
                break;
            case "parameters_options":
                instance.setParameters_options(parser.getText());
                break;
            case "parameters_orientation":
                instance.setParameters_orientation(parser.getText());
                break;
            case "parameters_pitchfactor":
                instance.setParameters_pitchfactor(parser.getDoubleValue());
                break;
            case "parameters_rescale_intercept":
                instance.setParameters_rescale_intercept(parser.getText());
                break;
            case "parameters_rescale_slope":
                instance.setParameters_rescale_slope(parser.getText());
                break;
            case "parameters_rotationdirection":
                instance.setParameters_rotationdirection(parser.getText());
                break;
            case "parameters_subjectposition":
                instance.setParameters_subjectposition(parser.getText());
                break;
            case "parameters_tablefeedperrotation":
                instance.setParameters_tablefeedperrotation(parser.getDoubleValue());
                break;
            case "parameters_tableheight":
                instance.setParameters_tableheight(parser.getDoubleValue());
                break;
            case "parameters_tablespeed":
                instance.setParameters_tablespeed(parser.getDoubleValue());
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
            case "parameters_xraytubecurrent":
                instance.setParameters_xraytubecurrent(parser.getDoubleValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

