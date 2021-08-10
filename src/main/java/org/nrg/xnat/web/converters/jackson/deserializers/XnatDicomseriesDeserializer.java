package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDicomseries;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatDicomseriesDeserializer<T extends XnatDicomseries> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 310111835470118214L;

    @SuppressWarnings("unchecked")
    public XnatDicomseriesDeserializer() {
        this((Class<T>) XnatDicomseries.class);
    }

    public XnatDicomseriesDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "abstractresource":
                // TODO: Handle the "abstractresource" property here: org.nrg.xdat.om.XnatAbstractresource
                break;
            case "cachepath":
                // TODO: Handle the "cachepath" property here: String
                break;
            case "content":
                // TODO: Handle the "content" property here: String
                break;
            case "description":
                // TODO: Handle the "description" property here: String
                break;
            case "dimensions_volumes":
                // TODO: Handle the "dimensions_volumes" property here: Integer
                break;
            case "dimensions_x":
                // TODO: Handle the "dimensions_x" property here: Integer
                break;
            case "dimensions_y":
                // TODO: Handle the "dimensions_y" property here: Integer
                break;
            case "dimensions_z":
                // TODO: Handle the "dimensions_z" property here: Integer
                break;
            case "format":
                // TODO: Handle the "format" property here: String
                break;
            case "imageset_image":
                // TODO: Handle the "imageset_image" property here: java.util.List
                break;
            case "label":
                // TODO: Handle the "label" property here: String
                break;
            case "orientation":
                // TODO: Handle the "orientation" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "unresolvedPaths":
                // TODO: Handle the "unresolvedPaths" property here: java.util.ArrayList
                break;
            case "voxelres_units":
                // TODO: Handle the "voxelres_units" property here: String
                break;
            case "voxelres_x":
                // TODO: Handle the "voxelres_x" property here: Double
                break;
            case "voxelres_y":
                // TODO: Handle the "voxelres_y" property here: Double
                break;
            case "voxelres_z":
                // TODO: Handle the "voxelres_z" property here: Double
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

