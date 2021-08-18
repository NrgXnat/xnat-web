package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatXascandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatXascandataDeserializer<T extends XnatXascandata> extends XnatImagescandataDeserializer<T> {
    private static final long serialVersionUID = -737853882173708047L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatXascandataDeserializer() {
        this((Class<T>) XnatXascandata.class);
    }

    protected XnatXascandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "parameters_contrastbolus":
                // TODO: Handle the "parameters_contrastbolus" property here: org.nrg.xdat.om.XnatContrastbolus
                break;
            case "parameters_derivation":
                instance.setParameters_derivation(parser.getText());
                break;
            case "parameters_fov_x":
                instance.setParameters_fov_x(parser.getIntValue());
                break;
            case "parameters_fov_y":
                instance.setParameters_fov_y(parser.getIntValue());
                break;
            case "parameters_imagetype":
                instance.setParameters_imagetype(parser.getText());
                break;
            case "parameters_options":
                instance.setParameters_options(parser.getText());
                break;
            case "parameters_orientation":
                instance.setParameters_orientation(parser.getText());
                break;
            case "parameters_pixelres_units":
                instance.setParameters_pixelres_units(parser.getText());
                break;
            case "parameters_pixelres_x":
                instance.setParameters_pixelres_x(parser.getIntValue());
                break;
            case "parameters_pixelres_y":
                instance.setParameters_pixelres_y(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

