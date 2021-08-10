package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatCtscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatCtscandataDeserializer<T extends XnatCtscandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = 3228933791844472050L;

    @SuppressWarnings("unchecked")
    public XnatCtscandataDeserializer() {
        this((Class<T>) XnatCtscandata.class);
    }

    protected XnatCtscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "dcmValidation":
                instance.setDcmvalidation(parser.getText());
                break;
            case "dcmValidationStatus":
                instance.setDcmvalidation_status(parser.getBooleanValue());
                break;
            case "parameters":
                /*
                Handle all CT scan-specific parameters:

                voxelRes
                    units
                    x
                    y
                    z
                orientation
                subjectPosition
                fov
                    x
                    y
                rescale
                    intercept
                    slope
                kvp
                acquisitionNumber
                imageType
                options
                collectionDiameter
                distanceSourceToDetector
                distanceSourceToPatient
                gantryTilt
                tableHeight
                rotationDirection
                exposureTime
                xrayTubeCurrent
                exposure
                filter
                generatorPower
                focalSpots
                    focalSpot
                convolutionKernel
                collimationWidth
                    single
                    total
                tableSpeed
                tableFeedPerRotation
                pitchFactor
                estimatedDoseSaving
                    modulation
                ctDIvol
                derivation
                contrastBolus
                */
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
