package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatVolumetricregionSubregion;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatVolumetricregionSubregionDeserializer<T extends XnatVolumetricregionSubregion> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 6036484568018061688L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatVolumetricregionSubregionDeserializer() {
        this((Class<T>) XnatVolumetricregionSubregion.class);
    }

    protected XnatVolumetricregionSubregionDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "name":
                instance.setName(parser.getText());
                break;
            case "voxels":
                instance.setVoxels(parser.getDoubleValue());
                break;
            case "xnatVolumetricregionSubregionId":
                instance.setXnatVolumetricregionSubregionId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

