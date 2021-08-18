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
            case "ecatvalidation":
                instance.setEcatvalidation(parser.getText());
                break;
            case "ecatvalidation_status":
                instance.setEcatvalidation_status(parser.getBooleanValue());
                break;
            case "parameters_acqtype":
                instance.setParameters_acqtype(parser.getIntValue());
                break;
            case "parameters_addparam":
                // TODO: Handle the "parameters_addparam" property here: java.util.List
                break;
            case "parameters_annotation":
                instance.setParameters_annotation(parser.getText());
                break;
            case "parameters_bedposition":
                instance.setParameters_bedposition(parser.getDoubleValue());
                break;
            case "parameters_binsize":
                instance.setParameters_binsize(parser.getDoubleValue());
                break;
            case "parameters_datatype":
                instance.setParameters_datatype(parser.getIntValue());
                break;
            case "parameters_dimensions_num":
                instance.setParameters_dimensions_num(parser.getIntValue());
                break;
            case "parameters_dimensions_x":
                instance.setParameters_dimensions_x(parser.getIntValue());
                break;
            case "parameters_dimensions_y":
                instance.setParameters_dimensions_y(parser.getIntValue());
                break;
            case "parameters_dimensions_z":
                instance.setParameters_dimensions_z(parser.getIntValue());
                break;
            case "parameters_ecatcalibrationfactor":
                instance.setParameters_ecatcalibrationfactor(parser.getDoubleValue());
                break;
            case "parameters_facility":
                instance.setParameters_facility(parser.getText());
                break;
            case "parameters_filetype":
                instance.setParameters_filetype(parser.getIntValue());
                break;
            case "parameters_filter_cutoff":
                instance.setParameters_filter_cutoff(parser.getDoubleValue());
                break;
            case "parameters_filtercode":
                instance.setParameters_filtercode(parser.getIntValue());
                break;
            case "parameters_frames_frame":
                // TODO: Handle the "parameters_frames_frame" property here: java.util.List
                break;
            case "parameters_frames_numframes":
                // TODO: Handle the "parameters_frames_numframes" property here: Object
                break;
            case "parameters_gateduration":
                instance.setParameters_gateduration(parser.getIntValue());
                break;
            case "parameters_mt11":
                instance.setParameters_mt11(parser.getDoubleValue());
                break;
            case "parameters_mt12":
                instance.setParameters_mt12(parser.getDoubleValue());
                break;
            case "parameters_mt13":
                instance.setParameters_mt13(parser.getDoubleValue());
                break;
            case "parameters_mt14":
                instance.setParameters_mt14(parser.getDoubleValue());
                break;
            case "parameters_mt21":
                instance.setParameters_mt21(parser.getDoubleValue());
                break;
            case "parameters_mt22":
                instance.setParameters_mt22(parser.getDoubleValue());
                break;
            case "parameters_mt23":
                instance.setParameters_mt23(parser.getDoubleValue());
                break;
            case "parameters_mt24":
                instance.setParameters_mt24(parser.getDoubleValue());
                break;
            case "parameters_mt31":
                instance.setParameters_mt31(parser.getDoubleValue());
                break;
            case "parameters_mt32":
                instance.setParameters_mt32(parser.getDoubleValue());
                break;
            case "parameters_mt33":
                instance.setParameters_mt33(parser.getDoubleValue());
                break;
            case "parameters_mt34":
                instance.setParameters_mt34(parser.getDoubleValue());
                break;
            case "parameters_numacceptedbeats":
                instance.setParameters_numacceptedbeats(parser.getIntValue());
                break;
            case "parameters_numangles":
                instance.setParameters_numangles(parser.getDoubleValue());
                break;
            case "parameters_numgates":
                instance.setParameters_numgates(parser.getIntValue());
                break;
            case "parameters_numplanes":
                instance.setParameters_numplanes(parser.getIntValue());
                break;
            case "parameters_numrelements":
                instance.setParameters_numrelements(parser.getDoubleValue());
                break;
            case "parameters_offset_x":
                instance.setParameters_offset_x(parser.getDoubleValue());
                break;
            case "parameters_offset_y":
                instance.setParameters_offset_y(parser.getDoubleValue());
                break;
            case "parameters_offset_z":
                instance.setParameters_offset_z(parser.getDoubleValue());
                break;
            case "parameters_orientation":
                instance.setParameters_orientation(parser.getIntValue());
                break;
            case "parameters_originalfilename":
                instance.setParameters_originalfilename(parser.getText());
                break;
            case "parameters_pixelsize_x":
                instance.setParameters_pixelsize_x(parser.getDoubleValue());
                break;
            case "parameters_pixelsize_y":
                instance.setParameters_pixelsize_y(parser.getDoubleValue());
                break;
            case "parameters_pixelsize_z":
                instance.setParameters_pixelsize_z(parser.getDoubleValue());
                break;
            case "parameters_planeseparation":
                instance.setParameters_planeseparation(parser.getDoubleValue());
                break;
            case "parameters_processingcode":
                instance.setParameters_processingcode(parser.getIntValue());
                break;
            case "parameters_recontype":
                instance.setParameters_recontype(parser.getIntValue());
                break;
            case "parameters_reconviews":
                instance.setParameters_reconviews(parser.getIntValue());
                break;
            case "parameters_reconzoom":
                instance.setParameters_reconzoom(parser.getDoubleValue());
                break;
            case "parameters_resolution_x":
                instance.setParameters_resolution_x(parser.getDoubleValue());
                break;
            case "parameters_resolution_y":
                instance.setParameters_resolution_y(parser.getDoubleValue());
                break;
            case "parameters_resolution_z":
                instance.setParameters_resolution_z(parser.getDoubleValue());
                break;
            case "parameters_rfilter_code":
                instance.setParameters_rfilter_code(parser.getIntValue());
                break;
            case "parameters_rfilter_cutoff":
                instance.setParameters_rfilter_cutoff(parser.getDoubleValue());
                break;
            case "parameters_rfilter_order":
                instance.setParameters_rfilter_order(parser.getIntValue());
                break;
            case "parameters_rfilter_resolution":
                instance.setParameters_rfilter_resolution(parser.getDoubleValue());
                break;
            case "parameters_rwaveoffset":
                instance.setParameters_rwaveoffset(parser.getIntValue());
                break;
            case "parameters_scattertype":
                instance.setParameters_scattertype(parser.getIntValue());
                break;
            case "parameters_systemtype":
                instance.setParameters_systemtype(parser.getIntValue());
                break;
            case "parameters_transaxialfov":
                instance.setParameters_transaxialfov(parser.getDoubleValue());
                break;
            case "parameters_zfilter_code":
                instance.setParameters_zfilter_code(parser.getIntValue());
                break;
            case "parameters_zfilter_cutoff":
                instance.setParameters_zfilter_cutoff(parser.getDoubleValue());
                break;
            case "parameters_zfilter_order":
                instance.setParameters_zfilter_order(parser.getIntValue());
                break;
            case "parameters_zfilter_resolution":
                instance.setParameters_zfilter_resolution(parser.getDoubleValue());
                break;
            case "parameters_zrotationangle":
                instance.setParameters_zrotationangle(parser.getDoubleValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

