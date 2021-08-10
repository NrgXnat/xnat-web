package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.model.XnatCtscandataFocalspotI;
import org.nrg.xdat.om.XnatContrastbolus;
import org.nrg.xdat.om.XnatCtscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class XnatCtscandataSerializer<T extends XnatCtscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = 5325245843724733448L;

    @SuppressWarnings("unchecked")
    public XnatCtscandataSerializer() {
        this((Class<T>) XnatCtscandata.class);
    }

    protected XnatCtscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T scan, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(scan, generator, provider);
        generator.writeObjectFieldStart("parameters");
        writeVoxelRes(generator, scan.getParameters_voxelres_x(), scan.getParameters_voxelres_y(), scan.getParameters_voxelres_z(), scan.getParameters_voxelres_units());
        writeNonBlankField(generator, "orientation", scan.getParameters_orientation());
        writeNonBlankField(generator, "subjectPosition", scan.getParameters_subjectposition());
        writeFov(generator, scan.getParameters_fov_x(), scan.getParameters_fov_y());
        final String slope     = scan.getParameters_rescale_slope();
        final String intercept = scan.getParameters_rescale_intercept();
        if (!StringUtils.isAnyBlank(slope, intercept)) {
            generator.writeObjectFieldStart("rescale");
            writeNonBlankField(generator, "slope", slope);
            writeNonBlankField(generator, "intercept", intercept);
            generator.writeEndObject();
        }
        writeNonNullNumber(generator, "kvp", scan.getParameters_kvp());
        writeNonNullNumber(generator, "acquisitionNumber", scan.getParameters_acquisitionnumber());
        writeNonBlankField(generator, "imageType", scan.getParameters_imagetype());
        writeNonBlankField(generator, "options", scan.getParameters_options());
        writeNonNullNumber(generator, "collectionDiameter", scan.getParameters_collectiondiameter());
        writeNonNullNumber(generator, "distanceSourceToDetector", scan.getParameters_distancesourcetodetector());
        writeNonNullNumber(generator, "distanceSourceToPatient", scan.getParameters_distancesourcetopatient());
        writeNonNullNumber(generator, "gantryTilt", scan.getParameters_gantrytilt());
        writeNonNullNumber(generator, "tableHeight", scan.getParameters_tableheight());
        writeNonBlankField(generator, "rotationDirection", scan.getParameters_rotationdirection());
        writeNonNullNumber(generator, "exposureTime", scan.getParameters_exposuretime());
        writeNonNullNumber(generator, "xrayTubeCurrent", scan.getParameters_xraytubecurrent());
        writeNonNullNumber(generator, "exposure", scan.getParameters_exposure());
        writeNonBlankField(generator, "filter", scan.getParameters_filter());
        writeNonNullNumber(generator, "generatorPower", scan.getParameters_generatorpower());
        final List<XnatCtscandataFocalspotI> focalSpots = scan.getParameters_focalspots_focalspot();
        if (focalSpots != null && !focalSpots.isEmpty()) {
            generator.writeArrayFieldStart("focalSpots");
            for (final XnatCtscandataFocalspotI focalSpot : focalSpots) {
                generator.writeObject(focalSpot);
            }
            generator.writeEndArray();
        }
        final Double single = scan.getParameters_collimationwidth_single();
        final Double total  = scan.getParameters_collimationwidth_total();
        if (ObjectUtils.allNotNull(single, total)) {
            generator.writeObjectFieldStart("rescale");
            generator.writeNumberField("single", single);
            generator.writeNumberField("total", total);
            generator.writeEndObject();
        }

        writeNonNullNumber(generator, "tableSpeed", scan.getParameters_tablespeed());
        writeNonNullNumber(generator, "tableFeedPerRotation", scan.getParameters_tablefeedperrotation());
        writeNonNullNumber(generator, "pitchFactor", scan.getParameters_pitchfactor());
        final Double estimatedDoseSaving           = scan.getParameters_estimateddosesaving();
        final String estimatedDoseSavingModulation = scan.getParameters_estimateddosesaving_modulation();
        if (ObjectUtils.anyNotNull(estimatedDoseSaving, estimatedDoseSavingModulation)) {
            generator.writeObjectFieldStart("estimatedDoseSaving");
            writeNonNullNumber(generator, "value", estimatedDoseSaving);
            writeNonBlankField(generator, "modulation", estimatedDoseSavingModulation);
            generator.writeEndObject();
        }
        writeNonNullNumber(generator, "pitchFactor", scan.getParameters_pitchfactor());
        writeNonNullNumber(generator, "ctdiVol", scan.getParameters_ctdivol());
        writeNonBlankField(generator, "derivation", scan.getParameters_derivation());
        final XnatContrastbolus contrastBolus = scan.getParameters_contrastbolus();
        if (contrastBolus != null) {
            generator.writeObjectField("contrastBolus", contrastBolus);
        }
        generator.writeEndObject(); // Ends parameters
        final String  validation = scan.getDcmvalidation();
        final Boolean status     = scan.getDcmvalidation_status();
        writeDcmValidation(generator, validation, status);
    }
}
