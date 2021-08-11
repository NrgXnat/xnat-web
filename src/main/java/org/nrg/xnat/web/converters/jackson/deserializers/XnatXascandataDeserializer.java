package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatXascandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatXascandataDeserializer<T extends XnatXascandata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 4554216397993426820L;

    @SuppressWarnings("unchecked")
    public XnatXascandataDeserializer() {
        this((Class<T>) XnatXascandata.class);
    }

    public XnatXascandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "imagescandata":
                // TODO: Handle the "imagescandata" property here: org.nrg.xdat.om.XnatImagescandata
                break;
            case "parameters_contrastbolus":
                // TODO: Handle the "parameters_contrastbolus" property here: org.nrg.xdat.model.XnatContrastbolusI
                break;
            case "parameters_derivation":
                // TODO: Handle the "parameters_derivation" property here: String
                break;
            case "parameters_fov_x":
                // TODO: Handle the "parameters_fov_x" property here: Integer
                break;
            case "parameters_fov_y":
                // TODO: Handle the "parameters_fov_y" property here: Integer
                break;
            case "parameters_imagetype":
                // TODO: Handle the "parameters_imagetype" property here: String
                break;
            case "parameters_options":
                // TODO: Handle the "parameters_options" property here: String
                break;
            case "parameters_orientation":
                // TODO: Handle the "parameters_orientation" property here: String
                break;
            case "parameters_pixelres_units":
                // TODO: Handle the "parameters_pixelres_units" property here: String
                break;
            case "parameters_pixelres_x":
                // TODO: Handle the "parameters_pixelres_x" property here: Integer
                break;
            case "parameters_pixelres_y":
                // TODO: Handle the "parameters_pixelres_y" property here: Integer
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

