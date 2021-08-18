package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatXascandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatXascandataSerializer<T extends XnatXascandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = -7410095958518300692L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatXascandataSerializer() {
        this((Class<T>) XnatXascandata.class);
    }

    protected XnatXascandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "parameters_contrastbolus" property here: org.nrg.xdat.om.XnatContrastbolus
        writeNonBlankField(generator, "parameters_derivation", instance.getParameters_derivation());
        writeNonNullNumber(generator, "parameters_fov_x", instance.getParameters_fov_x());
        writeNonNullNumber(generator, "parameters_fov_y", instance.getParameters_fov_y());
        writeNonBlankField(generator, "parameters_imagetype", instance.getParameters_imagetype());
        writeNonBlankField(generator, "parameters_options", instance.getParameters_options());
        writeNonBlankField(generator, "parameters_orientation", instance.getParameters_orientation());
        writeNonBlankField(generator, "parameters_pixelres_units", instance.getParameters_pixelres_units());
        writeNonNullNumber(generator, "parameters_pixelres_x", instance.getParameters_pixelres_x());
        writeNonNullNumber(generator, "parameters_pixelres_y", instance.getParameters_pixelres_y());
        super.serializeImpl(instance, generator, provider);
    }
}

