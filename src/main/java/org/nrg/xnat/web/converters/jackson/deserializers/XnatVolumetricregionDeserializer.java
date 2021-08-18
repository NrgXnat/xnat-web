package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatVolumetricregion;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatVolumetricregionDeserializer<T extends XnatVolumetricregion> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -3647134927877450346L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatVolumetricregionDeserializer() {
        this((Class<T>) XnatVolumetricregion.class);
    }

    protected XnatVolumetricregionDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "hemisphere":
                instance.setHemisphere(parser.getText());
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "subregions_subregion":
                // TODO: Handle the "subregions_subregion" property here: java.util.List
                break;
            case "units":
                instance.setUnits(parser.getText());
                break;
            case "voxels":
                instance.setVoxels(parser.getIntValue());
                break;
            case "xnatVolumetricregionId":
                instance.setXnatVolumetricregionId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

