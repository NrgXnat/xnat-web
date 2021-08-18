package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatMrscandataDeserializer<T extends XnatMrscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = -2230115963696248751L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatMrscandataDeserializer() {
        this((Class<T>) XnatMrscandata.class);
    }

    protected XnatMrscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "coil":
                instance.setCoil(parser.getText());
                break;
            case "dcmvalidation":
                instance.setDcmvalidation(parser.getText());
                break;
            case "dcmvalidation_status":
                instance.setDcmvalidation_status(parser.getBooleanValue());
                break;
            case "fieldstrength":
                instance.setFieldstrength(parser.getText());
                break;
            case "filenameuuid":
                instance.setFilenameuuid(parser.getText());
                break;
            case "marker":
                instance.setMarker(parser.getText());
                break;
            case "parameters_acqtime":
                instance.setParameters_acqtime(parser.getText());
                break;
            case "parameters_acqtype":
                instance.setParameters_acqtype(parser.getText());
                break;
            case "parameters_addparam":
                // TODO: Handle the "parameters_addparam" property here: java.util.List
                break;
            case "parameters_coil":
                instance.setParameters_coil(parser.getText());
                break;
            case "parameters_coilelements":
                instance.setParameters_coilelements(parser.getText());
                break;
            case "parameters_deltate":
                instance.setParameters_deltate(parser.getDoubleValue());
                break;
            case "parameters_diffusion_anisotropytype":
                instance.setParameters_diffusion_anisotropytype(parser.getText());
                break;
            case "parameters_diffusion_bmax":
                instance.setParameters_diffusion_bmax(parser.getText());
                break;
            case "parameters_diffusion_bvalues":
                instance.setParameters_diffusion_bvalues(parser.getText());
                break;
            case "parameters_diffusion_directionality":
                instance.setParameters_diffusion_directionality(parser.getText());
                break;
            case "parameters_diffusion_orientations":
                instance.setParameters_diffusion_orientations(parser.getText());
                break;
            case "parameters_diffusion_refocusflipangle":
                instance.setParameters_diffusion_refocusflipangle(parser.getText());
                break;
            case "parameters_dtiacqcount":
                instance.setParameters_dtiacqcount(parser.getIntValue());
                break;
            case "parameters_echospacing":
                instance.setParameters_echospacing(parser.getDoubleValue());
                break;
            case "parameters_flip":
                instance.setParameters_flip(parser.getIntValue());
                break;
            case "parameters_fov_x":
                instance.setParameters_fov_x(parser.getIntValue());
                break;
            case "parameters_fov_y":
                instance.setParameters_fov_y(parser.getIntValue());
                break;
            case "parameters_imagetype":
                instance.setParameters_imagetype(parser.getText());
                break;
            case "parameters_inplanephaseencoding_direction":
                instance.setParameters_inplanephaseencoding_direction(parser.getText());
                break;
            case "parameters_inplanephaseencoding_directionpositive":
                instance.setParameters_inplanephaseencoding_directionpositive(parser.getText());
                break;
            case "parameters_inplanephaseencoding_polarityswap":
                instance.setParameters_inplanephaseencoding_polarityswap(parser.getText());
                break;
            case "parameters_inplanephaseencoding_rotation":
                instance.setParameters_inplanephaseencoding_rotation(parser.getText());
                break;
            case "parameters_matrix_x":
                instance.setParameters_matrix_x(parser.getIntValue());
                break;
            case "parameters_matrix_y":
                instance.setParameters_matrix_y(parser.getIntValue());
                break;
            case "parameters_orientation":
                instance.setParameters_orientation(parser.getText());
                break;
            case "parameters_origin":
                instance.setParameters_origin(parser.getText());
                break;
            case "parameters_partitions":
                instance.setParameters_partitions(parser.getIntValue());
                break;
            case "parameters_phaseencodingdirection":
                instance.setParameters_phaseencodingdirection(parser.getText());
                break;
            case "parameters_pixelbandwidth":
                instance.setParameters_pixelbandwidth(parser.getDoubleValue());
                break;
            case "parameters_pmc":
                instance.setParameters_pmc(parser.getText());
                break;
            case "parameters_readoutsamplespacing":
                instance.setParameters_readoutsamplespacing(parser.getText());
                break;
            case "parameters_scanoptions":
                instance.setParameters_scanoptions(parser.getText());
                break;
            case "parameters_scansequence":
                instance.setParameters_scansequence(parser.getText());
                break;
            case "parameters_sequence":
                instance.setParameters_sequence(parser.getText());
                break;
            case "parameters_seqvariant":
                instance.setParameters_seqvariant(parser.getText());
                break;
            case "parameters_subjectposition":
                instance.setParameters_subjectposition(parser.getText());
                break;
            case "parameters_te":
                instance.setParameters_te(parser.getDoubleValue());
                break;
            case "parameters_ti":
                instance.setParameters_ti(parser.getDoubleValue());
                break;
            case "parameters_tr":
                instance.setParameters_tr(parser.getDoubleValue());
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
            case "stabilization":
                instance.setStabilization(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

