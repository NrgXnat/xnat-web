package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrscandata;

import java.io.IOException;

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
        writeNonNullBoolean(generator, "dcmvalidation_status", instance.getDcmvalidation_status());
        writeNonBlankField(generator, "fieldstrength", instance.getFieldstrength());
        writeNonBlankField(generator, "filenameuuid", instance.getFilenameuuid());
        writeNonBlankField(generator, "marker", instance.getMarker());
        writeNonBlankField(generator, "parameters_acqtime", instance.getParameters_acqtime());
        writeNonBlankField(generator, "parameters_acqtype", instance.getParameters_acqtype());
        // TODO: Write out the "parameters_addparam" property here: java.util.List
        writeNonBlankField(generator, "parameters_coil", instance.getParameters_coil());
        writeNonBlankField(generator, "parameters_coilelements", instance.getParameters_coilelements());
        writeNonNullNumber(generator, "parameters_deltate", instance.getParameters_deltate());
        writeNonBlankField(generator, "parameters_diffusion_anisotropytype", instance.getParameters_diffusion_anisotropytype());
        writeNonBlankField(generator, "parameters_diffusion_bmax", instance.getParameters_diffusion_bmax());
        writeNonBlankField(generator, "parameters_diffusion_bvalues", instance.getParameters_diffusion_bvalues());
        writeNonBlankField(generator, "parameters_diffusion_directionality", instance.getParameters_diffusion_directionality());
        writeNonBlankField(generator, "parameters_diffusion_orientations", instance.getParameters_diffusion_orientations());
        writeNonBlankField(generator, "parameters_diffusion_refocusflipangle", instance.getParameters_diffusion_refocusflipangle());
        writeNonNullNumber(generator, "parameters_dtiacqcount", instance.getParameters_dtiacqcount());
        writeNonNullNumber(generator, "parameters_echospacing", instance.getParameters_echospacing());
        writeNonNullNumber(generator, "parameters_flip", instance.getParameters_flip());
        writeNonNullNumber(generator, "parameters_fov_x", instance.getParameters_fov_x());
        writeNonNullNumber(generator, "parameters_fov_y", instance.getParameters_fov_y());
        writeNonBlankField(generator, "parameters_imagetype", instance.getParameters_imagetype());
        writeNonBlankField(generator, "parameters_inplanephaseencoding_direction", instance.getParameters_inplanephaseencoding_direction());
        writeNonBlankField(generator, "parameters_inplanephaseencoding_directionpositive", instance.getParameters_inplanephaseencoding_directionpositive());
        writeNonBlankField(generator, "parameters_inplanephaseencoding_polarityswap", instance.getParameters_inplanephaseencoding_polarityswap());
        writeNonBlankField(generator, "parameters_inplanephaseencoding_rotation", instance.getParameters_inplanephaseencoding_rotation());
        writeNonNullNumber(generator, "parameters_matrix_x", instance.getParameters_matrix_x());
        writeNonNullNumber(generator, "parameters_matrix_y", instance.getParameters_matrix_y());
        writeNonBlankField(generator, "parameters_orientation", instance.getParameters_orientation());
        writeNonBlankField(generator, "parameters_origin", instance.getParameters_origin());
        writeNonNullNumber(generator, "parameters_partitions", instance.getParameters_partitions());
        writeNonBlankField(generator, "parameters_phaseencodingdirection", instance.getParameters_phaseencodingdirection());
        writeNonNullNumber(generator, "parameters_pixelbandwidth", instance.getParameters_pixelbandwidth());
        writeNonBlankField(generator, "parameters_pmc", instance.getParameters_pmc());
        writeNonBlankField(generator, "parameters_readoutsamplespacing", instance.getParameters_readoutsamplespacing());
        writeNonBlankField(generator, "parameters_scanoptions", instance.getParameters_scanoptions());
        writeNonBlankField(generator, "parameters_scansequence", instance.getParameters_scansequence());
        writeNonBlankField(generator, "parameters_sequence", instance.getParameters_sequence());
        writeNonBlankField(generator, "parameters_seqvariant", instance.getParameters_seqvariant());
        writeNonBlankField(generator, "parameters_subjectposition", instance.getParameters_subjectposition());
        writeNonNullNumber(generator, "parameters_te", instance.getParameters_te());
        writeNonNullNumber(generator, "parameters_ti", instance.getParameters_ti());
        writeNonNullNumber(generator, "parameters_tr", instance.getParameters_tr());
        writeNonBlankField(generator, "parameters_voxelres_units", instance.getParameters_voxelres_units());
        writeNonNullNumber(generator, "parameters_voxelres_x", instance.getParameters_voxelres_x());
        writeNonNullNumber(generator, "parameters_voxelres_y", instance.getParameters_voxelres_y());
        writeNonNullNumber(generator, "parameters_voxelres_z", instance.getParameters_voxelres_z());
        writeNonBlankField(generator, "stabilization", instance.getStabilization());
        super.serializeImpl(instance, generator, provider);
    }
}

