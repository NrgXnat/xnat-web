package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatOptscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatOptscandataSerializer<T extends XnatOptscandata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -528931127356358261L;

    @SuppressWarnings("unchecked")
    public XnatOptscandataSerializer() {
        this((Class<T>) XnatOptscandata.class);
    }

    protected XnatOptscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "dcmvalidation" property here: String
        // TODO: Write out the "dcmvalidation_status" property here: Boolean
        // TODO: Write out the "imagescandata" property here: org.nrg.xdat.om.XnatImagescandata
        // TODO: Write out the "parameters_fov_x" property here: Integer
        // TODO: Write out the "parameters_fov_y" property here: Integer
        // TODO: Write out the "parameters_illuminationPower" property here: String
        // TODO: Write out the "parameters_illuminationWavelength" property here: String
        // TODO: Write out the "parameters_imagetype" property here: String
        // TODO: Write out the "parameters_laterality" property here: String
        // TODO: Write out the "parameters_voxelres_units" property here: String
        // TODO: Write out the "parameters_voxelres_x" property here: Double
        // TODO: Write out the "parameters_voxelres_y" property here: Double
        // TODO: Write out the "parameters_voxelres_z" property here: Double
        // TODO: Write out the "schemaElementName" property here: String
    }
}

