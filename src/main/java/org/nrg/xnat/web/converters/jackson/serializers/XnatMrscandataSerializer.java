package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.model.XnatAddfieldI;
import org.nrg.xdat.model.XnatCtscandataFocalspotI;
import org.nrg.xdat.om.XnatMrscandata;

import java.io.IOException;
import java.util.List;

@XnatSerializer
@Slf4j
public class XnatMrscandataSerializer<T extends XnatMrscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = 5201837185234798834L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatMrscandataSerializer() {
        this((Class<T>) XnatMrscandata.class);
    }

    protected XnatMrscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "coil", instance.getCoil());
        writeNonBlankField(generator, "dcmvalidation", instance.getDcmvalidation());
        writeNonNullBoolean(generator, "dcmvalidationStatus", instance.getDcmvalidation_status());
        writeNonBlankField(generator, "fieldStrength", instance.getFieldstrength());
        writeNonBlankField(generator, "fileNameUuid", instance.getFilenameuuid());
        writeNonBlankField(generator, "marker", instance.getMarker());
        writeNonBlankField(generator, "parametersAcqtime", instance.getParameters_acqtime());
        writeNonBlankField(generator, "parametersAcqtype", instance.getParameters_acqtype());
        // TODO: Write out the "parameters_addparam" property here: java.util.List
        final List<XnatAddfieldI> parametersAddParams = instance.getParameters_addparam();
        writeParametersAddpParam(generator, parametersAddParams);
        
        writeNonBlankField(generator, "parametersCoil", instance.getParameters_coil());
        writeNonBlankField(generator, "parametersCoilElements", instance.getParameters_coilelements());
        writeNonNullNumber(generator, "parametersDeltate", instance.getParameters_deltate());
        writeNonBlankField(generator, "parametersDiffusionAnisotropytype", instance.getParameters_diffusion_anisotropytype());
        writeNonBlankField(generator, "parametersDiffusionBmax", instance.getParameters_diffusion_bmax());
        writeNonBlankField(generator, "parametersDiffusionBvalues", instance.getParameters_diffusion_bvalues());
        writeNonBlankField(generator, "parametersDiffusionDirectionality", instance.getParameters_diffusion_directionality());
        writeNonBlankField(generator, "parametersDiffusionOrientations", instance.getParameters_diffusion_orientations());
        writeNonBlankField(generator, "parametersDiffusionRefocusflipangle", instance.getParameters_diffusion_refocusflipangle());
        writeNonNullNumber(generator, "parametersDtiacqcount", instance.getParameters_dtiacqcount());
        writeNonNullNumber(generator, "parametersEchospacing", instance.getParameters_echospacing());
        writeNonNullNumber(generator, "parametersFlip", instance.getParameters_flip());
        writeNonNullNumber(generator, "parametersFov_x", instance.getParameters_fov_x());
        writeNonNullNumber(generator, "parametersFov_y", instance.getParameters_fov_y());
        writeNonBlankField(generator, "parametersImagetype", instance.getParameters_imagetype());
        writeNonBlankField(generator, "parametersInplanephaseencodingDirection", instance.getParameters_inplanephaseencoding_direction());
        writeNonBlankField(generator, "parametersInplanephaseencodingDirectionpositive", instance.getParameters_inplanephaseencoding_directionpositive());
        writeNonBlankField(generator, "parametersInplanephaseencodingPolarityswap", instance.getParameters_inplanephaseencoding_polarityswap());
        writeNonBlankField(generator, "parametersInplanephaseencodingRotation", instance.getParameters_inplanephaseencoding_rotation());
        writeNonNullNumber(generator, "parametersMatrix_x", instance.getParameters_matrix_x());
        writeNonNullNumber(generator, "parametersMatrix_y", instance.getParameters_matrix_y());
        writeNonBlankField(generator, "parametersMrientation", instance.getParameters_orientation());
        writeNonBlankField(generator, "parametersOrigin", instance.getParameters_origin());
        writeNonNullNumber(generator, "parametersPartitions", instance.getParameters_partitions());
        writeNonBlankField(generator, "parametersPhaseEncodingDirection", instance.getParameters_phaseencodingdirection());
        writeNonNullNumber(generator, "parametersPixelBandwidth", instance.getParameters_pixelbandwidth());
        writeNonBlankField(generator, "parametersPmc", instance.getParameters_pmc());
        writeNonBlankField(generator, "parametersReadoutSampleSpacing", instance.getParameters_readoutsamplespacing());
        writeNonBlankField(generator, "parametersScanOptions", instance.getParameters_scanoptions());
        writeNonBlankField(generator, "parametersScanSequence", instance.getParameters_scansequence());
        writeNonBlankField(generator, "parametersSequence", instance.getParameters_sequence());
        writeNonBlankField(generator, "parametersSeqvariant", instance.getParameters_seqvariant());
        writeNonBlankField(generator, "parametersSubjectPosition", instance.getParameters_subjectposition());
        writeNonNullNumber(generator, "parametersTe", instance.getParameters_te());
        writeNonNullNumber(generator, "parametersTi", instance.getParameters_ti());
        writeNonNullNumber(generator, "parametersTr", instance.getParameters_tr());
        writeNonBlankField(generator, "parametersVoxelresUnits", instance.getParameters_voxelres_units());
        writeNonNullNumber(generator, "parametersVoxelresX", instance.getParameters_voxelres_x());
        writeNonNullNumber(generator, "parametersVoxelresY", instance.getParameters_voxelres_y());
        writeNonNullNumber(generator, "parametersVoxelresZ", instance.getParameters_voxelres_z());
        writeNonBlankField(generator, "stabilization", instance.getStabilization());
        super.serializeImpl(instance, generator, provider);
    }
}

