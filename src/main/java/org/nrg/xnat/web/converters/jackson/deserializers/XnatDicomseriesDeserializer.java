package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDicomseries;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatDicomseriesDeserializer<T extends XnatDicomseries> extends XnatAbstractresourceDeserializer<T> {
    private static final long serialVersionUID = 4647687366287043723L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatDicomseriesDeserializer() {
        this((Class<T>) XnatDicomseries.class);
    }

    protected XnatDicomseriesDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "cachepath":
                instance.setCachepath(parser.getText());
                break;
            case "content":
                instance.setContent(parser.getText());
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "dimensions_volumes":
                instance.setDimensions_volumes(parser.getIntValue());
                break;
            case "dimensions_x":
                instance.setDimensions_x(parser.getIntValue());
                break;
            case "dimensions_y":
                instance.setDimensions_y(parser.getIntValue());
                break;
            case "dimensions_z":
                instance.setDimensions_z(parser.getIntValue());
                break;
            case "format":
                instance.setFormat(parser.getText());
                break;
            case "imageset_image":
                // TODO: Handle the "imageset_image" property here: java.util.List
                break;
            case "orientation":
                instance.setOrientation(parser.getText());
                break;
            case "uid":
                instance.setUid(parser.getText());
                break;
            case "voxelres_units":
                instance.setVoxelres_units(parser.getText());
                break;
            case "voxelres_x":
                instance.setVoxelres_x(parser.getDoubleValue());
                break;
            case "voxelres_y":
                instance.setVoxelres_y(parser.getDoubleValue());
                break;
            case "voxelres_z":
                instance.setVoxelres_z(parser.getDoubleValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

