package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatOptscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatOptscandataSerializer<T extends XnatOptscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = 191574520396854647L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatOptscandataSerializer() {
        this((Class<T>) XnatOptscandata.class);
    }

    protected XnatOptscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "dcmValidation", instance.getDcmvalidation());
        writeNonNullBoolean(generator, "dcmValidationStatus", instance.getDcmvalidation_status());
        writeNonNullNumber(generator, "parametersFovX", instance.getParameters_fov_x());
        writeNonNullNumber(generator, "parametersFovY", instance.getParameters_fov_y());
        writeNonBlankField(generator, "parametersIlluminationPower", instance.getParameters_illuminationPower());
        writeNonBlankField(generator, "parametersIlluminationWaveLength", instance.getParameters_illuminationWavelength());
        writeNonBlankField(generator, "parametersImageType", instance.getParameters_imagetype());
        writeNonBlankField(generator, "parametersLaterality", instance.getParameters_laterality());
        writeNonBlankField(generator, "parametersVoxelResUnits", instance.getParameters_voxelres_units());
        writeNonNullNumber(generator, "parametersVoxelResX", instance.getParameters_voxelres_x());
        writeNonNullNumber(generator, "parametersVoxelResY", instance.getParameters_voxelres_y());
        writeNonNullNumber(generator, "parametersVoxelResZ", instance.getParameters_voxelres_z());
        super.serializeImpl(instance, generator, provider);
    }
}

