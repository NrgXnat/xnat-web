package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.om.XnatContrastbolus;
import org.nrg.xdat.om.XnatXascandata;
import org.nrg.xft.ItemI;

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
            case "parametersContrastbolus":
                // TODO: Handle the "parameters_contrastbolus" property here: org.nrg.xdat.om.XnatContrastbolus
            	try {
            	instance.setParameters_contrastbolus((ItemI)parser.readValuesAs(XnatContrastbolus.class));
            	}catch (Exception e) {
            		log.error("Tried to set a field parameters_contrastbolus in XnatXascandata but failed", e);
				}
                break;
            case "parametersDerivation":
                instance.setParameters_derivation(parser.getText());
                break;
            case "parametersFovX":
                instance.setParameters_fov_x(parser.getIntValue());
                break;
            case "parametersFovY":
                instance.setParameters_fov_y(parser.getIntValue());
                break;
            case "parametersImagetype":
                instance.setParameters_imagetype(parser.getText());
                break;
            case "parametersOptions":
                instance.setParameters_options(parser.getText());
                break;
            case "parametersOrientation":
                instance.setParameters_orientation(parser.getText());
                break;
            case "parametersPixelresUnits":
                instance.setParameters_pixelres_units(parser.getText());
                break;
            case "parametersPixelResX":
                instance.setParameters_pixelres_x(parser.getIntValue());
                break;
            case "parametersPixelResY":
                instance.setParameters_pixelres_y(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

