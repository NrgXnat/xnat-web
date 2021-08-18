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
        writeNonBlankField(generator, "dcmvalidation", instance.getDcmvalidation());
        writeNonNullBoolean(generator, "dcmvalidation_status", instance.getDcmvalidation_status());
        writeNonNullNumber(generator, "parameters_fov_x", instance.getParameters_fov_x());
        writeNonNullNumber(generator, "parameters_fov_y", instance.getParameters_fov_y());
        writeNonBlankField(generator, "parameters_illuminationPower", instance.getParameters_illuminationPower());
        writeNonBlankField(generator, "parameters_illuminationWavelength", instance.getParameters_illuminationWavelength());
        writeNonBlankField(generator, "parameters_imagetype", instance.getParameters_imagetype());
        writeNonBlankField(generator, "parameters_laterality", instance.getParameters_laterality());
        writeNonBlankField(generator, "parameters_voxelres_units", instance.getParameters_voxelres_units());
        writeNonNullNumber(generator, "parameters_voxelres_x", instance.getParameters_voxelres_x());
        writeNonNullNumber(generator, "parameters_voxelres_y", instance.getParameters_voxelres_y());
        writeNonNullNumber(generator, "parameters_voxelres_z", instance.getParameters_voxelres_z());
        super.serializeImpl(instance, generator, provider);
    }
}

