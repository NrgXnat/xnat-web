package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatPetscandataSerializer<T extends XnatPetscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = -36088753698326368L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatPetscandataSerializer() {
        this((Class<T>) XnatPetscandata.class);
    }

    protected XnatPetscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "ecatvalidation", instance.getEcatvalidation());
        writeNonNullBoolean(generator, "ecatvalidation_status", instance.getEcatvalidation_status());
        writeNonNullNumber(generator, "parameters_acqtype", instance.getParameters_acqtype());
        // TODO: Write out the "parameters_addparam" property here: java.util.List
        writeNonBlankField(generator, "parameters_annotation", instance.getParameters_annotation());
        writeNonNullNumber(generator, "parameters_bedposition", instance.getParameters_bedposition());
        writeNonNullNumber(generator, "parameters_binsize", instance.getParameters_binsize());
        writeNonNullNumber(generator, "parameters_datatype", instance.getParameters_datatype());
        writeNonNullNumber(generator, "parameters_dimensions_num", instance.getParameters_dimensions_num());
        writeNonNullNumber(generator, "parameters_dimensions_x", instance.getParameters_dimensions_x());
        writeNonNullNumber(generator, "parameters_dimensions_y", instance.getParameters_dimensions_y());
        writeNonNullNumber(generator, "parameters_dimensions_z", instance.getParameters_dimensions_z());
        writeNonNullNumber(generator, "parameters_ecatcalibrationfactor", instance.getParameters_ecatcalibrationfactor());
        writeNonBlankField(generator, "parameters_facility", instance.getParameters_facility());
        writeNonNullNumber(generator, "parameters_filetype", instance.getParameters_filetype());
        writeNonNullNumber(generator, "parameters_filter_cutoff", instance.getParameters_filter_cutoff());
        writeNonNullNumber(generator, "parameters_filtercode", instance.getParameters_filtercode());
        // TODO: Write out the "parameters_frames_frame" property here: java.util.List
        // TODO: Write out the "parameters_frames_numframes" property here: Object
        writeNonNullNumber(generator, "parameters_gateduration", instance.getParameters_gateduration());
        writeNonNullNumber(generator, "parameters_mt11", instance.getParameters_mt11());
        writeNonNullNumber(generator, "parameters_mt12", instance.getParameters_mt12());
        writeNonNullNumber(generator, "parameters_mt13", instance.getParameters_mt13());
        writeNonNullNumber(generator, "parameters_mt14", instance.getParameters_mt14());
        writeNonNullNumber(generator, "parameters_mt21", instance.getParameters_mt21());
        writeNonNullNumber(generator, "parameters_mt22", instance.getParameters_mt22());
        writeNonNullNumber(generator, "parameters_mt23", instance.getParameters_mt23());
        writeNonNullNumber(generator, "parameters_mt24", instance.getParameters_mt24());
        writeNonNullNumber(generator, "parameters_mt31", instance.getParameters_mt31());
        writeNonNullNumber(generator, "parameters_mt32", instance.getParameters_mt32());
        writeNonNullNumber(generator, "parameters_mt33", instance.getParameters_mt33());
        writeNonNullNumber(generator, "parameters_mt34", instance.getParameters_mt34());
        writeNonNullNumber(generator, "parameters_numacceptedbeats", instance.getParameters_numacceptedbeats());
        writeNonNullNumber(generator, "parameters_numangles", instance.getParameters_numangles());
        writeNonNullNumber(generator, "parameters_numgates", instance.getParameters_numgates());
        writeNonNullNumber(generator, "parameters_numplanes", instance.getParameters_numplanes());
        writeNonNullNumber(generator, "parameters_numrelements", instance.getParameters_numrelements());
        writeNonNullNumber(generator, "parameters_offset_x", instance.getParameters_offset_x());
        writeNonNullNumber(generator, "parameters_offset_y", instance.getParameters_offset_y());
        writeNonNullNumber(generator, "parameters_offset_z", instance.getParameters_offset_z());
        writeNonNullNumber(generator, "parameters_orientation", instance.getParameters_orientation());
        writeNonBlankField(generator, "parameters_originalfilename", instance.getParameters_originalfilename());
        writeNonNullNumber(generator, "parameters_pixelsize_x", instance.getParameters_pixelsize_x());
        writeNonNullNumber(generator, "parameters_pixelsize_y", instance.getParameters_pixelsize_y());
        writeNonNullNumber(generator, "parameters_pixelsize_z", instance.getParameters_pixelsize_z());
        writeNonNullNumber(generator, "parameters_planeseparation", instance.getParameters_planeseparation());
        writeNonNullNumber(generator, "parameters_processingcode", instance.getParameters_processingcode());
        writeNonNullNumber(generator, "parameters_recontype", instance.getParameters_recontype());
        writeNonNullNumber(generator, "parameters_reconviews", instance.getParameters_reconviews());
        writeNonNullNumber(generator, "parameters_reconzoom", instance.getParameters_reconzoom());
        writeNonNullNumber(generator, "parameters_resolution_x", instance.getParameters_resolution_x());
        writeNonNullNumber(generator, "parameters_resolution_y", instance.getParameters_resolution_y());
        writeNonNullNumber(generator, "parameters_resolution_z", instance.getParameters_resolution_z());
        writeNonNullNumber(generator, "parameters_rfilter_code", instance.getParameters_rfilter_code());
        writeNonNullNumber(generator, "parameters_rfilter_cutoff", instance.getParameters_rfilter_cutoff());
        writeNonNullNumber(generator, "parameters_rfilter_order", instance.getParameters_rfilter_order());
        writeNonNullNumber(generator, "parameters_rfilter_resolution", instance.getParameters_rfilter_resolution());
        writeNonNullNumber(generator, "parameters_rwaveoffset", instance.getParameters_rwaveoffset());
        writeNonNullNumber(generator, "parameters_scattertype", instance.getParameters_scattertype());
        writeNonNullNumber(generator, "parameters_systemtype", instance.getParameters_systemtype());
        writeNonNullNumber(generator, "parameters_transaxialfov", instance.getParameters_transaxialfov());
        writeNonNullNumber(generator, "parameters_zfilter_code", instance.getParameters_zfilter_code());
        writeNonNullNumber(generator, "parameters_zfilter_cutoff", instance.getParameters_zfilter_cutoff());
        writeNonNullNumber(generator, "parameters_zfilter_order", instance.getParameters_zfilter_order());
        writeNonNullNumber(generator, "parameters_zfilter_resolution", instance.getParameters_zfilter_resolution());
        writeNonNullNumber(generator, "parameters_zrotationangle", instance.getParameters_zrotationangle());
        super.serializeImpl(instance, generator, provider);
    }
}

