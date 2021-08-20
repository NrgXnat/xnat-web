package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.model.XnatAddfieldI;
import org.nrg.xdat.model.XnatPetscandataFrameI;
import org.nrg.xdat.om.XnatPetscandata;

import java.io.IOException;
import java.util.List;

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
        writeNonBlankField(generator, "ecatValidation", instance.getEcatvalidation());
        writeNonNullBoolean(generator, "ecatValidationStatus", instance.getEcatvalidation_status());
        writeNonNullNumber(generator, "parametersAcqtype", instance.getParameters_acqtype());
        // TODO: Write out the "parameters_addparam" property here: java.util.List
        final List<XnatAddfieldI> parametersAddParams = instance.getParameters_addparam();
        writeParametersAddpParam(generator, parametersAddParams);
        
        writeNonBlankField(generator, "parametersAnnotation", instance.getParameters_annotation());
        writeNonNullNumber(generator, "parametersBedPosition", instance.getParameters_bedposition());
        writeNonNullNumber(generator, "parameterBinSize", instance.getParameters_binsize());
        writeNonNullNumber(generator, "parametersDataType", instance.getParameters_datatype());
        writeNonNullNumber(generator, "parametersDimensionsNum", instance.getParameters_dimensions_num());
        writeNonNullNumber(generator, "parametersDimensionsX", instance.getParameters_dimensions_x());
        writeNonNullNumber(generator, "parametersDimensionsY", instance.getParameters_dimensions_y());
        writeNonNullNumber(generator, "parametersDimensionsZ", instance.getParameters_dimensions_z());
        writeNonNullNumber(generator, "parametersEcatcalibrationFactor", instance.getParameters_ecatcalibrationfactor());
        writeNonBlankField(generator, "parametersFacility", instance.getParameters_facility());
        writeNonNullNumber(generator, "parametersFileType", instance.getParameters_filetype());
        writeNonNullNumber(generator, "parametersFilterCutoff", instance.getParameters_filter_cutoff());
        writeNonNullNumber(generator, "parametersFilterCode", instance.getParameters_filtercode());
        // TODO: Write out the "parameters_frames_frame" property here: java.util.List
        final List<XnatPetscandataFrameI> parametersFramesFrames = instance.getParameters_frames_frame();
        if (parametersFramesFrames != null && !parametersFramesFrames.isEmpty()) {
            generator.writeArrayFieldStart("parametersFramesFrame");
            for (final XnatPetscandataFrameI parametersFramesFrame : parametersFramesFrames) {
                generator.writeObject(parametersFramesFrame);
            }
            generator.writeEndArray();
        }
        // TODO: Write out the "parameters_frames_numframes" property here: Object
        generator.writeStartObject();
        generator.writeObjectField("parametersFramesNumFrames", instance.getParameters_frames_numframes());
        generator.writeEndObject();
        writeNonNullNumber(generator, "parametersGateDuration", instance.getParameters_gateduration());
        writeNonNullNumber(generator, "parametersMt11", instance.getParameters_mt11());
        writeNonNullNumber(generator, "parametersMt12", instance.getParameters_mt12());
        writeNonNullNumber(generator, "parametersMt13", instance.getParameters_mt13());
        writeNonNullNumber(generator, "parametersMt14", instance.getParameters_mt14());
        writeNonNullNumber(generator, "parametersMt21", instance.getParameters_mt21());
        writeNonNullNumber(generator, "parametersMt22", instance.getParameters_mt22());
        writeNonNullNumber(generator, "parametersMt23", instance.getParameters_mt23());
        writeNonNullNumber(generator, "parametersMt24", instance.getParameters_mt24());
        writeNonNullNumber(generator, "parametersMt31", instance.getParameters_mt31());
        writeNonNullNumber(generator, "parametersMt32", instance.getParameters_mt32());
        writeNonNullNumber(generator, "parametersMt33", instance.getParameters_mt33());
        writeNonNullNumber(generator, "parametersMt34", instance.getParameters_mt34());
        writeNonNullNumber(generator, "parametersNumAcceptedBeats", instance.getParameters_numacceptedbeats());
        writeNonNullNumber(generator, "parametersNumAngles", instance.getParameters_numangles());
        writeNonNullNumber(generator, "parametersNumGates", instance.getParameters_numgates());
        writeNonNullNumber(generator, "parametersNumPlanes", instance.getParameters_numplanes());
        writeNonNullNumber(generator, "parametersNumRelements", instance.getParameters_numrelements());
        writeNonNullNumber(generator, "parametersOffsetX", instance.getParameters_offset_x());
        writeNonNullNumber(generator, "parametersOffsetY", instance.getParameters_offset_y());
        writeNonNullNumber(generator, "parametersOffsetZ", instance.getParameters_offset_z());
        writeNonNullNumber(generator, "parametersOrientation", instance.getParameters_orientation());
        writeNonBlankField(generator, "parametersOriginalFileName", instance.getParameters_originalfilename());
        writeNonNullNumber(generator, "parametersPixelSizeX", instance.getParameters_pixelsize_x());
        writeNonNullNumber(generator, "parametersPixelSizeY", instance.getParameters_pixelsize_y());
        writeNonNullNumber(generator, "parametersPixelsizeZ", instance.getParameters_pixelsize_z());
        writeNonNullNumber(generator, "parametersPlaneSeparation", instance.getParameters_planeseparation());
        writeNonNullNumber(generator, "parametersProcessingCode", instance.getParameters_processingcode());
        writeNonNullNumber(generator, "parametersReconType", instance.getParameters_recontype());
        writeNonNullNumber(generator, "parametersReconViews", instance.getParameters_reconviews());
        writeNonNullNumber(generator, "parametersReconZoom", instance.getParameters_reconzoom());
        writeNonNullNumber(generator, "parametersResolutionX", instance.getParameters_resolution_x());
        writeNonNullNumber(generator, "parametersResolutionY", instance.getParameters_resolution_y());
        writeNonNullNumber(generator, "parametersResolutionZ", instance.getParameters_resolution_z());
        writeNonNullNumber(generator, "parametersRfilterCode", instance.getParameters_rfilter_code());
        writeNonNullNumber(generator, "parametersRfilterCutoff", instance.getParameters_rfilter_cutoff());
        writeNonNullNumber(generator, "parametersRfilterOrder", instance.getParameters_rfilter_order());
        writeNonNullNumber(generator, "parametersRfilterResolution", instance.getParameters_rfilter_resolution());
        writeNonNullNumber(generator, "parametersRwaveOffset", instance.getParameters_rwaveoffset());
        writeNonNullNumber(generator, "parametersScatterType", instance.getParameters_scattertype());
        writeNonNullNumber(generator, "parametersSystemType", instance.getParameters_systemtype());
        writeNonNullNumber(generator, "parametersTransAxialFov", instance.getParameters_transaxialfov());
        writeNonNullNumber(generator, "parametersZfilterCode", instance.getParameters_zfilter_code());
        writeNonNullNumber(generator, "parametersZfilterCutoff", instance.getParameters_zfilter_cutoff());
        writeNonNullNumber(generator, "parametersZfilterOrder", instance.getParameters_zfilter_order());
        writeNonNullNumber(generator, "parametersZfilterResolution", instance.getParameters_zfilter_resolution());
        writeNonNullNumber(generator, "parametersZrotationAngle", instance.getParameters_zrotationangle());
        super.serializeImpl(instance, generator, provider);
    }
}

