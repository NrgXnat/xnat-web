package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatImageresourceseries;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatImageresourceseriesDeserializer<T extends XnatImageresourceseries> extends XnatResourceseriesDeserializer<T> {
    private static final long serialVersionUID = -3162645879985788452L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatImageresourceseriesDeserializer() {
        this((Class<T>) XnatImageresourceseries.class);
    }

    protected XnatImageresourceseriesDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
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
            case "orientation":
                instance.setOrientation(parser.getText());
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

