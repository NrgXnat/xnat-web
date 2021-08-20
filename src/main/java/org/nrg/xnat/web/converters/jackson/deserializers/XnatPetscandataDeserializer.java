package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatPetscandataDeserializer<T extends XnatPetscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = -3039316584511671072L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatPetscandataDeserializer() {
        this((Class<T>) XnatPetscandata.class);
    }

    protected XnatPetscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "ecatValidation":
                instance.setEcatvalidation(parser.getText());
                break;
            case "ecatValidationStatus":
                instance.setEcatvalidation_status(parser.getBooleanValue());
                break;
            case "parametersAcqtype":
                instance.setParameters_acqtype(parser.getIntValue());
                break;
            case "parametersAddParam":
                // TODO: Handle the "parameters_addparam" property here: java.util.List
                break;
            case "parametersAnnotation":
                instance.setParameters_annotation(parser.getText());
                break;
            case "parametersBedPosition":
                instance.setParameters_bedposition(parser.getDoubleValue());
                break;
            case "parametersBinSize":
                instance.setParameters_binsize(parser.getDoubleValue());
                break;
            case "parametersDataType":
                instance.setParameters_datatype(parser.getIntValue());
                break;
            case "parametersDimensionsNum":
                instance.setParameters_dimensions_num(parser.getIntValue());
                break;
            case "parametersDimensionsX":
                instance.setParameters_dimensions_x(parser.getIntValue());
                break;
            case "parametersDimensionsY":
                instance.setParameters_dimensions_y(parser.getIntValue());
                break;
            case "parametersDimensionsZ":
                instance.setParameters_dimensions_z(parser.getIntValue());
                break;
            case "parametersEcatCalibrationFactor":
                instance.setParameters_ecatcalibrationfactor(parser.getDoubleValue());
                break;
            case "parametersFacility":
                instance.setParameters_facility(parser.getText());
                break;
            case "parametersFileType":
                instance.setParameters_filetype(parser.getIntValue());
                break;
            case "parametersFilterCutoff":
                instance.setParameters_filter_cutoff(parser.getDoubleValue());
                break;
            case "parametersFilterCode":
                instance.setParameters_filtercode(parser.getIntValue());
                break;
            case "parametersFramesFrame":
                // TODO: Handle the "parameters_frames_frame" property here: java.util.List
                break;
            case "parametersFramesNumFrames":
                // TODO: Handle the "parameters_frames_numframes" property here: Object
                break;
            case "parametersGateDuration":
                instance.setParameters_gateduration(parser.getIntValue());
                break;
            case "parametersMt11":
                instance.setParameters_mt11(parser.getDoubleValue());
                break;
            case "parametersMt12":
                instance.setParameters_mt12(parser.getDoubleValue());
                break;
            case "parametersMt13":
                instance.setParameters_mt13(parser.getDoubleValue());
                break;
            case "parametersMt14":
                instance.setParameters_mt14(parser.getDoubleValue());
                break;
            case "parametersMt21":
                instance.setParameters_mt21(parser.getDoubleValue());
                break;
            case "parametersMt22":
                instance.setParameters_mt22(parser.getDoubleValue());
                break;
            case "parametersMt23":
                instance.setParameters_mt23(parser.getDoubleValue());
                break;
            case "parametersMt24":
                instance.setParameters_mt24(parser.getDoubleValue());
                break;
            case "parametersMt31":
                instance.setParameters_mt31(parser.getDoubleValue());
                break;
            case "parametersMt32":
                instance.setParameters_mt32(parser.getDoubleValue());
                break;
            case "parametersMt33":
                instance.setParameters_mt33(parser.getDoubleValue());
                break;
            case "parametersMt34":
                instance.setParameters_mt34(parser.getDoubleValue());
                break;
            case "parametersNumAcceptedBeats":
                instance.setParameters_numacceptedbeats(parser.getIntValue());
                break;
            case "parametersNumAngles":
                instance.setParameters_numangles(parser.getDoubleValue());
                break;
            case "parametersNumGates":
                instance.setParameters_numgates(parser.getIntValue());
                break;
            case "parametersNmPlanes":
                instance.setParameters_numplanes(parser.getIntValue());
                break;
            case "parametersNumRelements":
                instance.setParameters_numrelements(parser.getDoubleValue());
                break;
            case "parametersOffsetX":
                instance.setParameters_offset_x(parser.getDoubleValue());
                break;
            case "parametersOffsetY":
                instance.setParameters_offset_y(parser.getDoubleValue());
                break;
            case "parametersOffsetZ":
                instance.setParameters_offset_z(parser.getDoubleValue());
                break;
            case "parametersOrientation":
                instance.setParameters_orientation(parser.getIntValue());
                break;
            case "parametersOriginalFileName":
                instance.setParameters_originalfilename(parser.getText());
                break;
            case "parametersPixelSizeX":
                instance.setParameters_pixelsize_x(parser.getDoubleValue());
                break;
            case "parametersPixelSizeY":
                instance.setParameters_pixelsize_y(parser.getDoubleValue());
                break;
            case "parametersPixelSizeZ":
                instance.setParameters_pixelsize_z(parser.getDoubleValue());
                break;
            case "parametersPlaneSeparation":
                instance.setParameters_planeseparation(parser.getDoubleValue());
                break;
            case "parametersProcessingCode":
                instance.setParameters_processingcode(parser.getIntValue());
                break;
            case "parametersReconType":
                instance.setParameters_recontype(parser.getIntValue());
                break;
            case "parameters_reconviews":
                instance.setParameters_reconviews(parser.getIntValue());
                break;
            case "parametersReconZoom":
                instance.setParameters_reconzoom(parser.getDoubleValue());
                break;
            case "parametersResolutionX":
                instance.setParameters_resolution_x(parser.getDoubleValue());
                break;
            case "parametersResolutionY":
                instance.setParameters_resolution_y(parser.getDoubleValue());
                break;
            case "parametersResolutionZ":
                instance.setParameters_resolution_z(parser.getDoubleValue());
                break;
            case "parametersRfilterCode":
                instance.setParameters_rfilter_code(parser.getIntValue());
                break;
            case "parameters_rfilter_cutoff":
                instance.setParameters_rfilter_cutoff(parser.getDoubleValue());
                break;
            case "parametersRfilterRrder":
                instance.setParameters_rfilter_order(parser.getIntValue());
                break;
            case "parametersRfilterResolution":
                instance.setParameters_rfilter_resolution(parser.getDoubleValue());
                break;
            case "parametersRwaveOffset":
                instance.setParameters_rwaveoffset(parser.getIntValue());
                break;
            case "parametersScatterType":
                instance.setParameters_scattertype(parser.getIntValue());
                break;
            case "parametersSystemType":
                instance.setParameters_systemtype(parser.getIntValue());
                break;
            case "parametersTransAxialFov":
                instance.setParameters_transaxialfov(parser.getDoubleValue());
                break;
            case "parametersZfilterCode":
                instance.setParameters_zfilter_code(parser.getIntValue());
                break;
            case "parametersZfilterCutoff":
                instance.setParameters_zfilter_cutoff(parser.getDoubleValue());
                break;
            case "parametersZfilterOrder":
                instance.setParameters_zfilter_order(parser.getIntValue());
                break;
            case "parametersZfilterResolution":
                instance.setParameters_zfilter_resolution(parser.getDoubleValue());
                break;
            case "parametersZrotationangle":
                instance.setParameters_zrotationangle(parser.getDoubleValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

