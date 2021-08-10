package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatXascandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatXascandataSerializer<T extends XnatXascandata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 7241077284273024818L;

    @SuppressWarnings("unchecked")
    public XnatXascandataSerializer() {
        this((Class<T>) XnatXascandata.class);
    }

    protected XnatXascandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "imagescandata" property here: org.nrg.xdat.om.XnatImagescandata
        // TODO: Write out the "parameters_contrastbolus" property here: org.nrg.xdat.model.XnatContrastbolusI
        // TODO: Write out the "parameters_derivation" property here: String
        // TODO: Write out the "parameters_fov_x" property here: Integer
        // TODO: Write out the "parameters_fov_y" property here: Integer
        // TODO: Write out the "parameters_imagetype" property here: String
        // TODO: Write out the "parameters_options" property here: String
        // TODO: Write out the "parameters_orientation" property here: String
        // TODO: Write out the "parameters_pixelres_units" property here: String
        // TODO: Write out the "parameters_pixelres_x" property here: Integer
        // TODO: Write out the "parameters_pixelres_y" property here: Integer
        // TODO: Write out the "schemaElementName" property here: String
    }
}

