package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.om.XnatAddfield;
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
            case "dcmvalidationStatus":
                instance.setDcmvalidation_status(parser.getBooleanValue());
                break;
            case "fieldStrength":
                instance.setFieldstrength(parser.getText());
                break;
            case "fileNameUuid":
                instance.setFilenameuuid(parser.getText());
                break;
            case "marker":
                instance.setMarker(parser.getText());
                break;
            case "parametersAcqtime":
                instance.setParameters_acqtime(parser.getText());
                break;
            case "parametersAcqtype":
                instance.setParameters_acqtype(parser.getText());
                break;
            case "parametersAddParam":
                // TODO: Handle the "parameters_addparam" property here: java.util.List
            	try {
            		instance.setParameters_addparam(parser.readValueAs(XnatAddfield.class));
            	} catch (Exception e) {
            		log.error("Tried to set a field parameters add param in Mrscandata but failed", e);
            	}
                break;
            case "parametersCoil":
                instance.setParameters_coil(parser.getText());
                break;
            case "parametersCoilElements":
                instance.setParameters_coilelements(parser.getText());
                break;
            case "parametersDeltate":
                instance.setParameters_deltate(parser.getDoubleValue());
                break;
            case "parametersDiffusionAnisotropytype":
                instance.setParameters_diffusion_anisotropytype(parser.getText());
                break;
            case "parametersDiffusionBmax":
                instance.setParameters_diffusion_bmax(parser.getText());
                break;
            case "parametersDiffusionBvalues":
                instance.setParameters_diffusion_bvalues(parser.getText());
                break;
            case "parametersDiffusionDirectionality":
                instance.setParameters_diffusion_directionality(parser.getText());
                break;
            case "parametersDiffusionOrientations":
                instance.setParameters_diffusion_orientations(parser.getText());
                break;
            case "parametersDiffusionRefocusflipangle":
                instance.setParameters_diffusion_refocusflipangle(parser.getText());
                break;
            case "parametersDtiacqcount":
                instance.setParameters_dtiacqcount(parser.getIntValue());
                break;
            case "parametersEchospacing":
                instance.setParameters_echospacing(parser.getDoubleValue());
                break;
            case "parametersFlip":
                instance.setParameters_flip(parser.getIntValue());
                break;
            case "parametersFov_x":
                instance.setParameters_fov_x(parser.getIntValue());
                break;
            case "parametersFov_y":
                instance.setParameters_fov_y(parser.getIntValue());
                break;
            case "parametersImageType":
                instance.setParameters_imagetype(parser.getText());
                break;
            case "parametersInplanephaseencodingDirection":
                instance.setParameters_inplanephaseencoding_direction(parser.getText());
                break;
            case "parametersInplanephaseencodingDirectionpositive":
                instance.setParameters_inplanephaseencoding_directionpositive(parser.getText());
                break;
            case "parametersInplanephaseencodingPolarityswap":
                instance.setParameters_inplanephaseencoding_polarityswap(parser.getText());
                break;
            case "parametersInplanephaseencodingRotation":
                instance.setParameters_inplanephaseencoding_rotation(parser.getText());
                break;
            case "parametersMatrix_x":
                instance.setParameters_matrix_x(parser.getIntValue());
                break;
            case "parametersMatrix_y":
                instance.setParameters_matrix_y(parser.getIntValue());
                break;
            case "parametersOrientation":
                instance.setParameters_orientation(parser.getText());
                break;
            case "parametersOrigin":
                instance.setParameters_origin(parser.getText());
                break;
            case "parametersPartitions":
                instance.setParameters_partitions(parser.getIntValue());
                break;
            case "parametersPhaseEncodingDirection":
                instance.setParameters_phaseencodingdirection(parser.getText());
                break;
            case "parametersPixelBandwidth":
                instance.setParameters_pixelbandwidth(parser.getDoubleValue());
                break;
            case "parametersPmc":
                instance.setParameters_pmc(parser.getText());
                break;
            case "parametersReadoutSampleSpacing":
                instance.setParameters_readoutsamplespacing(parser.getText());
                break;
            case "parametersScanOptions":
                instance.setParameters_scanoptions(parser.getText());
                break;
            case "parametersScanSequence":
                instance.setParameters_scansequence(parser.getText());
                break;
            case "parametersSequence":
                instance.setParameters_sequence(parser.getText());
                break;
            case "parametersSeqvariant":
                instance.setParameters_seqvariant(parser.getText());
                break;
            case "parametersSubjectPosition":
                instance.setParameters_subjectposition(parser.getText());
                break;
            case "parametersTe":
                instance.setParameters_te(parser.getDoubleValue());
                break;
            case "parametersTi":
                instance.setParameters_ti(parser.getDoubleValue());
                break;
            case "parametersTr":
                instance.setParameters_tr(parser.getDoubleValue());
                break;
            case "parametersVoxelresUnits":
                instance.setParameters_voxelres_units(parser.getText());
                break;
            case "parametersVoxelresX":
                instance.setParameters_voxelres_x(parser.getDoubleValue());
                break;
            case "parametersVoxelresY":
                instance.setParameters_voxelres_y(parser.getDoubleValue());
                break;
            case "parametersVoxelresZ":
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

